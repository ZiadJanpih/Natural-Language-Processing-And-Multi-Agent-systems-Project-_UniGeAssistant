package com.ziad.unigeassistant;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.ziad.unigeassistant.Handlers.DB_Handler;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoinActivity extends AppCompatActivity {

    public static final String tag = "ziad";
    DB_Handler mydb = new DB_Handler(this, null);
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    String token = FirebaseInstanceId.getInstance().getToken();

    OkHttpClient mClient = new OkHttpClient();
    public static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    final MediaType JSON
            = MediaType.parse("application/json");

    AutoCompleteTextView email;
    EditText password;
    Button email_sign_in_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loin);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        email_sign_in_button = findViewById(R.id.email_sign_in_button);

        email_sign_in_button.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        String u = email.getText().toString();
                                                        String p = password.getText().toString();
                                                        token = FirebaseInstanceId.getInstance().getToken();

                                                        boolean valid = true;
                                                        if (u == null || u.isEmpty()) {
                                                            email.setError(getString(R.string.error_field_required));
                                                            valid = false;
                                                        } else
                                                            email.setError(null);
                                                        if (p == null || p.isEmpty()) {
                                                            password.setError(getString(R.string.error_field_required));
                                                            valid = false;
                                                        } else
                                                            password.setError(null);
                                                        if (valid) {
                                                            JSONObject data = new JSONObject();

                                                            try {
                                                                data.put("function", "login");
                                                                data.put("username", u);
                                                                data.put("password", p);
                                                                data.put("token", token);

                                                                send_sever_Message(data);
                                                            } catch (Exception e) {

                                                            }
                                                        }

                                                    }
                                                }
        );

    }


    public void send_sever_Message(final JSONObject data) {

        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    JSONObject root = new JSONObject();
                    root.put("data", data);
                    root.put("to", "/topics/server");

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
                    Toast.makeText(getApplicationContext(), "Login data sent successfully \n please wait while the result received", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    //  e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    String postToFCM(String bodyString) throws IOException {
        RequestBody body = RequestBody.create(JSON, bodyString);
        Request request = new Request.Builder()
                .url(FCM_MESSAGE_URL)
                .post(body)
                .addHeader("Authorization", "key=" + "*******")
                .build();
        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }
}