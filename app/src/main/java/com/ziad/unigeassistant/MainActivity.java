package com.ziad.unigeassistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ziad.unigeassistant.Classes.Category;
import com.ziad.unigeassistant.Classes.Category_rank;
import com.ziad.unigeassistant.Classes.Chat;
import com.ziad.unigeassistant.Classes.Question;
import com.ziad.unigeassistant.Handlers.BusHolder;
import com.ziad.unigeassistant.Handlers.DB_Handler;
import com.ziad.unigeassistant.Handlers.Zutil;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    public static final String tag = "ziad";
    public static String user_id;
    DB_Handler mydb = new DB_Handler(this, null);
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    public static boolean active = false;
    OkHttpClient mClient = new OkHttpClient();
    public static Map<String, String> normal_list;

    public static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    final MediaType JSON = MediaType.parse("application/json");

    public final String PREFS_NAME = "MyPrefsFile";
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    Thread update_thread;
    boolean is_update = false;

    RecyclerView rv_chats_list;
    public static Chat_Adapter chat_adapter;
    EditText et_input;
    Button but_enter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseMessaging.getInstance().subscribeToTopic("bots")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "1111";
                        if (!task.isSuccessful()) {
                            msg = "fff";
                        }
                    }
                });
        Update_token();

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        rv_chats_list = findViewById(R.id.rv_chats_list);
        et_input = findViewById(R.id.et_input);
        but_enter = findViewById(R.id.but_enter);

        normal_list = read_data("normal.json");
        ArrayList<Chat> item_list = mydb.get_All_chats();
        chat_adapter = new Chat_Adapter(this, item_list);
        rv_chats_list.setAdapter(chat_adapter);
        rv_chats_list.setLayoutManager(new LinearLayoutManager(this));
        rv_chats_list.scrollToPosition(chat_adapter.items.size() - 1);
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        user_id = sharedPreferences.getString("user_id", "");

        update_thread = new Thread("New Thread") {
            public void run() {
                while (is_update) {
                    try {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                check_new_questions();
                                chat_adapter.items = mydb.get_All_chats();
                                chat_adapter.notifyDataSetChanged();
                            }
                        });
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        is_update = true;
        //update_thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        is_update = false;
    }


    @Override
    public void onStart() {
        super.onStart();
        is_update = true;
        update_thread.start();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    //----------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.basic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear_chats: {
                mydb.del_chats();
                return true;
            }
            case R.id.basic_logout: {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Logout")
                        .setMessage("If you Logout all chats will be deleted")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mydb.del_chats();
                                editor.putString("login_successful", null);
                                editor.putString("user_id", null);
                                editor.putString("user_name", null);
                                editor.putString("bot_name", null);
                                editor.commit();
                                Intent intent = new Intent(getApplicationContext(), LoinActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                builder.create();
                builder.show();

                return true;
            }

        }
        return super.onOptionsItemSelected(item);
    }


    //---------------------------------------------------------------------------------------------
    public void check_new_questions() {
        ArrayList<Question> unseen_questions = mydb.get_unseen_questions();
        if (unseen_questions != null) {
            for (Question q : unseen_questions) {
                Chat chat = new Chat();
                chat.sender = "bot";//q.target;
                chat.chat_message = "you asked me :" + q.question + "\n " + q.answer;
                chat.datetime = Zutil.dateFormat.format(new Date());
                mydb.add_chat_message(chat);
                q.status = "answered";
                mydb.update_question(q);
            }
        }
    }

    public void Update_token() {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.i("ziad", "token  =   " + token);

    }

    public void enter_input_click(View v) {
        String message = et_input.getText().toString().trim();
        if (!message.isEmpty()) {
            Chat chat = new Chat();
            chat.sender = "user";
            chat.chat_message = message;
            mydb.add_chat_message(chat);
            for (Chat res : get_bot_chat_response(chat)) {
                if (res != null && !res.chat_message.isEmpty())
                    mydb.add_chat_message(res);
            }
            chat_adapter.items = mydb.get_All_chats();
            chat_adapter.notifyDataSetChanged();
            rv_chats_list.scrollToPosition(chat_adapter.items.size() - 1);
            et_input.setText("");
        }
    }

    public ArrayList<Chat> get_bot_chat_response(Chat chat) {
        ArrayList<Chat> list = new ArrayList<>();

        String normalized = normalize(chat.chat_message);
        String[] sentences = sentence_breaking(normalized);
        for (String sentence : sentences) {
            Chat bot_chat = new Chat();
            bot_chat.chat_message = find_matching(sentence);
            bot_chat.sender = "bot";
            list.add(bot_chat);
        }

        return list;
    }

    public String normalize(String sentence) {
     /*   String[] tokens= sentence.trim().split("\\s+");
        String res="";
        for (String str:tokens)
        {
            String replace=normal_list.get(str);
            if(replace!=null && !replace.isEmpty())
                res=res+replace+" ";
            else
                res=res+str+" ";

        }
*/
        for (Map.Entry<String, String> pair : normal_list.entrySet()) {
            if (sentence.contains(pair.getKey()))
            {
                int i =sentence.indexOf(pair.getKey());
                if (i>0 )
                {
                    char s =sentence.charAt(i-1);
                    if (s==' ')
                    {

                    }
                }
                else
                    sentence = sentence.replace(pair.getKey(), pair.getValue());
            }

        }
        return sentence.trim();
    }

    public String[] sentence_breaking(String sentences) {
        String[] tokens = sentences.split("!|\\?|\\.");
        return tokens;
    }

    public String find_matching(String sentence) {
        Category match = find_category_matching(sentence);
        JSONObject data = new JSONObject();
        if (match != null) {
            try {
                switch (match.type) {
                    case "classes_today": {
                        data.put("function", "news");
                        data.put("ask", "classes_today");
                        data.put("time", dateFormat.format(new Date()));
                        data.put("user_id", user_id);
                        data.put("question_id", add_question(sentence, "server"));
                        send_server_Message(data);
                        break;
                    }
                    case "classes_tomorrow": {
                        data.put("function", "news");
                        data.put("ask", "classes_tomorrow");
                        data.put("time", dateFormat.format(new Date()));
                        data.put("user_id", user_id);
                        data.put("question_id", add_question(sentence, "server"));
                        send_server_Message(data);
                        break;
                    }
                    case "events_today": {
                        data.put("function", "news");
                        data.put("ask", "events_today");
                        data.put("time", dateFormat.format(new Date()));
                        data.put("user_id", user_id);
                        data.put("question_id", add_question(sentence, "server"));
                        send_server_Message(data);
                        break;
                    }
                    case "events_tomorrow": {
                        data.put("function", "news");
                        data.put("ask", "events_tomorrow");
                        data.put("time", dateFormat.format(new Date()));
                        data.put("user_id", user_id);
                        data.put("question_id", add_question(sentence, "server"));
                        send_server_Message(data);
                        break;
                    }
                    case "next_class": {
                        data.put("function", "news");
                        data.put("ask", "next_class");
                        data.put("time", dateFormat.format(new Date()));
                        data.put("user_id", user_id);
                        data.put("question_id", add_question(sentence, "server"));
                        send_server_Message(data);
                        break;
                    }
                    case "next_event": {
                        data.put("function", "news");
                        data.put("ask", "next_event");
                        data.put("time", dateFormat.format(new Date()));
                        data.put("user_id", user_id);
                        data.put("question_id", add_question(sentence, "server"));
                        send_server_Message(data);
                        break;
                    }

                    case "user_name": {
                        String name= sentence.substring(11);
                        editor.putString("user_name", name);
                        editor.commit();
                        match.template+=" "+name;
                        break;
                    }
                    case "bot_name": {
                        String name= sentence.substring(13);
                        editor.putString("bot_name", name);
                        editor.commit();
                        match.template=name+"! "+match.template;
                        break;
                    }
                    case "user_name_answer": {
                        String  user_name =sharedPreferences.getString("user_name", "");
                        if (user_name!=null && !user_name.isEmpty() )
                            match.template = "your name is "+user_name;
                        else
                            match.template = "sorry i do't know";
                        break;
                    }

                    case "bot_name_answer": {
                        String  bot_name =sharedPreferences.getString("bot_name", "");
                        if (bot_name!=null && !bot_name.isEmpty() )
                            match.template = "my name is "+bot_name;
                        else
                            match.template = "i don't have it yet";
                        break;
                    }
                    case "greetings": {
                        String  user_name =sharedPreferences.getString("user_name", "");
                        if (user_name!=null && !user_name.isEmpty() )
                            match.template = match.template +user_name;
                        break;
                    }
                }
            } catch (Exception e) {

            }

            return match.template;
        } else {
            show_options_dialog(sentence);
            return "";
        }
    }

    public void show_options_dialog(final String sentence) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.options_dialog);
        dialog.setTitle("Unknown Question");
        dialog.setCancelable(true);
        Button but_learn = dialog.findViewById(R.id.but_learn);
        Button but_ask_bots = dialog.findViewById(R.id.but_ask_bots);
        Button but_submit_to_server = dialog.findViewById(R.id.but_submit_to_server);
        Button but_do_nothing = dialog.findViewById(R.id.but_do_nothing);


        but_learn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                final Intent intent = new Intent(getApplicationContext(), LearnQuestion.class);
                intent.putExtra("question", sentence);
                startActivity(intent);
                finish();

            }
        });
        but_ask_bots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject data = new JSONObject();
                try {
                    String token = FirebaseInstanceId.getInstance().getToken();
                    data.put("function", "unknown_question");
                    data.put("user_id", user_id);
                    data.put("token", token);
                    data.put("question", sentence);
                    data.put("question_id", add_question(sentence, "bots"));
                    send_bots_Message(data);
                } catch (Exception e) {
                }
                dialog.cancel();
            }
        });
        but_submit_to_server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject data = new JSONObject();
                try {
                    String token = FirebaseInstanceId.getInstance().getToken();
                    data.put("function", "unknown_question");
                    data.put("user_id", user_id);
                    data.put("token", token);
                    data.put("question", sentence);
                    data.put("question_id", add_question(sentence, "server"));
                    send_server_Message(data);
                } catch (Exception e) {
                }
                dialog.cancel();
            }
        });

        but_do_nothing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        dialog.show();
    }


    public long add_question(String sentence, String target) {
        Question question = new Question();
        question.question = sentence;
        question.answer_datetime = dateFormat.format(new Date());
        question.status = "not_answered";
        question.target = target;
        long id = mydb.add_question(question);
        return id;
    }

    public Category find_category_matching(String sentence) {
        Category category = mydb.match_category(sentence);
        if (category != null) {
            return category;
        } else {
            Category_rank res1 = find_matching_star(sentence);
            Category_rank res2 = find_star_matching(sentence);

            if (res2.rank > res1.rank)
                return res2.category;
            else
                return res1.category;
        }
    }

    public Category_rank find_star_matching(String sentence) {
        String[] tokens = sentence.trim().split("\\s+");
        if (tokens.length > 1) {

            String res = star_sentence(tokens);
            Category star_category = mydb.match_category("* " + res);
            if (star_category != null) {
                return new Category_rank(star_category, tokens.length - 1);
            } else
                return find_star_matching(res);
        } else
            return new Category_rank(null, 0);
    }

    public Category_rank find_matching_star(String sentence) {
        String[] tokens = sentence.trim().split("\\s+");
        if (tokens.length > 1) {
            String res = sentence_star(tokens);
            Category category_star = mydb.match_category(res + " *");
            if (category_star != null) {
                return new Category_rank(category_star, tokens.length - 1);
            } else
                return find_matching_star(res);
        } else
            return new Category_rank(null, 0);
    }

    public String star_sentence(String[] tokens) {
        String res = "";
        for (int i = 1; i < tokens.length; i++)
            res += tokens[i] + " ";
        return res.trim();
    }

    public String sentence_star(String[] tokens) {
        String res = "";
        for (int i = 0; i < tokens.length - 1; i++)
            res += tokens[i] + " ";
        return res.trim();
    }

    public void send_server_Message(final JSONObject data) {

        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    JSONObject root = new JSONObject();
                    root.put("data", data);
                    root.put("to", "/topics/server");

                    String result = postToFCM_server(root.toString());
                    Log.i("ziad", "Result: " + result);
                    return result;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                try {
                    JSONObject resultJson = new JSONObject(result);
                    long message_id, failure;
                    message_id = resultJson.getLong("message_id");
                    //failure = resultJson.getInt("failure");
                    // Toast.makeText(getApplicationContext(), "Evaluation sent successfully", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    //  e.printStackTrace();
                    // Toast.makeText(getApplicationContext(), "Message failed", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    String postToFCM_server(String bodyString) throws IOException {
        RequestBody body = RequestBody.create(JSON, bodyString);
        Request request = new Request.Builder()
                .url(FCM_MESSAGE_URL)
                .post(body)
                .addHeader("Authorization", "key=" + "******")
                .build();
        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }

    public void send_bots_Message(final JSONObject data) {

        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    JSONObject root = new JSONObject();
                    root.put("data", data);
                    root.put("to", "/topics/bots");

                    String result = postToFCM_bots(root.toString());
                    Log.i("ziad", "Result: " + result);
                    return result;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                try {
                    JSONObject resultJson = new JSONObject(result);
                    long message_id, failure;
                    message_id = resultJson.getLong("message_id");
                    //failure = resultJson.getInt("failure");
                    // Toast.makeText(getApplicationContext(), "Evaluation sent successfully", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    //  e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "Message failed", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    String postToFCM_bots(String bodyString) throws IOException {
        RequestBody body = RequestBody.create(JSON, bodyString);
        Request request = new Request.Builder()
                .url(FCM_MESSAGE_URL)
                .post(body)
                .addHeader("Authorization", "key=" + "*******")
                .build();
        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }


    public class Chat_Adapter extends RecyclerView.Adapter<Chat_Adapter.ViewHolder> {
        Context context;
        LayoutInflater inflater;
        public ArrayList<Chat> items;

        public Chat_Adapter(Context context, ArrayList<Chat> items) {
            this.context = context;
            this.inflater = LayoutInflater.from(context);
            this.items = items;
        }

        @NonNull
        @Override
        public Chat_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            View view;
            if (i == 1) {
                view = inflater.inflate(R.layout.list_chat_right, viewGroup, false);
            } else {
                view = inflater.inflate(R.layout.list_chat, viewGroup, false);
            }

            Chat_Adapter.ViewHolder holder = new Chat_Adapter.ViewHolder(view);
            return holder;

        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            viewHolder.tv_chat.setText(items.get(i).chat_message);
        }

        @Override
        public int getItemViewType(int position) {

            if (items.get(position).sender.equals("bot")) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            TextView tv_chat;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_chat = itemView.findViewById(R.id.tv_chat);
            }
        }
    }


    //------------------ test


    public String loadJSONFromAsset(String file) {
        String json = null;
        try {
            InputStream is = this.getAssets().open(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public Map<String, String> read_data(String file) {
        Map<String, String> list = new HashMap<>();
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset(file));
            JSONArray m_jArry = obj.getJSONArray("data");

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONArray s1 = m_jArry.getJSONArray(i);
                list.put(s1.getString(0).trim(), s1.getString(1).trim());
            }
        } catch (Exception e) {
            Log.i(tag, " Exception read_data  : " + e.getMessage());
        }
        return list;
    }


}