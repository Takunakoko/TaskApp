package com.example.takunaka.taskapp.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

    public UpdateFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        if(TaskContainer.getSelectedTask().getState().equals("Выполняется")){
            state.setSelection(0);
        }else state.setSelection(1);


        initCal();

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light,
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
                date.setText(dayOfMonth + "." + month + "." + year);
            }
        };

        return rootView;


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menu.findItem(R.id.action_edit).setVisible(false);
        menu.findItem(R.id.account_action).setVisible(false);
        menu.findItem(R.id.action_save).setVisible(true);
        menu.findItem(R.id.addTask).setVisible(false);
        menu.findItem(R.id.action_save_create).setVisible(false);

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

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                showTaskFragment = new ShowTaskFragment();
                fragmentTransaction.replace(R.id.container, showTaskFragment, "Show");
                fragmentTransaction.commit();
                db.close();
            }
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
        String[] date = TaskContainer.getSelectedTask().getDate().split("\\.");
        year_x = Integer.valueOf(date[2]);
        int month = Integer.valueOf(date[1]);
        month_x = month - 1;
        day_x = Integer.valueOf(date[0]);
    }

}
