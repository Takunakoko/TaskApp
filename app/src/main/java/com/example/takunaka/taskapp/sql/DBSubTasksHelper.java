package com.example.takunaka.taskapp.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.takunaka.taskapp.sqlQuerry.SubTask;
import com.example.takunaka.taskapp.sqlQuerry.TaskContainer;
import com.example.takunaka.taskapp.sqlQuerry.UserContainer;
import com.example.takunaka.taskapp.sqlQuerry.Users;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by takunaka on 03.08.17.
 */

public class DBSubTasksHelper extends SQLiteOpenHelper {

   public static final int DATABASE_VERSION = 1;
   public static final String DATABASE_NAME = "SubTasksDB";
   public static final String TABLE_SUBTASK = "SubTasks";

    public static final String KEY_ID = "_id";
    public static final String KEY_NAMEID = "_nameid";
    public static final String KEY_TASKID = "_taskid";
    public static final String KEY_DESCRIPTION = "_description";
    public static final String KEY_STATE = "_state";


    public DBSubTasksHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_SUBTASK + " ("
                + KEY_ID + " integer primary key autoincrement, "
                + KEY_NAMEID + " integer, "
                + KEY_TASKID + " integer, "
                + KEY_DESCRIPTION + " text, "
                + KEY_STATE + " text " + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table id exists " + TABLE_SUBTASK);

        onCreate(db);
    }

    public List<SubTask> getAllSubTasks(){
        List<SubTask> subTasks = new ArrayList<SubTask>();

        String selectQuery = "SELECT  * FROM " + TABLE_SUBTASK + " WHERE "
                + KEY_NAMEID + " = " + String.valueOf(UserContainer.getSelectedID()) + " AND "
                + KEY_TASKID + " = " + String.valueOf(TaskContainer.getSelectedTaskID());

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                subTasks.add(new SubTask(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), cursor.getString(3), cursor.getString(4)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return subTasks;
    }


    public void updateRow(String query){
        SQLiteDatabase db = this.getWritableDatabase();
        db.rawQuery(query, null);
        db.close();
    }



}
