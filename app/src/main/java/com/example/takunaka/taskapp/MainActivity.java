package com.example.takunaka.taskapp;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.takunaka.taskapp.fragments.MainFragment;
import com.example.takunaka.taskapp.sql.DBHelper;
import com.example.takunaka.taskapp.sqlQuerry.UserContainer;
import com.example.takunaka.taskapp.sqlQuerry.Users;

import java.util.List;

public class MainActivity extends AppCompatActivity {


    private MainFragment mainFragment;
    private MenuItem save;
    private MenuItem saveOnCreate;
    private MenuItem edit;
    private MenuItem account;
    private MenuItem addTask;
    private MenuItem search;

    private Spinner spinnerUsers;

    private DBHelper dbHelper;
    private SQLiteDatabase dbNames;

    private String selectedDialogName;
    private String selectedDialogSurname;

    private EditText selName;
    private EditText selSurname;
    private SharedPreferences sPref;

    //sharedpreferences
    private final String SAVED_ID = "saved_id";
    private final String SAVED_NAME = "saved_name";
    private final String SAVED_SURNAME = "saved_surname";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Создание DB
        dbHelper = new DBHelper(this);

        //проверка на изменение состояния/поворот экрана
        //если сейвдинстанс == null - изменения не было
        if (savedInstanceState == null) {
            //проверка на выбранного пользователя
            if (UserContainer.getSelectedID() == 0) {
                //если пользователь не выбран - показать пользователя
                showUsersSelectDialog();
            } else {
                //иначе загрузить пользователя
                loadUser();
                //установить тайтл
                getSupportActionBar().setTitle(UserContainer.getFullName());
                //и инициализировать главный фрагмент
                mainFragment = new MainFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, mainFragment)
                        .commit();
            }
        } else {
            //сейвдинстанс содержит в себе какие то данные - изменение было
            //если пользователь не выбран
            if (UserContainer.getSelectedID() == 0) {
                //показвать диалог
                showUsersSelectDialog();
                //иначе грузить пользователя из созхраненных данных
            }else loadUser();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //сохранение пользователя при выключении приложения
        saveUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //создание меню
        getMenuInflater().inflate(R.menu.menu_main, menu);
        save = menu.findItem(R.id.action_save);
        edit = menu.findItem(R.id.action_edit);
        account = menu.findItem(R.id.account_action);
        addTask = menu.findItem(R.id.addTask);
        saveOnCreate = menu.findItem(R.id.action_save_create);
        search = menu.findItem(R.id.search_action);
        //настройка отображения правильных иконок в зависимости от выбранного фрагмента
            save.setVisible(false);
            edit.setVisible(false);
            account.setVisible(true);
            addTask.setVisible(true);
            saveOnCreate.setVisible(false);
            search.setVisible(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //проверка на нажатие иконки. если это аккаунт
        if (id == R.id.account_action) {
            //показывать диалог выбора/создания пользователя.
           showUsersSelectDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    //Диалог выбора пользователя
    public void showUsersSelectDialog() {
        //получение уже существующих имен из базы данных
        List<Users> names = dbHelper.getAllNames();
        //создание диалога
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.Theme_AppCompat_Dialog));
        //присвоение вида диалога переменной
        final View view = getLayoutInflater().inflate(R.layout.dialog_login, null);
        spinnerUsers = (Spinner) view.findViewById(R.id.spinnerNamesDialog);
        builder.setTitle("Выбор пользователя")
                //установка отображения диалога из переменной
                .setView(view)
                .setNegativeButton("Новый пользователь", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //переход к диалогу создания нового пользователя
                        showNewUserDialog();
                    }
                });
        //проверка на существование хотя бы одного пользователя. Если список пуст - не отображать эту кнопку.
        if(names.size() != 0){
            builder.setPositiveButton("Выбрать", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                        //Установка во временное хранилище айдишника и имени с фамилией
                        UserContainer.setSelectedID(((Users)spinnerUsers.getSelectedItem()).getUserID());
                        UserContainer.setSelectedName(((Users)spinnerUsers.getSelectedItem()).getUserName());
                        UserContainer.setSelectedSurName(((Users)spinnerUsers.getSelectedItem()).getUserSurName());
                    //установка тайтла
                        getSupportActionBar().setTitle(UserContainer.getFullName());
                    //переход на новый фрагмент
                        mainFragment = new MainFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, mainFragment)
                                .commit();

                }
            });
        }


        //адаптер для отображения списка пользователей в спиннере на основании списка полученного из базы данных
        ArrayAdapter<Users> arrayAdapter = new ArrayAdapter<Users>(getApplicationContext(),
                android.R.layout.simple_spinner_item
                , names);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUsers.setAdapter(arrayAdapter);

        //если список не пустой
        if(names!=null){
            //Проверка каждого имени с выбранным пользователем
            for (Users user: names){
                //Создание полного ФИ одной строкой.
                String name = user.getUserSurName() + " " + user.getUserName();
                //если полное ФИ соответствует выбранному имени
                if(name.equals(UserContainer.getFullName())){
                    //установка выбранного имени в спиннере как дефолтного.
                    spinnerUsers.setSelection(user.getUserID() - 1);
                }
            }
        }

        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();

    }

    //Диалог добавления нового юзера
    public void showNewUserDialog() {
        //Получение записываемой базы данных
        dbNames = dbHelper.getWritableDatabase();
        //создание CV для внесения новой строки в таблицу
        final ContentValues cv = new ContentValues();
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.Theme_AppCompat_Dialog));
        builder.setTitle("Создание пользователя:")
                .setView(R.layout.dialog_add_newuser)
                .setPositiveButton("Создать", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Dialog f = (Dialog) dialog;
                        //поиск полей в диалоге
                        selName = (EditText) f.findViewById(R.id.nameTextDialog);
                        selSurname = (EditText) f.findViewById(R.id.surNameTextDialog);
                        //присвоение переменным имени и фамилии
                        selectedDialogName = String.valueOf(selName.getText());
                        selectedDialogSurname = String.valueOf(selSurname.getText());
                        //проверка на пустые поля в обоих инпутах.
                        if (selectedDialogSurname.equals("") && selectedDialogName.equals("")){
                            //если поля путсые - вывести сообщение о необходимости ввести хотя бы одно поле
                            Toast.makeText(f.getContext(), "Необходимо ввести Имя или Фамилию", Toast.LENGTH_SHORT).show();
                            //повторно показать ввод нового пользователя
                            showNewUserDialog();
                        } else {
                            //если поля не пустые
                            //проверка на дубли имен-фамилий
                            if(searchForDoubles()){
                                //если дубли есть показать предупреждение
                                showWarningDialog();
                            } else {
                                //если дублей нет - внести данные в таблицу и перейти к выбору пользователя
                                cv.put(DBHelper.KEY_NAME, selectedDialogName);
                                cv.put(DBHelper.KEY_SURNAME, selectedDialogSurname);
                                //инсерт в таблицу
                                dbNames.insert(DBHelper.TABLE_NAMES, null, cv);
                                //сообщение о том что пользователь добавлен
                                Toast.makeText(getApplicationContext(), "Пользователь добавлен!", Toast.LENGTH_SHORT).show();
                                //переход к диалогу выбора пользователя с обновленным списком
                                showUsersSelectDialog();
                            }
                        }




                    }
                })
                //кнопка отмены возвращает нас обратно на выбор пользователя
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showUsersSelectDialog();

                    }
                });
        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();

    }

    //проверка на существование такого юзера
    public boolean searchForDoubles() {
        //получение базы данных
        dbNames = dbHelper.getWritableDatabase();
        //создание из строк введенных пользователем общего стринга
        String selUser = selectedDialogName + " " + selectedDialogSurname;
        //создание курсора для прохода по базе данных
        Cursor cursor = dbNames.query(DBHelper.TABLE_NAMES, null, null, null, null, null, null);
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
        }return exist;
    }

    //диалог с сообщением о уже существующем юзере
    public void showWarningDialog(){
        //открываем базу данных и создаем CV
        dbNames = dbHelper.getWritableDatabase();
        final ContentValues cv = new ContentValues();
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.Theme_AppCompat_Dialog));
        builder.setTitle("Такой пользователь уже существует!")
                .setPositiveButton("Выбрать существующего", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //если пользоатель хочет выбрать уже существующего пользователя
                        //показать диалог выбора пользователя
                        showUsersSelectDialog();
                        //закрыть базу данных
                        dbNames.close();
                    }
                })
                .setNegativeButton("Создать нового", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //если хочет создать нового
                        //кладем ранее введенные данные в CV
                        cv.put(DBHelper.KEY_NAME, selectedDialogName);
                        cv.put(DBHelper.KEY_SURNAME, selectedDialogSurname);
                        //вставляем их в базу данных
                        dbNames.insert(DBHelper.TABLE_NAMES, null, cv);
                        //выводим сообщение о добавлении
                        Toast.makeText(getApplicationContext(), "Пользователь добавлен!", Toast.LENGTH_SHORT).show();
                        //перебрасываем на выбор пользователя
                        showUsersSelectDialog();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();


    }


    //Метод сохранения пользователя при сворачивании приложения
    public void saveUser(){
        //создание SharedPreferences
        sPref = getPreferences(MODE_PRIVATE);
        //создание контейнера для хранения
        SharedPreferences.Editor ed = sPref.edit();
        //помещение пользователя в хранилище
        ed.putString(SAVED_ID, String.valueOf(UserContainer.getSelectedID()));
        ed.putString(SAVED_NAME, UserContainer.getSelectedName());
        ed.putString(SAVED_SURNAME, UserContainer.getSelectedSurName());
        //отправка
        ed.commit();
    }
    //метод загрузки пользователя при повторном открытии
    public void loadUser(){
        sPref = getPreferences(MODE_PRIVATE);
        String savedID = sPref.getString(SAVED_ID, "");
        //загрузка пользователя из временного хранилища
        UserContainer.setSelectedID(Integer.valueOf(savedID));
        UserContainer.setSelectedName(sPref.getString(SAVED_NAME, ""));
        UserContainer.setSelectedSurName(sPref.getString(SAVED_SURNAME, ""));
    }


    //метод вызывается при восстановлении после изменения ориентации
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        //если произошло изменение состояния
        // если пользователь был ранее не выбран
        if(UserContainer.getFullName() == null){
            //показать диалог выбора пользователя
            showUsersSelectDialog();
        }
        super.onRestoreInstanceState(savedInstanceState);
    }
}
