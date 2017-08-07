package com.example.takunaka.taskapp.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.takunaka.taskapp.R;
import com.example.takunaka.taskapp.adapters.RecyclerViewCreateSubItemAdapt;
import com.example.takunaka.taskapp.adapters.RecyclerViewSubItemAdapter;
import com.example.takunaka.taskapp.sql.DBSubTasksHelper;
import com.example.takunaka.taskapp.sql.DBTasksHelper;
import com.example.takunaka.taskapp.sqlQuerry.UserContainer;


public class CreateTaskFragment extends Fragment {


    private DBTasksHelper dbTasksHelper;
    private SQLiteDatabase dbTasks;
    private EditText name;
    private EditText date;
    private String nameText;
    private String dateText;
    private MainFragment mainFragment;
    private Button addSubItemBtn;
    DBSubTasksHelper dbSubTasksHelper;

    View rootView;

    public CreateTaskFragment() {

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_create_task, container, false);

        dbTasksHelper = new DBTasksHelper(getContext());
        dbTasks = dbTasksHelper.getWritableDatabase();
        name = (EditText) rootView.findViewById(R.id.NameUpdateField);
        date = (EditText) rootView.findViewById(R.id.DateUpdateField);
        nameText = String.valueOf(name.getText());
        dateText = String.valueOf(date.getText());

        setHasOptionsMenu(true);

        addSubItemBtn = (Button) rootView.findViewById(R.id.addSubCreateTaskButton);
        addSubItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
                RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.recyclerViewCreate);
                RecyclerViewCreateSubItemAdapt adapter = new RecyclerViewCreateSubItemAdapt(dbSubTasksHelper.getAllSubTasks(), rootView.getContext());
                rv.setHasFixedSize(true);
                rv.setAdapter(adapter);
                LinearLayoutManager llm = new LinearLayoutManager(rootView.getContext());
                rv.setLayoutManager(llm);
            }
        });



        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menu.findItem(R.id.action_edit).setVisible(false);
        menu.findItem(R.id.account_action).setVisible(false);
        menu.findItem(R.id.addTask).setVisible(false);
        menu.findItem(R.id.action_save).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_save) {
            final ContentValues cv = new ContentValues();
            cv.put(DBTasksHelper.KEY_DESCRIPTION, nameText);
            cv.put(DBTasksHelper.KEY_DATE, dateText);
            cv.put(DBTasksHelper.KEY_STATE, "Выполняется");
            cv.put(DBTasksHelper.KEY_NAMEID, UserContainer.getSelectedID());
            dbTasks.insert(DBTasksHelper.TABLE_TASKS, null, cv);

            Toast.makeText(getContext(), "Задача создана", Toast.LENGTH_SHORT).show();

            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            mainFragment = new MainFragment();
            fragmentTransaction.replace(R.id.container, mainFragment, "Create");
            fragmentTransaction.addToBackStack("Main");
            fragmentTransaction.commit();
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDialog() {
        dbSubTasksHelper.getWritableDatabase();
        final ContentValues cv = new ContentValues();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Добавить дело")
                .setView(R.layout.dialog_add_description)
                .setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        View view = (View) dialog;
                        EditText description = (EditText) view.findViewById(R.id.descriptionDialog);
                        //cv.put(dbSubTasksHelper.KEY_DESCRIPTION, description.getText().toString());

                        //TODO сохранять сабтаски в лист, затем отправлять их в базу данных для отображения
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }


}
