package com.example.takunaka.taskapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.example.takunaka.taskapp.fragments.CreateTaskFragment;
import com.example.takunaka.taskapp.fragments.MainFragment;
import com.example.takunaka.taskapp.fragments.ShowTaskFragment;
import com.example.takunaka.taskapp.fragments.UpdateFragment;
import com.example.takunaka.taskapp.sql.DBHelper;
import com.example.takunaka.taskapp.sqlQuerry.UserContainer;
import com.example.takunaka.taskapp.sqlQuerry.Users;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    //спиннер выбора пользователя для диалога
    private Spinner spinnerUsers;
    //база данных
    private DBHelper dbHelper;
    //временные стринги для хранения введенных имени/фамилии при создании
    private String selectedDialogName;
    private String selectedDialogSurname;
    //sharedPreferences
    private SharedPreferences sPref;
    private final String SAVED_ID = "saved_id";
    private final String SAVED_NAME = "saved_name";
    private final String SAVED_SURNAME = "saved_surname";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //инициализация тулбара
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //назначение тулбара в виде экшбара
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
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(UserContainer.getFullName());
                //и инициализировать главный фрагмент
                changeFragment("Main");
            }
        } else {
            //сейвдинстанс содержит в себе какие то данные - изменение было
            //если пользователь не выбран
            if (UserContainer.getSelectedID() == 0) {
                //показвать диалог
                showUsersSelectDialog();
                //иначе грузить пользователя из созхраненных данных
            } else {
                loadUser();
                //установить тайтл
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(UserContainer.getFullName());
            }
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
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        //настройка отображения правильных иконок в зависимости от выбранного фрагмента
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_save).setVisible(false);
        menu.findItem(R.id.action_edit).setVisible(false);
        menu.findItem(R.id.account_action).setVisible(true);
        menu.findItem(R.id.addTask).setVisible(true);
        menu.findItem(R.id.action_save_create).setVisible(false);
        menu.findItem(R.id.search_action).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        //проверка на нажатие иконки. если это аккаунт
        if (id == R.id.account_action) {
            //показывать диалог выбора/создания пользователя.
            showUsersSelectDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Диалог выбора пользователя
     */
    public void showUsersSelectDialog() {
        //получение уже существующих имен из базы данных
        List<Users> names = dbHelper.getAllNames();
        //проверка на существование хотя бы одного пользователя. Если список пуст - не отображать эту кнопку.
        if (!names.isEmpty()) {
            //создание диалога
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.Theme_AppCompat_Dialog));
            View view = getLayoutInflater().inflate(R.layout.dialog_login, null);
            spinnerUsers = (Spinner) view.findViewById(R.id.spinnerNamesDialog);
            builder.setTitle(R.string.user_select)
                    //установка отображения диалога из переменной
                    .setView(view)
                    //кнопка нового пользователя
                    .setNegativeButton(R.string.new_user, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //переход к диалогу создания нового пользователя
                            showNewUserDialog();
                        }
                    })
                    //кнопка выбора пользователя
                    .setPositiveButton(R.string.select, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Установка во временное хранилище айдишника и имени с фамилией
                            UserContainer.setSelectedID(((Users) spinnerUsers.getSelectedItem()).getUserID());
                            UserContainer.setSelectedName(((Users) spinnerUsers.getSelectedItem()).getUserName());
                            UserContainer.setSelectedSurName(((Users) spinnerUsers.getSelectedItem()).getUserSurName());
                            //установка тайтла
                            if (getSupportActionBar() != null)
                                getSupportActionBar().setTitle(UserContainer.getFullName());
                            //переход на новый фрагмент
                            changeFragment("Main");
                        }
                    });

            //адаптер для отображения списка пользователей в спиннере на основании списка полученного из базы данных
            ArrayAdapter<Users> arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                    android.R.layout.simple_spinner_item
                    , names);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerUsers.setAdapter(arrayAdapter);

            //Проверка каждого имени с выбранным пользователем
            for (Users user : names) {
                //Создание полного ФИ одной строкой.
                String name = user.getUserSurName() + " " + user.getUserName();
                //если полное ФИ соответствует выбранному имени
                if (name.equals(UserContainer.getFullName())) {
                    //установка выбранного имени в спиннере как дефолтного.
                    spinnerUsers.setSelection(user.getUserID() - 1);
                }
            }

            AlertDialog alert = builder.create();
            alert.setCanceledOnTouchOutside(false);
            alert.show();
        } else {
            //если пользователей не существует - открывать сразу создание пользователя
            showNewUserDialog();
        }

    }

    /**
     * Диалог добавления нового юзера
     */
    public void showNewUserDialog() {
        //создание CV для внесения новой строки в таблицу
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.Theme_AppCompat_Dialog));
        builder.setTitle(R.string.new_user_create)
                .setView(R.layout.dialog_add_newuser)
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Dialog f = (Dialog) dialog;
                        //присвоение переменным имени и фамилии
                        selectedDialogName = String.valueOf(((EditText) f.findViewById(R.id.nameTextDialog)).getText());
                        selectedDialogSurname = String.valueOf(((EditText) f.findViewById(R.id.surNameTextDialog)).getText());
                        //проверка на пустые поля в обоих инпутах.
                        if (selectedDialogSurname.isEmpty() && selectedDialogName.isEmpty()) {
                            //если поля путсые - вывести сообщение о необходимости ввести хотя бы одно поле
                            Toast.makeText(f.getContext(), R.string.create_empty_error_message, Toast.LENGTH_SHORT).show();
                            //повторно показать ввод нового пользователя
                            showNewUserDialog();
                        } else {
                            //если поля не пустые
                            //проверка на дубли имен-фамилий
                            if (dbHelper.searchForDoubles(selectedDialogName, selectedDialogSurname)) {
                                //если дубли есть показать предупреждение
                                showWarningDialog();
                            } else {
                                //если дублей нет - внести данные в таблицу и перейти к выбору пользователя
                                dbHelper.createNewUser(selectedDialogName, selectedDialogSurname);
                                //сообщение о том что пользователь добавлен
                                Toast.makeText(getApplicationContext(), R.string.create_success_message, Toast.LENGTH_SHORT).show();
                                //переход к диалогу выбора пользователя с обновленным списком
                                showUsersSelectDialog();
                            }
                        }
                    }
                })
                //кнопка отмены возвращает нас обратно на выбор пользователя
                .setNegativeButton(R.string.abort, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showUsersSelectDialog();

                    }
                });
        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();

    }

    /**
     * диалог с сообщением о уже существующем юзере
     */
    public void showWarningDialog() {
        //открываем базу данных и создаем CV
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.Theme_AppCompat_Dialog));
        builder.setTitle(R.string.user_already_exists)
                .setPositiveButton(R.string.choose_exists, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //если пользоатель хочет выбрать уже существующего пользователя
                        //показать диалог выбора пользователя
                        showUsersSelectDialog();
                        //закрыть базу данных
                    }
                })
                .setNegativeButton(R.string.create_new, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //если хочет создать нового
                        dbHelper.createNewUser(selectedDialogName, selectedDialogSurname);
                        //выводим сообщение о добавлении
                        Toast.makeText(getApplicationContext(), R.string.create_success_message, Toast.LENGTH_SHORT).show();
                        //перебрасываем на выбор пользователя
                        showUsersSelectDialog();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();


    }

    /**
     * Метод сохранения пользователя при сворачивании приложения
     */
    public void saveUser() {
        //создание SharedPreferences
        sPref = getPreferences(MODE_PRIVATE);
        //создание контейнера для хранения
        SharedPreferences.Editor ed = sPref.edit();
        //помещение пользователя в хранилище
        ed.putString(SAVED_ID, String.valueOf(UserContainer.getSelectedID()));
        ed.putString(SAVED_NAME, UserContainer.getSelectedName());
        ed.putString(SAVED_SURNAME, UserContainer.getSelectedSurName());
        //отправка
        ed.apply();
    }

    /**
     * метод загрузки пользователя при повторном открытии
     */
    public void loadUser() {
        sPref = getPreferences(MODE_PRIVATE);
        String savedID = sPref.getString(SAVED_ID, "");
        //загрузка пользователя из временного хранилища
        UserContainer.setSelectedID(Integer.valueOf(savedID));
        UserContainer.setSelectedName(sPref.getString(SAVED_NAME, ""));
        UserContainer.setSelectedSurName(sPref.getString(SAVED_SURNAME, ""));
    }

    /**
     * метод вызывается при восстановлении после изменения ориентации
     *
     * @param savedInstanceState Bundle с сохраненными параметрами
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        //если произошло изменение состояния
        // если пользователь был ранее не выбран
        if (UserContainer.getFullName() == null) {
            //показать диалог выбора пользователя
            showUsersSelectDialog();
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * метод смены фрагмента
     *
     * @param caseName параметр на основании которого происходит смена фрагмента
     */
    public void changeFragment(@NonNull String caseName) {
        //Фрагменты
        MainFragment mainFragment = new MainFragment();
        CreateTaskFragment create = new CreateTaskFragment();
        ShowTaskFragment show = new ShowTaskFragment();
        UpdateFragment update = new UpdateFragment();
        // В зависимости от входящего caseName
        switch (caseName) {
            case "Main":
                //Заменяет контейнер фрагментов на MainFragment
                getSupportFragmentManager().popBackStack();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, mainFragment)
                        .commit();
                break;
            case "Create":
                //Заменяет контейнер фрагментов на CreateFragment
                getSupportFragmentManager().popBackStack();
                getSupportFragmentManager().beginTransaction()
                        .add(create, "Main")
                        .replace(R.id.container, create)
                        .addToBackStack(null)
                        .commit();
                break;
            case "ShowTask":
                //Заменяет контейнер фрагментов на ShowTaskFragment
                getSupportFragmentManager().popBackStack();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, show)
                        .addToBackStack(null)
                        .commit();
                break;
            case "Update":
                //Заменяет контейнер фрагментов на UpdateFragment
                getSupportFragmentManager().popBackStack();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, update)
                        .addToBackStack(null)
                        .commit();
                break;
        }
    }
}
