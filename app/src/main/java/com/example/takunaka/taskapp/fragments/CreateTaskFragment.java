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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.takunaka.taskapp.R;
import com.example.takunaka.taskapp.sql.DBTasksHelper;
import com.example.takunaka.taskapp.sqlQuerry.UserContainer;

import java.util.Calendar;


public class CreateTaskFragment extends Fragment{


    private DBTasksHelper dbTasksHelper;
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
        name = (EditText) rootView.findViewById(R.id.NameCreateField);
        date = (TextView) rootView.findViewById(R.id.DateCreateField);
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
                month = month + 1;
                year_x = year;
                month_x = month;
                day_x = dayOfMonth;
                dateCalSet = dayOfMonth + "." + month + "." + year;
                date.setText(dateCalSet);
            }
        };

        setHasOptionsMenu(true);
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
            nameText = String.valueOf(name.getText());
            dateText = String.valueOf(date.getText());
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

}
