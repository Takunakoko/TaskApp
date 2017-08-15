package com.example.takunaka.taskapp.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.takunaka.taskapp.Configurator;
import com.example.takunaka.taskapp.R;
import com.example.takunaka.taskapp.sql.DBSubTasksHelper;
import com.example.takunaka.taskapp.sql.DBTasksHelper;
import com.example.takunaka.taskapp.sqlQuerry.SubTask;
import com.example.takunaka.taskapp.sqlQuerry.Task;
import com.example.takunaka.taskapp.sqlQuerry.TaskContainer;

import java.util.Calendar;
import java.util.List;


public class UpdateFragment extends Fragment {

    private ShowTaskFragment showTaskFragment;
    private EditText name;
    private TextView date;
    private Spinner state;
    private DBTasksHelper dbTasksHelper;
    private String selectedID;
    private int year_x, month_x, day_x;
    private DatePickerDialog.OnDateSetListener mDateSetListner;
    private Configurator config = Configurator.getInstance();
    MenuItem saveItem;
    int currentPosition;

    public UpdateFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_update, container, false);
        name = (EditText) rootView.findViewById(R.id.NameUpdateField);
        date = (TextView) rootView.findViewById(R.id.DateUpdateField);
        state = (Spinner) rootView.findViewById(R.id.StateUpdateField);

        name.setText(TaskContainer.getSelectedTask().getDesription());
        date.setText(TaskContainer.getSelectedTask().getDate());
        selectedID = String.valueOf(TaskContainer.getSelectedTask().getTaskID());




        String[] states = { "Выполняется", "Закрыта"};

        ArrayAdapter<String> spinnerItemsAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, states);
        spinnerItemsAdapter.setDropDownViewResource(R.layout.spinner_item);
        state.setAdapter(spinnerItemsAdapter);


        if(TaskContainer.getSelectedTask().getState().equals("Выполняется")){
            state.setSelection(0);
            currentPosition = 0;
        }else {
            state.setSelection(1);
            currentPosition = 1;
        }

        initCal();

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog dialog = new DatePickerDialog(getContext(), R.style.Theme_AppCompat_Dialog,
                        mDateSetListner, year_x, month_x, day_x);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));
                dialog.show();
            }
        });

        mDateSetListner = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                year_x = year;
                month_x = month;
                day_x = dayOfMonth;
                date.setText(dayOfMonth + "." + (month + 1) + "." + year);
            }
        };

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);


        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(TaskContainer.getSelectedTask().getState().equals("В работе")){
                    saveItem.setVisible(false);
                }else if (TaskContainer.getSelectedTask().getState().equals("Закрыта")){
                    saveItem.setVisible(false);
                }
                saveItem.setVisible(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                saveItem.setVisible(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (currentPosition == 0) {
                    if (position == 1) {
                        saveItem.setVisible(true);
                    }
                }
                if (currentPosition == 1) {
                    if (position == 0) {
                        saveItem.setVisible(true);
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return rootView;


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        saveItem = menu.findItem(R.id.action_save).setVisible(false);
        menu.findItem(R.id.action_edit).setVisible(false);
        menu.findItem(R.id.account_action).setVisible(false);
        menu.findItem(R.id.addTask).setVisible(false);
        menu.findItem(R.id.action_save_create).setVisible(false);
        menu.findItem(R.id.search_action).setVisible(false);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_save) {

            if(name.getText().equals("")){
                Toast.makeText(getContext(), "Название не может быть пустым!", Toast.LENGTH_SHORT).show();
            }else {
                dbTasksHelper = new DBTasksHelper(getContext());
                SQLiteDatabase db = dbTasksHelper.getWritableDatabase();

                ContentValues cv = new ContentValues();
                cv.put(DBTasksHelper.KEY_DESCRIPTION, name.getText().toString());
                cv.put(DBTasksHelper.KEY_DATE, date.getText().toString());
                cv.put(DBTasksHelper.KEY_STATE, state.getSelectedItem().toString());

                db.update(dbTasksHelper.TABLE_TASKS, cv, dbTasksHelper.KEY_ID +" = " + selectedID, null);

                if(state.getSelectedItem().toString().equals("Закрыта")){
                    config.setClosed(true);
                    DBSubTasksHelper dbSubTasksHelper = new DBSubTasksHelper(getContext());
                    List<SubTask> subTasks = dbSubTasksHelper.getAllSubTasks(Integer.valueOf(selectedID));
                    for (SubTask s : subTasks){
                        dbSubTasksHelper.updateState(s.getId(), s.getTaskID(), s.getNameID());
                    }
                }
                MainFragment mainFragment = new MainFragment();
                getFragmentManager().beginTransaction()
                .replace(R.id.container, mainFragment)
                .commit();
                db.close();
            }
        }
        if (id == android.R.id.home){
            showTaskFragment = new ShowTaskFragment();
            getFragmentManager().beginTransaction()
            .replace(R.id.container, showTaskFragment)
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
        String[] _date = date.getText().toString().split("\\.");
        year_x = Integer.valueOf(_date[2]);
        int month = Integer.valueOf(_date[1]);
        month_x = month - 1;
        day_x = Integer.valueOf(_date[0]);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("DateCreate", date.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        if(savedInstanceState != null){
            date.setText(savedInstanceState.getString("DateCreate"));
            initCal();
        }
        super.onViewStateRestored(savedInstanceState);
    }
}
