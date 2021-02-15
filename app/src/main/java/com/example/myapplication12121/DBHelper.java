package com.example.myapplication12121;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

     private static final int DATABASE_VERSION = 1;
     static final String DATABASE_NAME = "tasksDb";
     static final String TABLE_NAME = "tasks";

    static final String KEY_NAME = "name";
    static final String KEY_DATE = "date";
    static final String KEY_NOTE = "note";
    static final String KEY_CHECK = "checked";


     DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " +  TABLE_NAME  + "(" +
                KEY_NAME + " TEXT PRIMARY KEY," +
                KEY_DATE + " TEXT DEFAULT NULL," +
                KEY_NOTE + " TEXT DEFAULT NULL," +
                KEY_CHECK + " INTEGER DEFAULT 0 );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }



}
