package com.example.takunaka.taskapp.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.takunaka.taskapp.sqlQuerry.SubTask;
import com.example.takunaka.taskapp.sqlQuerry.Task;
import com.example.takunaka.taskapp.sqlQuerry.TaskContainer;
import com.example.takunaka.taskapp.sqlQuerry.UserContainer;
import com.example.takunaka.taskapp.sqlQuerry.Users;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//класс для создания и работы с базой данных
public class DBHelper extends SQLiteOpenHelper {
    //версия базы данных
    private static final int DATABASE_VERSION = 1;
    //имя базы данных
    private static final String DATABASE_NAME = "DataBase";
    //таблица имен
    private static final String TABLE_NAMES = "Names";
    //таблица задач
    private static final String TABLE_TASKS = "Tasks";
    //таблица дел
    private static final String TABLE_SUBTASK = "SubTasks";
    //ID
    private static final String KEY_ID = "_id";
    //Имя
    private static final String KEY_NAME = "_name";
    //Фамилия
    private static final String KEY_SURNAME = "_surname";
    //ID имени
    private static final String KEY_NAMEID = "_nameid";
    //ID задачи
    private static final String KEY_TASKID = "_taskid";
    //описание
    private static final String KEY_DESCRIPTION = "_description";
    //статус
    private static final String KEY_STATE = "_state";
    //дата
    private static final String KEY_DATE = "_date";

    @NonNull
    private ContentValues cv = new ContentValues();

    @NonNull
    private ArrayList<Task> tasks = new ArrayList<>();

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
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
                + KEY_DATE + " integer, "
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
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        //обновление таблиц
        db.execSQL("drop table if exists " + TABLE_NAMES);
        db.execSQL("drop table if exists " + TABLE_TASKS);
        db.execSQL("drop table id exists " + TABLE_SUBTASK);

        onCreate(db);
    }

    //DBNames

    /**
     * метод получения всех имен
     *
     * @return возвращает список имен из базы данных
     */
    @NonNull
    public List<Users> getAllNames() {
        //список имен
        List<Users> names = new ArrayList<>();
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

    /**
     * Создание нового пользователя
     *
     * @param name    Имя
     * @param surName Фамилия
     */
    public void createNewUser(String name, String surName) {
        cv.clear();
        cv.put(DBHelper.KEY_NAME, name);
        cv.put(DBHelper.KEY_SURNAME, surName);
        //инсерт в таблицу
        this.getWritableDatabase().insert(DBHelper.TABLE_NAMES, null, cv);
    }

    /**
     * Поиск дублей в базе
     *
     * @param name    Имя
     * @param surName Фамилия
     * @return возвращает true если найдено совпадение по имени + фамилии
     */
    public boolean searchForDoubles(String name, String surName) {
        //создание из строк введенных пользователем общего стринга
        String selUser = name + " " + surName;
        //создание курсора для прохода по базе данных
        Cursor cursor = this.getWritableDatabase().query(DBHelper.TABLE_NAMES, null, null, null, null, null, null);
        //установка булевой переменной
        boolean exist = false;
        //проход по базе данных
        if (cursor.moveToFirst()) {
            //получение айдишников полей
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
            int surNameIndex = cursor.getColumnIndex(DBHelper.KEY_SURNAME);
            while (cursor.moveToNext()) {
                //создание нового пользователя с такими данными.
                Users user = new Users(cursor.getInt(idIndex), cursor.getString(nameIndex), cursor.getString(surNameIndex));
                //получение его имени и фамилии
                String tmp = user.getUserName() + " " + user.getUserSurName();
                //если ФИ == введенному пользователем ФИ
                if (tmp.equals(selUser)) {
                    //изменять значение переменной на true
                    exist = true;
                }
            }
        }
        cursor.close();
        return exist;
    }

    /**
     * метод получения списка задач
     *
     * @param param    входящий параметр для отправки запроса в базу данных
     * @param dateFrom Дата "С" включенного фильтра
     * @param dateTo   Дата "По" включенного фильтра
     * @return возвращает список в соответствии с входящим параметром
     */
    @NonNull
    public ArrayList<Task> getTasks(@NonNull String param, @Nullable Integer dateFrom, @Nullable Integer dateTo) {
        //получение идентификатора действующего аккаунта пользователя
        String id = String.valueOf(UserContainer.getSelectedID());
        //строка запроса
        String selectQuery = null;
        //в зависимости от параметра отправляется определенный запрос
        switch (param) {
            //открытые отсортированные
            case "openedSort":
                selectQuery = "SELECT * FROM " + TABLE_TASKS
                        + " WHERE " + KEY_NAMEID + " = " + id + " AND " + KEY_STATE + " = " + "'Выполняется'";
                break;
            //все отсортированные
            case "allSort":
                selectQuery = "SELECT * FROM " + TABLE_TASKS
                        + " WHERE " + KEY_NAMEID + " = " + id;
                break;
            //все с фильтром
            case "allFilter":
                selectQuery = "SELECT * FROM " + TABLE_TASKS
                        + " WHERE " + KEY_NAMEID + " = " + id
                        + " AND " + KEY_DATE + " BETWEEN " + dateFrom + " AND " + dateTo;
                break;
            //только открытые с фильтром
            case "openedFilter":
                selectQuery = "SELECT * FROM " + TABLE_TASKS
                        + " WHERE " + KEY_NAMEID + " = " + id
                        + " AND " + KEY_DATE + " BETWEEN " + dateFrom + " AND " + dateTo
                        + " AND " + KEY_STATE + " = " + "'Выполняется'";
                break;
        }
        //заполнение списка
        fillListFromDB(selectQuery);
        //если список не пустой
        if (!tasks.isEmpty()) {
            //сортировка
            sortList(tasks);
            //маркировка
            addMarks(tasks);
        }
        return tasks;
    }

    /**
     * Метод наполняет список на основании запроса
     *
     * @param selectQuery входящий запрос для БД
     */
    private void fillListFromDB(String selectQuery) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        tasks.clear();
        if (cursor.moveToFirst()) {
            do {
                tasks.add(new Task(cursor.getInt(0), cursor.getString(2), cursor.getInt(3), cursor.getString(4)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

    }

    /**
     * добавление в бд новой задачи
     *
     * @param nameText название задачи
     * @param dateText дата задачи
     * @param state    статус задачи
     */
    public void createTask(String nameText, long dateText, String state) {
        cv.clear();
        cv.put(DBHelper.KEY_DESCRIPTION, nameText);
        cv.put(DBHelper.KEY_DATE, dateText);
        cv.put(DBHelper.KEY_STATE, state);
        cv.put(DBHelper.KEY_NAMEID, UserContainer.getSelectedID());
        //вставка новых данных в таблицу тасков
        this.getWritableDatabase().insert(DBHelper.TABLE_TASKS, null, cv);
    }

    /**
     * метод добавления в бд Дел
     *
     * @param subTasks список дел для поочередного создания в БД
     */
    public void createSubTask(@NonNull List<SubTask> subTasks) {
        for (SubTask s : subTasks) {
            //добавление в таблицу сабтасков из временного листа
            cv.clear();
            cv.put(DBHelper.KEY_DESCRIPTION, s.getDescription());
            cv.put(DBHelper.KEY_STATE, s.getState());
            cv.put(DBHelper.KEY_NAMEID, s.getNameID());
            cv.put(DBHelper.KEY_TASKID, getLastTaskID());
            this.getWritableDatabase().insert(DBHelper.TABLE_SUBTASK, null, cv);
        }
    }

    /**
     * метод добавления в бд одного дела
     *
     * @param description описание дела
     * @param state       статус дела
     * @param nameID      id имени, которому дело принадлежит
     * @param taskID      id задачи, к которой дело принадлежит
     */
    public void createSubTask(String description, String state, int nameID, int taskID) {
        cv.clear();
        cv.put(DBHelper.KEY_DESCRIPTION, description);
        cv.put(DBHelper.KEY_STATE, state);
        cv.put(DBHelper.KEY_NAMEID, nameID);
        cv.put(DBHelper.KEY_TASKID, taskID);
        this.getWritableDatabase().insert(DBHelper.TABLE_SUBTASK, null, cv);
    }

    /**
     * закрытие всех дел в задаче
     *
     * @param state статус
     * @param id    идентификатор дела, которое необходимо закрыть
     */
    public void closeAllSubTasks(String state, int id) {
        cv.clear();
        cv.put(DBHelper.KEY_STATE, state);
        this.getWritableDatabase().update(DBHelper.TABLE_TASKS, cv, DBHelper.KEY_ID + " = " + id, null);
    }

    /**
     * Обновление задачи
     *
     * @param nameText новое описание
     * @param dateText новая дата
     * @param state    новый статус
     */
    public void updateTask(String nameText, long dateText, String state) {
        cv.clear();
        cv.put(DBHelper.KEY_DESCRIPTION, nameText);
        cv.put(DBHelper.KEY_DATE, dateText);
        cv.put(DBHelper.KEY_STATE, state);
        this.getWritableDatabase().update(DBHelper.TABLE_TASKS, cv, DBHelper.KEY_ID + " = " + String.valueOf(TaskContainer.getSelectedTask().getTaskID()), null);
    }

    /**
     * сортировка задач по датам
     *
     * @param sortedTasks входящий список задач
     */
    private void sortList(@NonNull ArrayList<Task> sortedTasks) {
        //использование компаратора
        Collections.sort(sortedTasks, new Comparator<Task>() {
            @Override
            public int compare(@NonNull Task t1, @NonNull Task t2) {
                return t1.getDate().compareTo(t2.getDate());
            }
        });
    }

    /**
     * добавление марок для отображения списка
     *
     * @param sortedTask лист с задачами
     */
    private void addMarks(@NonNull ArrayList<Task> sortedTask) {
        //берем первую дату из списка
        int date = sortedTask.get(0).getDate();
        //для первой даты сортированного списка установка типа "1"
        sortedTask.get(0).setType(1);
        //для каждого последующего элемента списка
        for (int i = 1; i < sortedTask.size(); i++) {
            //если дата повторяется - ставим тип 2
            if (sortedTask.get(i).getDate() == date) {
                sortedTask.get(i).setType(2);
            } else {
                //если дата новая - ставим тип 1 и присваиваем переменной date новую дату
                sortedTask.get(i).setType(1);
                date = sortedTask.get(i).getDate();
            }
        }
    }

    /**
     * @return возвращает идентификатор последнего элемента в списке задач
     */
    //получение последнего айдишника в списке тасков
    private int getLastTaskID() {
        String selectQuery = "SELECT " + KEY_ID + " FROM " + TABLE_TASKS
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

    /**
     * получение всех дел к задаче
     *
     * @param id идентификатор задачи
     * @return возвращает список дел
     */
    @NonNull
    public List<SubTask> getAllSubTasks(int id) {
        List<SubTask> subTasks = new ArrayList<>();

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

    /**
     * обновлние статуса у дела
     *
     * @param id     идентификатор дела
     * @param taskID идентификатор задачи
     * @param nameID идентификатор аккаунта
     */
    public void updateState(int id, int taskID, int nameID) {
        cv.clear();
        cv.put(DBHelper.KEY_STATE, "Закрыта");
        //обновление статуса на основании пришедших айдишников
        this.getWritableDatabase().update(DBHelper.TABLE_SUBTASK, cv, DBHelper.KEY_ID + " = " + id + " AND " + DBHelper.KEY_NAMEID
                + " = " + nameID + " AND " + DBHelper.KEY_TASKID + " = " + taskID, null);
    }

}
