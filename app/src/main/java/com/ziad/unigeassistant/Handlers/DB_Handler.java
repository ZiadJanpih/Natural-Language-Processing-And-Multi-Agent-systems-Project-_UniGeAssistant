package com.ziad.unigeassistant.Handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ziad.unigeassistant.Classes.Category;
import com.ziad.unigeassistant.Classes.Chat;
import com.ziad.unigeassistant.Classes.Question;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import java.text.SimpleDateFormat;

public class DB_Handler extends SQLiteOpenHelper{

    public static String tag = "ziad";
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static final int DB_v = 1;
    private static final String DB_Name = "UniGeAssistant.db";



    //----------Chat Table
    public static final String TABLE_Chat = "Chat";
    public static final String Chat_COLUM_id = "id";
    public static final String Chat_COLUM_chat_message = "chat_message";
    public static final String Chat_COLUM_datetime = "datetime";
    public static final String Chat_COLUM_sender = "sender";



    //----------Categories Table
    public static final String TABLE_Categories = "Categories";
    public static final String Categories_COLUM_id = "id";
    public static final String Categories_COLUM_pattern = "pattern";
    public static final String Categories_COLUM_template = "template";
    public static final String Categories_COLUM_type = "type";
    public static final String Categories_COLUM_added_by = "added_by";


    //----------Questions Table
    public static final String TABLE_Questions = "Questions";
    public static final String Questions_COLUM_id = "id";
    public static final String Questions_COLUM_question = "question";
    public static final String Questions_COLUM_category_id = "category_id";
    public static final String Questions_COLUM_answer = "answer";
    public static final String Questions_COLUM_status = "status";
    public static final String Questions_COLUM_question_datetime = "question_datetime";
    public static final String Questions_COLUM_answer_datetime = "answer_datetime";
    public static final String Questions_COLUM_target = "target";


    public DB_Handler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DB_Name, factory, DB_v);
    }

    @Override
    public void  onCreate(SQLiteDatabase db) {

        //----------Chat Table
        String q_chats = " CREATE TABLE  " + TABLE_Chat + " ( " +
                Chat_COLUM_id + " INTEGER PRIMARY KEY  AUTOINCREMENT," +
                Chat_COLUM_chat_message + " TEXT ," +
                Chat_COLUM_datetime + " TEXT ," +
                Chat_COLUM_sender + " TEXT " +
                " );";
        db.execSQL(q_chats);



        String q_categories = " CREATE TABLE  " + TABLE_Categories + " ( " +
                Categories_COLUM_id + " INTEGER PRIMARY KEY  AUTOINCREMENT," +
                Categories_COLUM_pattern + " TEXT ," +
                Categories_COLUM_template + " TEXT ," +
                Categories_COLUM_type + " TEXT ," +
                Categories_COLUM_added_by  + " TEXT " +
                " );";
        db.execSQL(q_categories);



        String q_questions = " CREATE TABLE  " + TABLE_Questions + " ( " +
                Questions_COLUM_id + " INTEGER PRIMARY KEY  AUTOINCREMENT," +
                Questions_COLUM_question + " TEXT ," +
                Questions_COLUM_category_id + " INTEGER ," +
                Questions_COLUM_status + " TEXT ," +
                Questions_COLUM_answer + " TEXT ," +
                Questions_COLUM_question_datetime + " TEXT ," +
                Questions_COLUM_answer_datetime + " TEXT ," +
                Questions_COLUM_target  + " TEXT " +
                " );";
        db.execSQL(q_questions);

    }

    @Override
    public void  onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Chat);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Categories);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Questions);
        onCreate(db);
    }
//  Add Functions -------------------Start-------------------------------------------------------
public long add_chat_message(Chat chat){
    ContentValues v = new ContentValues();
    v.put(Chat_COLUM_chat_message, chat.chat_message);
    v.put(Chat_COLUM_datetime, dateFormat.format(new Date()));
    v.put(Chat_COLUM_sender, chat.sender);
    SQLiteDatabase db = getWritableDatabase();
    long id =db.insert(TABLE_Chat, null, v);
    db.close();
    return id;
}


    public long add_category(Category category){
        ContentValues v = new ContentValues();
        v.put(Categories_COLUM_pattern, category.pattern);
        v.put(Categories_COLUM_template, category.template);
        v.put(Categories_COLUM_type, category.type);
        v.put(Categories_COLUM_added_by, category.added_by);
        SQLiteDatabase db = getWritableDatabase();
        long id =db.insert(TABLE_Categories, null, v);
        db.close();
        return id;
    }

    public long add_question(Question question){
        ContentValues v = new ContentValues();
        v.put(Questions_COLUM_question, question.question);
        v.put(Questions_COLUM_category_id, question.category_id);
        v.put(Questions_COLUM_status, question.status);
        v.put(Questions_COLUM_question_datetime, question.question_datetime);
        v.put(Questions_COLUM_answer_datetime, question.answer_datetime);
        v.put(Questions_COLUM_target, question.target);
        SQLiteDatabase db = getWritableDatabase();
        long id =db.insert(TABLE_Questions, null, v);
        db.close();
        return id;
    }
//  Add Functions ====================End========================================================



//  Get Functions -------------------Start-------------------------------------------------------
    public ArrayList<Chat> get_All_chats() {
        ArrayList<Chat> item_list = new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase();
        String q = "SELECT * FROM  " + TABLE_Chat + " WHERE  1=1 "+" order by "+ Chat_COLUM_id+","+Chat_COLUM_datetime  +" DESC";
        Cursor c = db.rawQuery(q, null);
        if (c.moveToFirst() && c.getCount() != 0) {
            while (!c.isAfterLast()) {
                Chat chat= new Chat();
                chat.id=(c.getLong(c.getColumnIndex(Chat_COLUM_id)));
                chat.chat_message=(c.getString(c.getColumnIndex(Chat_COLUM_chat_message)));
                chat.datetime=(c.getString(c.getColumnIndex(Chat_COLUM_datetime)));
                chat.sender=(c.getString(c.getColumnIndex(Chat_COLUM_sender)));
                item_list.add(chat);
                c.moveToNext();
            }
        } else {

        }
        c.close();
        db.close();
        return item_list;
    }


    public Category match_category(String pattern) {
        Category res = new Category();

        SQLiteDatabase db = getWritableDatabase();
        String q = "SELECT * FROM  " + TABLE_Categories + " Where upper(" + Categories_COLUM_pattern + ") = upper('" + pattern + "')";
        Cursor c = db.rawQuery(q, null);
        if (c.moveToFirst() && c.getCount() != 0) {

            res.id = c.getLong(c.getColumnIndex(Categories_COLUM_id));
            res.pattern = c.getString(c.getColumnIndex(Categories_COLUM_pattern));
            res.template = c.getString(c.getColumnIndex(Categories_COLUM_template));
            res.type = c.getString(c.getColumnIndex(Categories_COLUM_type));
            res.added_by = c.getString(c.getColumnIndex(Categories_COLUM_added_by));

        } else {
            return null;
        }
        c.close();
        db.close();
        return res;
    }


    public Question get_question_byId(long id) {
        Question res = null;
        try {
            SQLiteDatabase db = getWritableDatabase();
            String q = "SELECT * FROM  " + TABLE_Questions + " WHERE " + Questions_COLUM_id + " = " + id;
            Cursor c = db.rawQuery(q, null);
            if (c.moveToFirst() && c.getCount() != 0) {
                while (!c.isAfterLast()) {
                    res = new Question();
                    res.id = (c.getLong(c.getColumnIndex(Questions_COLUM_id)));
                    res.category_id = (c.getLong(c.getColumnIndex(Questions_COLUM_category_id)));
                    res.question = (c.getString(c.getColumnIndex(Questions_COLUM_question)));
                    res.answer = (c.getString(c.getColumnIndex(Questions_COLUM_answer)));
                    res.question_datetime = (c.getString(c.getColumnIndex(Questions_COLUM_question_datetime)));
                    res.answer_datetime = (c.getString(c.getColumnIndex(Questions_COLUM_answer_datetime)));
                    res.status = (c.getString(c.getColumnIndex(Questions_COLUM_status)));
                    res.target = (c.getString(c.getColumnIndex(Questions_COLUM_target)));
                    c.moveToNext();

                }
            } else {
                Log.i(tag, "null Questions  ");
                return null;
            }
            c.close();
            db.close();
        } catch (Exception e) {
            Log.i(tag, "Exception  " + e.getMessage());
            return null;
        }
        return res;
    }


    public ArrayList<Question> get_unseen_questions() {
        ArrayList<Question> item_list = new ArrayList<>();
        try {
            SQLiteDatabase db = getWritableDatabase();
            String q = "SELECT * FROM  " + TABLE_Questions + " WHERE " + Questions_COLUM_status + " = 'answered_not_seen'";
            Cursor c = db.rawQuery(q, null);
            if (c.moveToFirst() && c.getCount() != 0) {
                while (!c.isAfterLast()) {
                    Question res = new Question();
                    res.id = (c.getLong(c.getColumnIndex(Questions_COLUM_id)));
                    res.category_id = (c.getLong(c.getColumnIndex(Questions_COLUM_category_id)));
                    res.question = (c.getString(c.getColumnIndex(Questions_COLUM_question)));
                    res.answer = (c.getString(c.getColumnIndex(Questions_COLUM_answer)));
                    res.question_datetime = (c.getString(c.getColumnIndex(Questions_COLUM_question_datetime)));
                    res.answer_datetime = (c.getString(c.getColumnIndex(Questions_COLUM_answer_datetime)));
                    res.status = (c.getString(c.getColumnIndex(Questions_COLUM_status)));
                    res.target = (c.getString(c.getColumnIndex(Questions_COLUM_target)));
                    item_list.add(res);
                    c.moveToNext();
                }
            } else {
               // Log.i(tag, "null Questions  ");
                return null;
            }
            c.close();
            db.close();
        } catch (Exception e) {
            Log.i(tag, "Exception  " + e.getMessage());
            return null;
        }
        return item_list;
    }


//  Get Functions ====================End========================================================



//  Update Functions -------------------Start-------------------------------------------------------

    public void update_question(Question question) {
        ContentValues v = new ContentValues();
        //v.put(Questions_COLUM_id, question.id);
        v.put(Questions_COLUM_question, question.question);
        v.put(Questions_COLUM_category_id, question.category_id);
        v.put(Questions_COLUM_answer, question.answer);
        v.put(Questions_COLUM_status, question.status);
        v.put(Questions_COLUM_question_datetime, question.question_datetime);
        v.put(Questions_COLUM_answer_datetime, question.answer_datetime);
        v.put(Questions_COLUM_target, question.target);

        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_Questions, v, Questions_COLUM_id + " = " + question.id, null);
        db.close();
    }


    public void update_category(Category category) {
        ContentValues v = new ContentValues();
        //v.put(Questions_COLUM_id, question.id);
        v.put(Categories_COLUM_pattern, category.pattern);
        v.put(Categories_COLUM_template, category.template);
        v.put(Categories_COLUM_type, category.type);
        v.put(Categories_COLUM_added_by, category.added_by);

        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_Categories, v, Categories_COLUM_id + " = " + category.id, null);
        db.close();
    }
//  Update Functions ====================End========================================================




//  Delete Functions -------------------Start-------------------------------------------------------

    public void del_category(Category category) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_Categories, Categories_COLUM_id + " = " + category.id, null);
        db.close();

    }

    public void del_chats() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_Chat, "1 = 1", null);
        db.close();

    }


//  Delete Functions ====================End========================================================


//  --- Functions -------------------Start-------------------------------------------------------
//  --- Functions ====================End========================================================
}
