package com.example.takunaka.taskapp.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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



}
