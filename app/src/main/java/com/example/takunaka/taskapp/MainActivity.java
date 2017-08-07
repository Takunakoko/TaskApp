package com.example.takunaka.taskapp;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.example.takunaka.taskapp.adapters.ViewPagerAdapter;
import com.example.takunaka.taskapp.fragments.CreateTaskFragment;
import com.example.takunaka.taskapp.fragments.MainFragment;
import com.example.takunaka.taskapp.sql.DBNamesHelper;
import com.example.takunaka.taskapp.sql.DBTasksHelper;
import com.example.takunaka.taskapp.sqlQuerry.UserContainer;
import com.example.takunaka.taskapp.sqlQuerry.Users;

import java.util.List;

public class MainActivity extends AppCompatActivity {


    private MainFragment mainFragment;
    private CreateTaskFragment createTaskFragment;
    private MenuItem save;
    private MenuItem edit;
    private MenuItem account;
    private MenuItem addTask;

    private Spinner spinnerUsers;

    private DBNamesHelper dbNamesHelper;
    private SQLiteDatabase dbNames;

    private String selectedDialogName;
    private String selectedDialogSurname;

    private EditText selName;
    private EditText selSurname;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        toolbar.setTitle(null);

        //Создание DB
        dbNamesHelper = new DBNamesHelper(this);
        showUsersSelectDialog();

        FragmentTransaction ftrans = getSupportFragmentManager().beginTransaction();
        mainFragment = new MainFragment();
        ftrans.replace(R.id.container, mainFragment);
        ftrans.commit();
        mainFragment.notifyData();


    }

    @Override
    protected void onResume() {
        super.onResume();
        FragmentTransaction ftrans = getSupportFragmentManager().beginTransaction();
        mainFragment = new MainFragment();
        ftrans.replace(R.id.container, mainFragment);
        ftrans.commit();
        mainFragment.notifyData();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        save = menu.findItem(R.id.action_save);
        edit = menu.findItem(R.id.action_edit);
        account = menu.findItem(R.id.account_action);
        addTask = menu.findItem(R.id.addTask);
            save.setVisible(false);
            edit.setVisible(false);
            account.setVisible(true);
            addTask.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.account_action) {
           showUsersSelectDialog();
        }
        if (id == R.id.addTask){
            FragmentTransaction ftrans = getSupportFragmentManager().beginTransaction();
            createTaskFragment = new CreateTaskFragment();
            ftrans.replace(R.id.container, createTaskFragment);
            ftrans.commit();
        }

        return super.onOptionsItemSelected(item);
    }


    //диалог выбора юзера
    public void showUsersSelectDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.Theme_AppCompat_Dialog));
        View view = getLayoutInflater().inflate(R.layout.dialog_login, null);
        spinnerUsers = (Spinner) view.findViewById(R.id.spinnerNamesDialog);
        builder.setTitle("Выбор пользователя")
                .setView(view)
                .setPositiveButton("Выбрать", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UserContainer.setSelectedID(((Users)spinnerUsers.getSelectedItem()).getUserID());
                        UserContainer.setSelectedName(((Users)spinnerUsers.getSelectedItem()).getUserName());
                        UserContainer.setSelectedSurName(((Users)spinnerUsers.getSelectedItem()).getUserSurName());
                        getSupportActionBar().setSubtitle(UserContainer.getFullName());
                    }
                })
                .setNegativeButton("Новый пользователь", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showNewUserDialog();

                    }
                });

        List<Users> names = dbNamesHelper.getAllNames();
        ArrayAdapter<Users> arrayAdapter = new ArrayAdapter<Users>(getApplicationContext(),
                android.R.layout.simple_spinner_item
                , names);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUsers.setAdapter(arrayAdapter);

        AlertDialog alert = builder.create();
        alert.show();

    }

    //диалог добавления нового юзера
    public void showNewUserDialog() {
        dbNames = dbNamesHelper.getWritableDatabase();
        final ContentValues cv = new ContentValues();
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.Theme_AppCompat_Dialog));
        builder.setTitle("Создание пользователя:")
                .setView(R.layout.dialog_add_newuser)
                .setPositiveButton("Создать", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Dialog f = (Dialog) dialog;

                        selName = (EditText) f.findViewById(R.id.nameTextDialog);
                        selSurname = (EditText) f.findViewById(R.id.surNameTextDialog);
                        selectedDialogName = String.valueOf(selName.getText());
                        selectedDialogSurname = String.valueOf(selSurname.getText());

                        if(searchForDoubles()){
                            showWarningDialog();
                        } else {
                            cv.put(DBNamesHelper.KEY_NAME, selectedDialogName);
                            cv.put(DBNamesHelper.KEY_SURNAME, selectedDialogSurname);

                            dbNames.insert(DBNamesHelper.TABLE_NAMES, null, cv);
                            Toast.makeText(getApplicationContext(), "Пользователь добавлен!", Toast.LENGTH_SHORT).show();
                            FragmentTransaction ftrans = getSupportFragmentManager().beginTransaction();
                            createTaskFragment = new CreateTaskFragment();
                            ftrans.replace(R.id.container, createTaskFragment);
                            ftrans.commit();

                        }


                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showUsersSelectDialog();

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    //проверка на существование такого юзера
    public boolean searchForDoubles() {
        dbNames = dbNamesHelper.getWritableDatabase();
        String selUser = selectedDialogName + " " + selectedDialogSurname;

        Cursor cursor = dbNames.query(DBNamesHelper.TABLE_NAMES, null, null, null, null, null, null);

        boolean exist = false;
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBNamesHelper.KEY_ID);
            int nameIndex = cursor.getColumnIndex(DBNamesHelper.KEY_NAME);
            int surNameIndex = cursor.getColumnIndex(DBNamesHelper.KEY_SURNAME);
            while (cursor.moveToNext()) {
                Users user = new Users(cursor.getInt(idIndex), cursor.getString(nameIndex), cursor.getString(surNameIndex));
                String tmp = user.getUserName() + " " + user.getUserSurName();
                if (tmp.equals(selUser)) {
                    exist = true;
                }
            }
        }return exist;
    }

    //диалог с сообщением о уже существующем юзере
    public void showWarningDialog(){
        dbNames = dbNamesHelper.getWritableDatabase();
        final ContentValues cv = new ContentValues();
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.Theme_AppCompat_Dialog));
        builder.setTitle("Такой пользователь уже существует!")
                .setPositiveButton("Выбрать существующего", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showUsersSelectDialog();
                    }
                })
                .setNegativeButton("Создать нового", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cv.put(DBNamesHelper.KEY_NAME, selectedDialogName);
                        cv.put(DBNamesHelper.KEY_SURNAME, selectedDialogSurname);
                        dbNames.insert(DBNamesHelper.TABLE_NAMES, null, cv);
                        Toast.makeText(getApplicationContext(), "Пользователь добавлен!", Toast.LENGTH_SHORT).show();
                        FragmentTransaction ftrans = getSupportFragmentManager().beginTransaction();
                        createTaskFragment = new CreateTaskFragment();
                        ftrans.replace(R.id.container, createTaskFragment);
                        ftrans.commit();

                    }
                });

        AlertDialog alert = builder.create();
        alert.show();


    }


}
