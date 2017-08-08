package com.example.takunaka.taskapp.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.takunaka.taskapp.sqlQuerry.SubTask;
import com.example.takunaka.taskapp.sqlQuerry.Task;
import com.example.takunaka.taskapp.sqlQuerry.TaskContainer;
import com.example.takunaka.taskapp.sqlQuerry.UserContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by takunaka on 03.08.17.
 */

public class DBTasksHelper extends SQLiteOpenHelper {

   public static final int DATABASE_VERSION = 2;
   public static final String DATABASE_NAME = "TasksDB";
   public static final String TABLE_TASKS = "Tasks";

    public static final String KEY_ID = "_id";
    public static final String KEY_NAMEID = "_nameid";
    public static final String KEY_DESCRIPTION = "_description";
    public static final String KEY_DATE = "_date";
    public static final String KEY_STATE = "_state";


    public DBTasksHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_TASKS + " ("
                + KEY_ID + " integer primary key autoincrement, "
                + KEY_NAMEID + " integer, "
                + KEY_DESCRIPTION + " text, "
                + KEY_DATE + " numeric, "
                + KEY_STATE + " text " + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_TASKS );

        onCreate(db);
    }

    public List<Task> getAllTasks(){
        String id = String.valueOf(UserContainer.getSelectedID());
        List<Task> tasks = new ArrayList<Task>();

        String selectQuery = "SELECT * FROM " + TABLE_TASKS
                + " WHERE " + KEY_NAMEID + " = " + id ;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                tasks.add(new Task(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return tasks;
    }

}
