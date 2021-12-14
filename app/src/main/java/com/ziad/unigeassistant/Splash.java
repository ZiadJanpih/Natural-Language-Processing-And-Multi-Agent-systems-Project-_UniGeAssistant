package com.ziad.unigeassistant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.ziad.unigeassistant.Classes.Category;
import com.ziad.unigeassistant.Handlers.DB_Handler;
import com.ziad.unigeassistant.Handlers.Zutil;

import java.util.Date;

public class Splash extends AppCompatActivity {

    public final String PREFS_NAME = "MyPrefsFile";
    public static final String tag = "ziad";
    DB_Handler mydb = new DB_Handler(this, null);
    private SharedPreferences sharedPreferences;
    private final int SleepTime = 2000; // mileseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        sharedPreferences=getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor=sharedPreferences.edit();


        if (sharedPreferences.getBoolean("my_first_time", true)) {
            editor.putBoolean("my_first_time", false);
            editor.commit();

            Thread fill_data = new Thread() {
                public void run() {

                    Category q1 = new Category();
                    q1.added_by = "default";
                    q1.pattern = "* classes today";
                    q1.type = "classes_today";
                    q1.template = "I will Check that and get back to you !";
                    mydb.add_category(q1);

                    Category q2 = new Category();
                    q2.added_by = "default";
                    q2.pattern = "classes today *";
                    q2.type = "classes_today";
                    q2.template = "I will Check that and get back to you !";
                    mydb.add_category(q2);

                    Category q3 = new Category();
                    q3.added_by = "default";
                    q3.pattern = "class today *";
                    q3.type = "classes_today";
                    q3.template = "I will Check that and get back to you !";
                    mydb.add_category(q3);

                    Category q4 = new Category();
                    q4.added_by = "default";
                    q4.pattern = "* class today";
                    q4.type = "classes_today";
                    q4.template = "I will Check that and get back to you !";
                    mydb.add_category(q4);

                    Category q5 = new Category();
                    q5.added_by = "default";
                    q5.pattern = "* lectures today";
                    q5.type = "classes_today";
                    q5.template = "I will Check that and get back to you !";
                    mydb.add_category(q5);

                    Category q6 = new Category();
                    q6.added_by = "default";
                    q6.pattern = "lectures today *";
                    q6.type = "classes_today";
                    q6.template = "I will Check that and get back to you !";
                    mydb.add_category(q6);

                    Category q7 = new Category();
                    q7.added_by = "default";
                    q7.pattern = "lecture today *";
                    q7.type = "classes_today";
                    q7.template = "I will Check that and get back to you !";
                    mydb.add_category(q7);

                    Category q8 = new Category();
                    q8.added_by = "default";
                    q8.pattern = "* lecture today";
                    q8.type = "classes_today";
                    q8.template = "I will Check that and get back to you !";
                    mydb.add_category(q8);


                    Category q9 = new Category();
                    q9.added_by = "default";
                    q9.pattern = "* classes tomorrow";
                    q9.type = "classes_tomorrow";
                    q9.template = "I will Check that and get back to you !";
                    mydb.add_category(q9);

                    Category q10 = new Category();
                    q10.added_by = "default";
                    q10.pattern = "classes tomorrow *";
                    q10.type = "classes_tomorrow";
                    q10.template = "I will Check that and get back to you !";
                    mydb.add_category(q10);

                    Category q11 = new Category();
                    q11.added_by = "default";
                    q11.pattern = "* lecture tomorrow";
                    q11.type = "classes_tomorrow";
                    q11.template = "I will Check that and get back to you !";
                    mydb.add_category(q11);

                    Category q12 = new Category();
                    q12.added_by = "default";
                    q12.pattern = "class tomorrow *";
                    q12.type = "classes_tomorrow";
                    q12.template = "I will Check that and get back to you !";
                    mydb.add_category(q12);

                    Category q13 = new Category();
                    q13.added_by = "default";
                    q13.pattern = "* class tomorrow";
                    q13.type = "classes_tomorrow";
                    q13.template = "I will Check that and get back to you !";
                    mydb.add_category(q13);

                    Category q14 = new Category();
                    q14.added_by = "default";
                    q14.pattern = "* lectures tomorrow";
                    q14.type = "classes_tomorrow";
                    q14.template = "I will Check that and get back to you !";
                    mydb.add_category(q14);

                    Category q15 = new Category();
                    q15.added_by = "default";
                    q15.pattern = "lectures tomorrow *";
                    q15.type = "classes_tomorrow";
                    q15.template = "I will Check that and get back to you !";
                    mydb.add_category(q15);

                    Category q16 = new Category();
                    q16.added_by = "default";
                    q16.pattern = "lecture tomorrow *";
                    q16.type = "classes_tomorrow";
                    q16.template = "I will Check that and get back to you !";
                    mydb.add_category(q16);

                    Category q17 = new Category();
                    q17.added_by = "default";
                    q17.pattern = "* events tomorrow";
                    q17.type = "events_tomorrow";
                    q17.template = "I will Check that and get back to you !";
                    mydb.add_category(q17);

                    Category q18 = new Category();
                    q18.added_by = "default";
                    q18.pattern = "events tomorrow *";
                    q18.type = "events_tomorrow";
                    q18.template = "I will Check that and get back to you !";
                    mydb.add_category(q18);

                    Category q19 = new Category();
                    q19.added_by = "default";
                    q19.pattern = "* event tomorrow";
                    q19.type = "events_tomorrow";
                    q19.template = "I will Check that and get back to you !";
                    mydb.add_category(q19);

                    Category q20 = new Category();
                    q20.added_by = "default";
                    q20.pattern = "event tomorrow *";
                    q20.type = "events_tomorrow";
                    q20.template = "I will Check that and get back to you !";
                    mydb.add_category(q20);

                    Category q21 = new Category();
                    q21.added_by = "default";
                    q21.pattern = "* event today";
                    q21.type = "events_today";
                    q21.template = "I will Check that and get back to you !";
                    mydb.add_category(q21);

                    Category q22 = new Category();
                    q22.added_by = "default";
                    q22.pattern = "* events today";
                    q22.type = "events_today";
                    q22.template = "I will Check that and get back to you !";
                    mydb.add_category(q22);

                    Category q23 = new Category();
                    q23.added_by = "default";
                    q23.pattern = "event today *";
                    q23.type = "events_today";
                    q23.template = "I will Check that and get back to you !";
                    mydb.add_category(q23);

                    Category q24 = new Category();
                    q24.added_by = "default";
                    q24.pattern = "events today *";
                    q24.type = "events_today";
                    q24.template = "I will Check that and get back to you !";
                    mydb.add_category(q24);

                    Category q25 = new Category();
                    q25.added_by = "default";
                    q25.pattern = "my name is *";
                    q25.type = "user_name";
                    q25.template = "hello";
                    mydb.add_category(q25);

                    Category q26 = new Category();
                    q26.added_by = "default";
                    q26.pattern = "what is my name *";
                    q26.type = "user_name_answer";
                    mydb.add_category(q26);

                    mydb.add_category(new Category(
                            "what is my name"
                            ,""
                            ,"user_name_answer"
                            ,"default"
                    ));

                    Category q27 = new Category();
                    q27.added_by = "default";
                    q27.pattern = "* do you know my name";
                    q27.type = "user_name_answer";
                    mydb.add_category(q27);

                    mydb.add_category(new Category(
                            "do you know my name"
                            ,""
                            ,"user_name_answer"
                            ,"default"
                    ));

                    Category q28 = new Category();
                    q28.added_by = "default";
                    q28.pattern = "your name is *";
                    q28.type = "bot_name";
                    q28.template = "Wow it's a nice name thank you";
                    mydb.add_category(q28);


                    Category q29 = new Category();
                    q29.added_by = "default";
                    q29.pattern = "what is your name *";
                    q29.type = "bot_name_answer";
                    mydb.add_category(q29);

                    mydb.add_category(new Category(
                            "what is your name"
                            ,""
                            ,"bot_name_answer"
                            ,"default"
                    ));

                    Category q30 = new Category();
                    q30.added_by = "default";
                    q30.pattern = "* do you know your name";
                    q30.type = "bot_name_answer";
                    mydb.add_category(q30);

                    mydb.add_category(new Category(
                            "do you know your name"
                            ,""
                            ,"bot_name_answer"
                            ,"default"
                    ));


                    Category q32= new Category();
                    q32.added_by = "default";
                    q32.pattern = "today is my birthday";
                    q32.type = "normal";
                    q32.template = "Happy birthday";
                    mydb.add_category(q32);

                    Category q33= new Category();
                    q33.added_by = "default";
                    q33.pattern = "what color is the tree";
                    q33.type = "normal";
                    q33.template = "In spring and summer it is green";
                    mydb.add_category(q33);

                    Category q34= new Category();
                    q34.added_by = "default";
                    q34.pattern = "what color is the tree";
                    q34.type = "normal";
                    q34.template = "In spring and summer it is green";
                    mydb.add_category(q34);

                    mydb.add_category(new Category(
                            "* how old are you"
                            ,"I was born on 01.03.2021"
                            ,"normal"
                            ,"default"
                    ));

                    mydb.add_category(new Category(
                            "how old are you"
                            ,"I was born on 01.03.2021"
                            ,"normal"
                            ,"default"
                    ));

                    mydb.add_category(new Category(
                            "what time is it"
                            ,"it is "+ Zutil.dateFormat.format(new Date())
                            ,"normal"
                            ,"default"
                    ));

                    mydb.add_category(new Category(
                            "what time now"
                            ,"it is "+ Zutil.dateFormat.format(new Date())
                            ,"normal"
                            ,"default"
                    ));

                    mydb.add_category(new Category(
                            "hi *"
                            ,"Hi "
                            ,"greetings"
                            ,"default"
                    ));

                    mydb.add_category(new Category(
                            "hi"
                            ,"Hi "
                            ,"greetings"
                            ,"default"
                    ));
                    mydb.add_category(new Category(
                            "hello *"
                            ,"Hello "
                            ,"greetings"
                            ,"default"
                    ));

                    mydb.add_category(new Category(
                            "hello"
                            ,"Hello "
                            ,"greetings"
                            ,"default"
                    ));

                    mydb.add_category(new Category(
                            "good morning *"
                            ,"Good morning "
                            ,"greetings"
                            ,"default"
                    ));

                    mydb.add_category(new Category(
                            "good morning"
                            ,"Good morning "
                            ,"greetings"
                            ,"default"
                    ));
                    mydb.add_category(new Category(
                            "good evening *"
                            ,"Good evening "
                            ,"greetings"
                            ,"default"
                    ));

                    mydb.add_category(new Category(
                            "good evening"
                            ,"Good evening "
                            ,"greetings"
                            ,"default"
                    ));

                    mydb.add_category(new Category(
                            "good afternoon *"
                            ,"Good afternoon "
                            ,"greetings"
                            ,"default"
                    ));
                    mydb.add_category(new Category(
                            "good afternoon"
                            ,"Good afternoon "
                            ,"greetings"
                            ,"default"
                    ));

                    mydb.add_category(new Category(
                            "what do you do *"
                            ,"I am a chatbot app"
                            ,"normal"
                            ,"default"
                    ));

                    mydb.add_category(new Category(
                            "what do you do"
                            ,"I am a chatbot app"
                            ,"normal"
                            ,"default"
                    ));

                    mydb.add_category(new Category(
                            "where do you live *"
                            ,"I live in your phone"
                            ,"normal"
                            ,"default"
                    ));

                    mydb.add_category(new Category(
                            "where do you live"
                            ,"I live in your phone"
                            ,"normal"
                            ,"default"
                    ));
                }
            };
            fill_data.start();
        }
        Thread startTimer = new Thread() {
            public void run() {
                try {
                    sleep(SleepTime);
                    Intent intent;
                    String loged=sharedPreferences.getString("login_successful","" );
                    if (loged!= null && loged.equals("loged")) {

                        intent = new Intent(Splash.this, MainActivity.class);

                    }
                    else
                    {
                        intent = new Intent(Splash.this, LoinActivity.class);
                    }

                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        startTimer.start();

    }
}