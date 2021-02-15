package com.example.myapplication12121;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class NoteActivity extends AppCompatActivity {

    Toolbar toolbar;
    String name_task;
    EditText noteEditText;

    DBHelper dbHelper;
    SQLiteDatabase db;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        noteEditText = findViewById(R.id.noteEditText);

        name_task = (String)getIntent().getSerializableExtra("NAME_TASK");
        toolbar();

        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
        setCurrentNote();

    }

    private void toolbar(){
        toolbar = findViewById(R.id.toolbarNote);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle(name_task);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(!noteEditText.toString().isEmpty()) {
                    addDB();
                    System.out.println("lkhm");
                    this.finish();
                }
                else {
                    this.finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void addDB(){
        ContentValues content = new ContentValues();
        String s = name_task;

        content.put(DBHelper.KEY_NOTE, noteEditText.getText().toString());
        System.out.println(noteEditText.getText().toString());

        long res = db.update(DBHelper.TABLE_NAME, content, DBHelper.KEY_NAME + "=" + "\"" + s + "\"", null);
        if (res < 0) {
            System.out.println("Error update date in database");
        }
        else {
            System.out.println("ok");
        }

    }

    private void setCurrentNote() {
        Cursor c = db.rawQuery("select " + DBHelper.KEY_NOTE + " from " + DBHelper.TABLE_NAME + " where " + DBHelper.KEY_NAME + "=\"" + name_task + "\"", null);

        int count = c.getCount();

        if (count > 0) {
            while (c.moveToNext()) {
                String note = c.getString(c.getColumnIndex(DBHelper.KEY_NOTE));
                if(note != null) {
                    noteEditText.setText(note);
                }
            }
        }
        c.close();
    }
}
