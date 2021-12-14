package com.ziad.unigeassistant.Notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ziad.unigeassistant.Classes.Category;
import com.ziad.unigeassistant.Classes.Category_rank;
import com.ziad.unigeassistant.Classes.Chat;
import com.ziad.unigeassistant.Classes.Question;
import com.ziad.unigeassistant.Handlers.BusHolder;
import com.ziad.unigeassistant.Handlers.DB_Handler;
import com.ziad.unigeassistant.Handlers.Zutil;
import com.ziad.unigeassistant.MainActivity;
import com.ziad.unigeassistant.R;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FirebaseMessagingServices extends FirebaseMessagingService {
    private int NOTIFICATION_ID;
    public static final String tag = "ziad";
    DB_Handler mydb = new DB_Handler(this, null);

    OkHttpClient mClient = new OkHttpClient();
    public static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    final MediaType JSON = MediaType.parse("application/json");


    public final String PREFS_NAME = "MyPrefsFile";
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i("ziad", "onMessageReceived: ");
             sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
             editor = sharedPreferences.edit();
        Map<String, String> params = remoteMessage.getData();
        if (params.size() > 0) {
            String fun = params.get("function");
            Log.i("ziad", "onMessageReceived: function = " + fun);
            switch (fun) {
                case "login": {
                    login(params);
                    break;
                }
                case "news": {
                    get_news(params);
                    break;
                }
                case "unknown_question":
                {
                    answer_unknown_question(params);
                    break;
                }
                case "unknown_question_answer":
                {
                    get_question_answer(params);
                    break;
                }
            }
        }
    }

    public void get_question_answer(Map<String, String> params)
    {
        Long question_id = Long.parseLong(params.get("question_id"));
        String answer = params.get("answer");
        String sender = params.get("sender");
        String pattern = params.get("pattern");
        Question question = mydb.get_question_byId(question_id);
        if(question.status.equals("not_answered"))
        {
            question.answer_datetime = Zutil.dateFormat.format(new Date());
            question.answer = answer;
            question.status = "answered_not_seen";

            Category category= new Category();
            category.pattern= pattern;
            category.template=answer;
            category.type="normal";
            category.added_by=sender;

            long cat_id = mydb.add_category(category);
            question.category_id=cat_id;
            mydb.update_question(question);
        }
    }

    public void answer_unknown_question(Map<String, String> params)
    {
        String token = FirebaseInstanceId.getInstance().getToken();
        Long question_id = Long.parseLong(params.get("question_id"));
        String sentence = params.get("question");
        String user_token = params.get("token");
        if(!token.equals(user_token))
        {
            Category category= find_category_matching(sentence);
            if (category != null)
            {
                if (category.template!=null && !category.template.isEmpty())
                {
                    JSONObject data = new JSONObject();
                    try {
                        data.put("function", "unknown_question_answer");
                        data.put("answer", category.template);
                        data.put("pattern", category.pattern);
                        data.put("sender", "bot_friends");
                        data.put("question_id",question_id);
                        JSONArray recipients= new JSONArray();
                        recipients.put(user_token);
                        send_client_Message(data, recipients);
                    } catch (Exception e) {

                    }
                }
            }
        }
    }


    public void login(Map<String, String> params) {
        String status = params.get("status");
        if (status.equals("successful")) {
            String user_id = params.get("user_id");
            editor.putString("login_successful", "loged");
            editor.putString("user_id", user_id);
            editor.commit();
        } else {

        }

    }

    public void get_news(Map<String, String> params) {
        Long question_id = Long.parseLong(params.get("question_id"));
        String answer = params.get("answer");
        Question question = mydb.get_question_byId(question_id);
        question.answer_datetime = Zutil.dateFormat.format(new Date());
        question.answer = answer;
        question.status = "answered_not_seen";
        mydb.update_question(question);
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

    public void send_client_Message(final JSONObject data, final JSONArray to) {

        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    JSONObject root = new JSONObject();
                    root.put("data", data);
                    root.put("registration_ids",to); // "/topics/server"

                    String result = postToFCM(root.toString());
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
                    //Toast.makeText(getApplicationContext(), "Login data sent successfully \n please wait while the result received", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    //  e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    String postToFCM(String bodyString) throws IOException {
        RequestBody body = RequestBody.create(JSON, bodyString);
        Request request = new Request.Builder()
                .url(FCM_MESSAGE_URL)
                .post(body)
                .addHeader("Authorization", "key=" + "*****")
                .build();
        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }


}