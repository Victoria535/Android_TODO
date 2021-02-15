package com.example.myapplication12121;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;

public class AboutActivity extends AppCompatActivity {

   Toolbar toolbar;
   String name_task;
   TextView currentDateTime;
    TextView textViewNote;
   Calendar dateAndTime = Calendar.getInstance();

    DBHelper dbHelper;
    SQLiteDatabase db;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        name_task = (String)getIntent().getSerializableExtra("NAME_TASK");

        toolbar();
        currentDateTime = findViewById(R.id.currentDateTime);
        textViewNote = findViewById(R.id.textViewNote);

        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
        setDateInTextView();
        setCurrentNote();
    }

    private void toolbar(){
        toolbar = findViewById(R.id.toolbar);
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
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void SetDate(View view) {
        new DatePickerDialog(this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

     // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime();
        }
    };

    private void setInitialDateTime() {
        currentDateTime.setText(DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_NO_YEAR));
        currentDateTime.setTextColor(Color.rgb(25, 25, 112));

        addDate();
    }
    public void addDate(){
        String s = name_task;

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_DATE, currentDateTime.getText().toString());

        long res = db.update(DBHelper.TABLE_NAME, contentValues, DBHelper.KEY_NAME + "=" + "\"" + s + "\"", null);
        if (res < 0) {
            System.out.println("Error update date in database");
        }
    }


    public void setDateInTextView(){
        Cursor cursor = db.rawQuery("select " + DBHelper.KEY_DATE + " from " + DBHelper.TABLE_NAME + " where " + DBHelper.KEY_NAME + "=\"" + name_task +"\"", null);

        int count = cursor.getCount();

        if (count > 0) {
            while (cursor.moveToNext()) {
                String d = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_DATE));
                if(d != null) {
                    currentDateTime.setText(d);
                    currentDateTime.setTextColor(Color.rgb(25, 25, 112));
                }
            }
        }
        cursor.close();
    }

    private void setCurrentNote() {
        Cursor c = db.rawQuery("select " + DBHelper.KEY_NOTE + " from " + DBHelper.TABLE_NAME + " where " + DBHelper.KEY_NAME + "=\"" + name_task + "\"", null);

        int count = c.getCount();

        if (count > 0) {
            while (c.moveToNext()) {
                String note = c.getString(c.getColumnIndex(DBHelper.KEY_NOTE));
                if(note != null) {
                    textViewNote.setText(note);
                    textViewNote.setTextColor(Color.rgb(25, 25, 112));

                }
            }
        }
        c.close();
    }
    public void addNote(View view){
        Intent intentNote = new Intent(this, NoteActivity.class);

        intentNote.putExtra("NAME_TASK", name_task);
        this.startActivity(intentNote);
    }
}
