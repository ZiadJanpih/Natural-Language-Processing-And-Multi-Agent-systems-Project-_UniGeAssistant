package com.ziad.unigeassistant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ziad.unigeassistant.Classes.Category;
import com.ziad.unigeassistant.Classes.Question;
import com.ziad.unigeassistant.Handlers.DB_Handler;

import java.util.Date;

public class LearnQuestion extends AppCompatActivity {

    public static final String tag = "ziad";
    DB_Handler mydb = new DB_Handler(this, null);

    EditText et_answer_question_question;
    EditText et_answer_question_answer;
    Button but_answer_question_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_question);

        et_answer_question_question= findViewById(R.id.et_answer_question_question);
        et_answer_question_answer= findViewById(R.id.et_answer_question_answer);
        but_answer_question_save= findViewById(R.id.but_answer_question_save);

        Intent intent = getIntent();
        String question = intent.getStringExtra("question");

        if(question!= null && !question.isEmpty())
        {
            et_answer_question_question.setText(question);
        }
        but_answer_question_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question = et_answer_question_question.getText().toString();
                String answer = et_answer_question_answer.getText().toString();

                boolean valid = true;
                if (question == null || question.isEmpty()) {
                    et_answer_question_question.setError(getString(R.string.error_field_required));
                    valid = false;
                }else
                    et_answer_question_question.setError(null);
                if (answer == null || answer.isEmpty()) {
                    et_answer_question_answer.setError(getString(R.string.error_field_required));
                    valid = false;
                }else
                    et_answer_question_answer.setError(null);

                if (valid) {
                    Category category= new Category();
                    category.added_by="user";
                    category.pattern=question;
                    category.template=answer;
                    category.type="normal";
                    mydb.add_category(category);
                    Toast.makeText(getApplicationContext(), "Answer saved", Toast.LENGTH_LONG).show();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    onBackPressed();
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

}