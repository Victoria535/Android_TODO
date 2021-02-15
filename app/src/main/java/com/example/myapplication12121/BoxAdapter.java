package com.example.myapplication12121;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class BoxAdapter extends BaseAdapter {
    private Context ctx;
    private LayoutInflater lInflater;
    private static ArrayList<Task> objects;

    private CheckBox checked;
    private ImageView delImg;
    private ImageView editImg;
    private ImageView noteImg;

    private TextView textViewName;
    private TextView textViewDate;
    TextView tv;

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    BoxAdapter(Context context, ArrayList<Task> tasks){
        ctx = context;
        objects = tasks;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //кол-во элементов
    @Override
    public int getCount() {
        return objects.size();
    }

    // элемент по позиции
    public Object getItem(int position) {
        return objects.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    // пункт списка
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item, parent, false);
        }

        Task t = getTask(position);

        textViewName = view.findViewById(R.id.tvDescr);
        textViewDate = view.findViewById(R.id.tvDate);

        delImg = view.findViewById(R.id.deleteImage);
        editImg = view.findViewById(R.id.editImage);
      //  noteImg =  view.findViewById(R.id.imageNote);

        textViewName.setText(t.name);
        textViewDate.setText(t.date);

        editImg.setImageResource(R.drawable.icon_edit);
        delImg.setImageResource(R.drawable.icons_delete);

        dbHelper = new DBHelper(ctx);
        db = dbHelper.getWritableDatabase();

        delImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDelete(position);
            }});

        editImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertEdit(position);
            }});

        textViewName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, AboutActivity.class);
                intent.putExtra("NAME_TASK", getTask(position).name);
                ctx.startActivity(intent);
            }
        });

        checked =  view.findViewById(R.id.cbBox);
        // присваиваем чекбоксу обработчик
        checked.setOnCheckedChangeListener(myCheckChangeList);
        // пишем позицию
        checked.setTag(position);
        // заполняем данными: отмечено или нет
        checked.setChecked(t.box);
        System.out.println(t.box + "  llll");
        return view;
    }

    // задача по позиции
    public Task getTask(int position) {
        return ((Task) getItem(position));
    }


    private OnCheckedChangeListener myCheckChangeList = new OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            String s = getTask((Integer) buttonView.getTag()).name;

            if (isChecked) {
                textViewName.setPaintFlags(textViewName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                getTask((Integer) buttonView.getTag()).box = true;
                updateDBBox(1, s);

            } else {
                updateDBBox(0, s);
                getTask((Integer) buttonView.getTag()).box = false;
                textViewName.setPaintFlags(textViewName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        }
    };



    private void remove(int position){
        objects.remove(position);
        notifyDataSetChanged();
    }

    private void removeDB(int position){
        String s = getTask(position).name;
        long res = db.delete(DBHelper.TABLE_NAME, "name = ?", new String[]{s});
        if (res < 0) {
            System.out.println("Error delete database");
        }
    }

    private void updateDB(int position, String newName) {
        String s = getTask(position).name;
        ContentValues Values = new ContentValues();
        Values.put(DBHelper.KEY_NAME, newName);
        long res = db.update(DBHelper.TABLE_NAME, Values, DBHelper.KEY_NAME + "=" + "\"" + s + "\"", null);
        if (res < 0) {
            System.out.println("Error update database");
        }
    }
    private void updateDBBox(int newValue, String name) {
        String s = name;
        ContentValues Values = new ContentValues();
        Values.put(DBHelper.KEY_CHECK, newValue);

        long res = db.update(DBHelper.TABLE_NAME, Values, DBHelper.KEY_NAME + "=" + "\"" + s + "\"", null);
        if (res < 0){
            System.out.println("Error update");
        }

    }

    private void update(int position, String newName){
        getTask(position).name = newName;
        notifyDataSetChanged();
    }

    private void AlertDelete(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Удаление");
        builder.setCancelable(false);
        builder.setMessage("Задача " + getTask(position).name + " будет удалена");
        builder.setIcon(R.drawable.icons_delete);
        builder.setNegativeButton("Нет", null);
        builder.setPositiveButton("Удалить", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                removeDB(position);
                remove(position);
            }
        });
        builder.show();
    }

    private void AlertEdit(final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Редактирование");
        builder.setIcon(R.drawable.icon_edit);
        builder.setCancelable(false);
        final EditText input = new EditText(ctx);
        input.setText(getTask(position).name);
        input.setSelection(input.getText().length());
        builder.setView(input);

        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Ок", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (!input.getText().toString().isEmpty()) {
                    String temp = input.getText().toString();
                    updateDB(position, temp);
                    update(position, temp);
                } else {
                    Toast.makeText(
                            ctx,
                            "Введите тект задачи.",
                            Toast.LENGTH_LONG
                    ).show();
                }
            }
        });
        builder.show();
    }
}
