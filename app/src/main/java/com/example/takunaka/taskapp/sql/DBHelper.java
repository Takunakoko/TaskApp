package com.example.takunaka.taskapp.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.takunaka.taskapp.sqlQuerry.SubTask;
import com.example.takunaka.taskapp.sqlQuerry.Task;
import com.example.takunaka.taskapp.sqlQuerry.UserContainer;
import com.example.takunaka.taskapp.sqlQuerry.Users;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by takunaka on 03.08.17.
 */
//класс для создания и работы с базой данных
public class DBHelper extends SQLiteOpenHelper {
    //версия базы данных
    public static final int DATABASE_VERSION = 1;
    //имя базы данных
    public static final String DATABASE_NAME = "DataBase";
    //таблица имен
    public static final String TABLE_NAMES = "Names";
    //таблица тасков
    public static final String TABLE_TASKS = "Tasks";
    //таблица дел
    public static final String TABLE_SUBTASK = "SubTasks";
    //поля для разных таблиц
    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "_name";
    public static final String KEY_SURNAME = "_surname";
    public static final String KEY_NAMEID = "_nameid";
    public static final String KEY_TASKID = "_taskid";
    public static final String KEY_DESCRIPTION = "_description";
    public static final String KEY_STATE = "_state";
    public static final String KEY_DATE = "_date";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //создание таблицы имен
        db.execSQL("create table " + TABLE_NAMES + " ("
                + KEY_ID + " integer primary key autoincrement, "
                + KEY_NAME + " text, "
                + KEY_SURNAME + " text" + ")");
        //создание таблицы тасков
        db.execSQL("create table " + TABLE_TASKS + " ("
                + KEY_ID + " integer primary key autoincrement, "
                + KEY_NAMEID + " integer, "
                + KEY_DESCRIPTION + " text, "
                + KEY_DATE + " numeric, "
                + KEY_STATE + " text " + ")");
        //создание таблицы дел
        db.execSQL("create table " + TABLE_SUBTASK + " ("
                + KEY_ID + " integer primary key autoincrement, "
                + KEY_NAMEID + " integer, "
                + KEY_TASKID + " integer, "
                + KEY_DESCRIPTION + " text, "
                + KEY_STATE + " text " + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //обновление таблиц
        db.execSQL("drop table if exists " + TABLE_NAMES);
        db.execSQL("drop table if exists " + TABLE_TASKS);
        db.execSQL("drop table id exists " + TABLE_SUBTASK);

        onCreate(db);
    }
    //DBNames
    //метод получения всех имен из таблицы
    public List<Users> getAllNames(){
        //список имен
        List<Users> names = new ArrayList<Users>();
        //запрос
        String selectQuery = "SELECT * FROM " + TABLE_NAMES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                //добавление нового юзера в список
                names.add(new Users(cursor.getInt(0), cursor.getString(1), cursor.getString(2)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        //возврат списка
        return names;
    }

    //DBTASKS
    //метод получения всех тасков
    public ArrayList<Task> getAllTasks(){
        String id = String.valueOf(UserContainer.getSelectedID());
        ArrayList<Task> tasks = new ArrayList<Task>();
        //запрос
        String selectQuery = "SELECT * FROM " + TABLE_TASKS
                + " WHERE " + KEY_NAMEID + " = " + id ;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                //добавление в список
                tasks.add(new Task(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        //возврат списка
        return tasks;
    }
    //метод получения всех открытых задач из базы данных
    public ArrayList<Task> getOpenedTask(){
        String id = String.valueOf(UserContainer.getSelectedID());
        ArrayList<Task> tasks = new ArrayList<Task>();

        String selectQuery = "SELECT * FROM " + TABLE_TASKS
                + " WHERE " + KEY_NAMEID + " = " + id + " AND " + KEY_STATE + " = " + "'Выполняется'" ;

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
    //метод получения всех открытых задач из бд с сортировкой листа перед возвратом
    public ArrayList<Task> getOpenedSortedTask(){
        String id = String.valueOf(UserContainer.getSelectedID());
        ArrayList<Task> tasks = new ArrayList<Task>();

        String selectQuery = "SELECT * FROM " + TABLE_TASKS
                + " WHERE " + KEY_NAMEID + " = " + id + " AND " + KEY_STATE + " = " + "'Выполняется'" ;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                tasks.add(new Task(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        sortlist(tasks);
        return tasks;
    }
    //получение всех задач из бд с сортировкой листа перед возвратом
    public ArrayList<Task> getAllSortedTasks(){
        String id = String.valueOf(UserContainer.getSelectedID());
        ArrayList<Task> tasks = new ArrayList<Task>();

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
        sortlist(tasks);
        return tasks;
    }
    //метод сортировки листа сравнением дат
    public ArrayList<Task> sortlist(ArrayList<Task> sortdedTasks){
        //использование компаратора
        Collections.sort(sortdedTasks, new Comparator<Task>() {
            @Override
            public int compare(Task t1, Task t2) {
                //создание SDF для приведение даты из базы данных (Стринг) к дате(Date)
                SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
                Date date1 = null;
                Date date2 = null;
                //парсинг стрингов в даты
                try {
                    date1 = formatter.parse(t1.getDate());
                    date2 = formatter.parse(t2.getDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //сравнение двух дат
                return date1.compareTo(date2);
            }
        });
        //возврат сортированного листа
        return sortdedTasks;
    }
    //получение последнего айдишника в списке тасков
    public int getLastTaskID(){

        String selectQuery = "SELECT "+ KEY_ID +" FROM " + TABLE_TASKS
                + " ORDER BY " + KEY_ID + " DESC LIMIT 1";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int id = 0;
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return id;
    }

    //DBSUBTASKS
    //получение листа всех сабтасков
    public List<SubTask> getAllSubTasks(int id){
        List<SubTask> subTasks = new ArrayList<SubTask>();

        String selectQuery = "SELECT  * FROM " + TABLE_SUBTASK + " WHERE "
                + KEY_NAMEID + " = " + String.valueOf(UserContainer.getSelectedID()) + " AND "
                + KEY_TASKID + " = " + String.valueOf(id);

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
    //изменение статуса у выбранного сабтаска
    public void updateState(int id, int taskID, int nameID){
        ContentValues cv = new ContentValues();
        cv.put(this.KEY_STATE, "Закрыта");
        SQLiteDatabase db = this.getWritableDatabase();
        //обновление статуса на основании пришедших айдишников
        db.update(this.TABLE_SUBTASK, cv, this.KEY_ID + " = " + id + " AND " + this.KEY_NAMEID
                + " = " + nameID + " AND " + this.KEY_TASKID + " = " + taskID, null);
        db.close();
    }

}
