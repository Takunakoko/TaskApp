package com.example.takunaka.taskapp.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.takunaka.taskapp.Configurator;
import com.example.takunaka.taskapp.R;
import com.example.takunaka.taskapp.sql.DBHelper;
import com.example.takunaka.taskapp.sqlQuerry.SubTask;
import com.example.takunaka.taskapp.sqlQuerry.TaskContainer;

import java.util.List;


public class UpdateFragment extends Fragment {

    private ShowTaskFragment showTaskFragment;
    private EditText name;
    private TextView date;
    private Spinner state;
    private DBHelper dbHelper;
    private String selectedID;
    private int year_x, month_x, day_x;
    private DatePickerDialog.OnDateSetListener mDateSetListner;
    private Configurator config = Configurator.getInstance();
    private MenuItem saveItem;
    private int currentPosition;

    public UpdateFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    //создание фрагмента
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //присвоение для фрагмента тулбар меню
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_update, container, false);
        name = (EditText) rootView.findViewById(R.id.NameUpdateField);
        date = (TextView) rootView.findViewById(R.id.DateUpdateField);
        state = (Spinner) rootView.findViewById(R.id.StateUpdateField);
        //присвоение полям данных на основании выбранного таска
        name.setText(TaskContainer.getSelectedTask().getDesription());
        date.setText(TaskContainer.getSelectedTask().getDate());
        selectedID = String.valueOf(TaskContainer.getSelectedTask().getTaskID());

        //массив статусов. имеет декоративное значение.
        String[] states = { "Выполняется", "Закрыта"};
        //создание адаптера с присвоением массива статусов
        //создается для того, что бы корректно отображать стиль спиннера.
        ArrayAdapter<String> spinnerItemsAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, states);
        spinnerItemsAdapter.setDropDownViewResource(R.layout.spinner_item);
        state.setAdapter(spinnerItemsAdapter);

        //выбор дефолтного item в спиннере на основании статуса
        if(TaskContainer.getSelectedTask().getState().equals("Выполняется")){
            state.setSelection(0);
            currentPosition = 0;
        }else {
            state.setSelection(1);
            currentPosition = 1;
        }
        //инициализация календаря
        initCal();
        //прослушка клика юзера по дате
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //при нажатии отражение календаря
                DatePickerDialog dialog = new DatePickerDialog(getContext(), R.style.Theme_AppCompat_Dialog,
                        mDateSetListner, year_x, month_x, day_x);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));
                dialog.show();
            }
        });
        //инициализация календаря на нужную дату
        mDateSetListner = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                year_x = year;
                month_x = month;
                day_x = dayOfMonth;
                date.setText(dayOfMonth + "." + (month + 1) + "." + year);
            }
        };
        //включение отображения стрелки "Назад" в тулбаре
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        //прослушка изменений в полях и изменение видимости кнопки сохранить. Если произошло изменение - кнопка доступна.
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
    //настройка отображения меню тулбара
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
            //проверка на пустое поле в названии
            if(name.getText().equals("")){
                Toast.makeText(getContext(), "Название не может быть пустым!", Toast.LENGTH_SHORT).show();
            }else {
                //если поле не пустое
                //открытие базы данных
                dbHelper = new DBHelper(getContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                //создание cv
                ContentValues cv = new ContentValues();
                cv.put(DBHelper.KEY_DESCRIPTION, name.getText().toString());
                cv.put(DBHelper.KEY_DATE, date.getText().toString());
                cv.put(DBHelper.KEY_STATE, state.getSelectedItem().toString());
                //апдейт базы данных
                db.update(dbHelper.TABLE_TASKS, cv, dbHelper.KEY_ID +" = " + selectedID, null);
                //проверка на статус
                if(state.getSelectedItem().toString().equals("Закрыта")){
                    //установка булевой сигнализирущей о том что статус задачи - закрыт
                    config.setClosed(true);
                    //получение всех дел
                    List<SubTask> subTasks = dbHelper.getAllSubTasks(Integer.valueOf(selectedID));
                    //у каждого дела поставить статус "закрыто"
                    for (SubTask s : subTasks){
                        dbHelper.updateState(s.getId(), s.getTaskID(), s.getNameID());
                    }
                }
                //переход на фрагмент main
                MainFragment mainFragment = new MainFragment();
                getFragmentManager().popBackStack();
                getFragmentManager().beginTransaction()
                .replace(R.id.container, mainFragment)
                .commit();
                //вызов метода убирания клавиатуры
                hideKeyboard(getContext());
                //закрытие БД
                db.close();
                dbHelper.close();
            }
        }
        //возврт назад
        if (id == android.R.id.home){
            showTaskFragment = new ShowTaskFragment();
            getFragmentManager().beginTransaction()
            .replace(R.id.container, showTaskFragment)
            .commit();
        }

        return super.onOptionsItemSelected(item);
    }
    //метод скрытия клавиатуры
    public void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;
        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    //инициализация календаря
    public void initCal(){
        String[] _date = date.getText().toString().split("\\.");
        year_x = Integer.valueOf(_date[2]);
        int month = Integer.valueOf(_date[1]);
        month_x = month - 1;
        day_x = Integer.valueOf(_date[0]);
    }

    //сохранение данных при изменении ориентации
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("DateCreate", date.getText().toString());
        super.onSaveInstanceState(outState);
    }
    //возврат данных при изменении ориентации
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        if(savedInstanceState != null){
            date.setText(savedInstanceState.getString("DateCreate"));
            initCal();
        }
        super.onViewStateRestored(savedInstanceState);
    }
}
