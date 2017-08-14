package com.example.takunaka.taskapp.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.takunaka.taskapp.R;
import com.example.takunaka.taskapp.adapters.RecyclerViewSubItemAdapter;
import com.example.takunaka.taskapp.adapters.RecyclerViewSubItemOnCreateAdapter;
import com.example.takunaka.taskapp.sql.DBSubTasksHelper;
import com.example.takunaka.taskapp.sql.DBTasksHelper;
import com.example.takunaka.taskapp.sqlQuerry.SubTask;
import com.example.takunaka.taskapp.sqlQuerry.UserContainer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class CreateTaskFragment extends Fragment implements View.OnClickListener {


    private DBTasksHelper dbTasksHelper;
    private DBSubTasksHelper dbSubTasksHelper;
    private SQLiteDatabase dbTasks;
    private EditText name;
    private TextView date;
    private String nameText;
    private String dateText;
    private String dateCalSet;
    private MainFragment mainFragment;
    private View rootView;
    private int year_x, month_x, day_x;
    private DatePickerDialog.OnDateSetListener mDateSetListner;
    private Button addSubTsk;
    private RecyclerViewSubItemOnCreateAdapter adapter;
    private List<SubTask> subTasks;
    private RecyclerView rv;

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
        setRetainInstance(true);
        dbTasksHelper = new DBTasksHelper(getContext());
        dbSubTasksHelper = new DBSubTasksHelper(getContext());
        dbTasks = dbTasksHelper.getWritableDatabase();
        name = (EditText) rootView.findViewById(R.id.NameCreateField);
        name.requestFocus();
        date = (TextView) rootView.findViewById(R.id.DateCreateField);
        addSubTsk = (Button) rootView.findViewById(R.id.addSubShowTaskButtonOnCreate);
        rv = (RecyclerView) rootView.findViewById(R.id.recyclerViewOnCreate);

        subTasks = new ArrayList<>();
        addSubTsk.setOnClickListener(this);

        initCal();
        date.setText(day_x + "." + (month_x + 1) + "." + year_x);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog dialog = new DatePickerDialog(getContext(), R.style.Theme_AppCompat_Dialog,
                        mDateSetListner, year_x, month_x, day_x);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListner = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                year_x = year;
                month_x = month;
                day_x = dayOfMonth;
                dateCalSet = dayOfMonth + "." + (month + 1) + "." + year;
                date.setText(dateCalSet);
            }
        };


        adapter = new RecyclerViewSubItemOnCreateAdapter(subTasks, rootView.getContext());
        rv.setHasFixedSize(true);
        rv.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(rootView.getContext());
        rv.setLayoutManager(llm);

        setHasOptionsMenu(true);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        return rootView;
    }




    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menu.findItem(R.id.action_edit).setVisible(false);
        menu.findItem(R.id.account_action).setVisible(false);
        menu.findItem(R.id.addTask).setVisible(false);
        menu.findItem(R.id.action_save).setVisible(false);
        menu.findItem(R.id.action_save_create).setVisible(true);
        menu.findItem(R.id.search_action).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_save_create) {
            nameText = String.valueOf(name.getText());
            dateText = String.valueOf(date.getText());
            if(nameText.equals("") || subTasks.size() == 0){
                Toast.makeText(getContext(), "Нужно ввести название задачи и создать хотя бы одно дело", Toast.LENGTH_SHORT).show();
            }else {
                final ContentValues cv = new ContentValues();
                cv.put(DBTasksHelper.KEY_DESCRIPTION, nameText);
                cv.put(DBTasksHelper.KEY_DATE, dateText);
                cv.put(DBTasksHelper.KEY_STATE, "Выполняется");
                cv.put(DBTasksHelper.KEY_NAMEID, UserContainer.getSelectedID());
                dbTasks.insert(DBTasksHelper.TABLE_TASKS, null, cv);

                SQLiteDatabase dbSubTask = dbSubTasksHelper.getWritableDatabase();
                int lastID = dbTasksHelper.getLastTaskID();
                for (SubTask s: subTasks){
                    final ContentValues cvSub = new ContentValues();
                    cvSub.put(DBSubTasksHelper.KEY_DESCRIPTION, s.getDescription());
                    cvSub.put(DBSubTasksHelper.KEY_STATE, s.getState());
                    cvSub.put(DBSubTasksHelper.KEY_NAMEID, s.getNameID());
                    cvSub.put(DBSubTasksHelper.KEY_TASKID, lastID);
                    dbSubTask.insert(dbSubTasksHelper.TABLE_SUBTASK, null, cvSub);
                }

                dbSubTask.close();
                dbSubTasksHelper.close();
                dbTasks.close();
                dbTasksHelper.close();
                Toast.makeText(getContext(), "Задача создана", Toast.LENGTH_SHORT).show();
                mainFragment = new MainFragment();
                getFragmentManager().beginTransaction()
                .replace(R.id.container, mainFragment)
                .commit();
            }

        }
        if (id == android.R.id.home){
            mainFragment = new MainFragment();
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, mainFragment)
                    .commit();
        }

        return super.onOptionsItemSelected(item);
    }

    public Dialog showDatePickDialog(){
        return new DatePickerDialog(getContext(), dPickerListner, year_x, month_x, day_x );
    }

    private DatePickerDialog.OnDateSetListener dPickerListner
            = new DatePickerDialog.OnDateSetListener(){

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            year_x = year;
            month_x = month;
            day_x = dayOfMonth;
        }
    };

    public void initCal(){
        final Calendar cal = Calendar.getInstance();
        year_x = cal.get(Calendar.YEAR);
        month_x = cal.get(Calendar.MONTH);
        day_x = cal.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public void onClick(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(new android.view.ContextThemeWrapper(getContext(), R.style.Theme_AppCompat_Dialog));
        final View dialogview = View.inflate(getContext(), R.layout.dialog_add_description, null);
        builder.setTitle("Добавить дело")
                .setView(dialogview)
                .setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText description = (EditText) dialogview.findViewById(R.id.descriptionDialog);
                        subTasks.add(new SubTask(UserContainer.getSelectedID(), description.getText().toString(), "В работе"));
                        adapter.notifyDataSetChanged();
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
