package com.example.takunaka.taskapp;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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


import com.example.takunaka.taskapp.fragments.MainFragment;
import com.example.takunaka.taskapp.sql.DBNamesHelper;
import com.example.takunaka.taskapp.sqlQuerry.UsersContainer;

import java.util.List;

public class MainActivity extends AppCompatActivity {


    private MainFragment mainFragment;
    private MenuItem save;
    private MenuItem edit;
    private MenuItem account;
    private Spinner spinnerUsers;
    Dialog fcontext;
    private DBNamesHelper dbNamesHelper;
    private SQLiteDatabase dbNames;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setSubtitle("Кураев Алексей");
        toolbar.setTitle(null);

        //Создание DB
        dbNamesHelper = new DBNamesHelper(this);

        showUsersSelectDialog();

        FragmentTransaction ftrans = getSupportFragmentManager().beginTransaction();
        mainFragment = new MainFragment();
        ftrans.replace(R.id.container, mainFragment);
        ftrans.commit();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO сделать переход на страницу создания новой таски
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        save = menu.findItem(R.id.action_save);
        edit = menu.findItem(R.id.action_edit);
        account = menu.findItem(R.id.account_action);
            save.setVisible(false);
            edit.setVisible(false);
            account.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.account_action) {
            showUsersSelectDialog();
        }

        return super.onOptionsItemSelected(item);
    }



    public void showUsersSelectDialog() {
        dbNames = dbNamesHelper.getWritableDatabase();
        Cursor cursor = dbNames.query(DBNamesHelper.TABLE_NAMES, null, null, null, null, null, null);
        /*if(cursor.moveToFirst()){
            int idIndex = cursor.getColumnIndex(DBNamesHelper.KEY_ID);
            int nameIndex = cursor.getColumnIndex(DBNamesHelper.KEY_NAME);
            do {
                UsersContainer.addNewUser(cursor.getInt(idIndex), cursor.getString(nameIndex));
            } while (cursor.moveToNext());

        }*/

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.Theme_AppCompat_Dialog));
        View view = getLayoutInflater().inflate(R.layout.dialog_login, null);
        spinnerUsers = (Spinner) view.findViewById(R.id.spinnerNamesDialog);
        builder.setTitle("Выбор пользователя")
                .setView(view)
                .setPositiveButton("Выбрать", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String selectedText = spinnerUsers.getSelectedItem().toString();

                    }
                })
                .setNegativeButton("Новый пользователь", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showNewUserDialog();

                    }
                });

        List<String> names = dbNamesHelper.getAllNames();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item
                , names);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUsers.setAdapter(arrayAdapter);

        AlertDialog alert = builder.create();
        alert.show();

    }




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

                        EditText text = (EditText) f.findViewById(R.id.nameTextDialog);
                        String name = String.valueOf(text.getText());
                        cv.put(DBNamesHelper.KEY_NAME, name);
                        dbNames.insert(DBNamesHelper.TABLE_NAMES, null, cv);
                        showUsersSelectDialog();
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



    public void setSpinner(){

    }

}
