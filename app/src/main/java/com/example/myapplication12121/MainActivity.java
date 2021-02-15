package com.example.myapplication12121;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Task> tasks = new ArrayList<Task>();
    BoxAdapter boxAdapter;
    ListView List;
    Cursor userCursor;

    SQLiteDatabase db;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List = findViewById(R.id.List);

         dbHelper = new DBHelper(this);
         db = dbHelper.getWritableDatabase();
        ReadTasks();
    }


    public void ReadTasks() {
        userCursor = db.rawQuery("select * from " + DBHelper.TABLE_NAME, null);

        int count = userCursor.getCount();

        if (count > 0) {
            while (userCursor.moveToNext()) {
                tasks.add(new Task(userCursor.getString(userCursor.getColumnIndex(DBHelper.KEY_NAME)),
                        userCursor.getString(userCursor.getColumnIndex(DBHelper.KEY_DATE)),
                        userCursor.getString(userCursor.getColumnIndex(DBHelper.KEY_NOTE)),
                        false,
                        R.drawable.icon_edit, R.drawable.icons_delete));
            }
            userCursor.close();
        }

        boxAdapter = new BoxAdapter(this, tasks);
        List.setAdapter(boxAdapter);
    }


    public void add(View view) {
        EditText editText = findViewById(R.id.editText);
        String task = editText.getText().toString();

        ContentValues contentValues = new ContentValues();
        if (!task.isEmpty()) {
            contentValues.put(DBHelper.KEY_NAME, task);

            long result = db.insert(DBHelper.TABLE_NAME, null, contentValues);
            if (result < 0) {
                System.out.println("Error insert");
            }

            tasks.add(new Task(task, null, null, false,
                    R.drawable.icon_edit, R.drawable.icons_delete));
        } else {
            Toast.makeText(
                    MainActivity.this,
                    "Нельзя добавить пустую задачу, бездельник!",
                    Toast.LENGTH_LONG
            ).show();
        }
        boxAdapter.notifyDataSetChanged();
        editText.setText("");
    }

}

