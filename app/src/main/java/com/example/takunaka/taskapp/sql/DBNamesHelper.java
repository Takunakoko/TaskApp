package com.example.takunaka.taskapp.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by takunaka on 03.08.17.
 */

public class DBNamesHelper extends SQLiteOpenHelper {

   public static final int DATABASE_VERSION = 1;
   public static final String DATABASE_NAME = "NamesDB";
   public static final String TABLE_NAMES = "Names";

   public static final String KEY_ID = "_id";
   public static final String KEY_NAME = "_name";


    public DBNamesHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAMES + " ("
                + KEY_ID + " integer primary key autoincrement, "
                + KEY_NAME + " text" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_NAMES);

        onCreate(db);
    }

    public List<String> getAllNames(){
        List<String> names = new ArrayList<String>();

        String selectQuery = "SELECT  * FROM " + TABLE_NAMES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                names.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return names;
    }


}
