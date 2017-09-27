package com.example.takunaka.taskapp.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
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

import com.example.takunaka.taskapp.Cfg;
import com.example.takunaka.taskapp.MainActivity;
import com.example.takunaka.taskapp.R;
import com.example.takunaka.taskapp.Utils;
import com.example.takunaka.taskapp.sql.DBHelper;
import com.example.takunaka.taskapp.sqlQuerry.SubTask;
import com.example.takunaka.taskapp.sqlQuerry.TaskContainer;

import java.util.List;


public class UpdateFragment extends Fragment {

    //Название задачи
    private EditText name;
    //Дата задачи
    private TextView date;
    //Спиннер статусов
    private Spinner state;
    //поля календаря
    private int year_x, month_x, day_x;
    //календарь
    private DatePickerDialog.OnDateSetListener mDateSetListner;

    private Cfg config = Cfg.getInstance();
    //иконка меню save
    private MenuItem saveItem;
    //число для проверки изменений в спиннере и отображения кнопки сохранить.
    private int currentPosition;

    public UpdateFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    /**
     * Создание фрагмента
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //присвоение для фрагмента тулбар меню
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_update, container, false);
        name = (EditText) rootView.findViewById(R.id.NameUpdateField);
        date = (TextView) rootView.findViewById(R.id.DateUpdateField);
        state = (Spinner) rootView.findViewById(R.id.StateUpdateField);
        //присвоение полям данных на основании выбранного таска
        name.setText(TaskContainer.getSelectedTask().getDescription());
        date.setText(Utils.getStringDate(TaskContainer.getSelectedTask().getDate()));

        //массив статусов. имеет декоративное значение.
        String[] states = {getResources().getStringArray(R.array.states)[0],
                getResources().getStringArray(R.array.states)[1]};
        //создание адаптера с присвоением массива статусов
        //создается для того, что бы корректно отображать стиль спиннера.
        ArrayAdapter<String> spinnerItemsAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, states);
        spinnerItemsAdapter.setDropDownViewResource(R.layout.spinner_item);
        state.setAdapter(spinnerItemsAdapter);

        //выбор дефолтного item в спиннере на основании статуса
        if (TaskContainer.getSelectedTask().getState().equals(states[0])) {
            state.setSelection(0);
            currentPosition = 0;
        } else {
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
                if (dialog.getWindow() != null)
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
        ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayShowHomeEnabled(true);
        }
        //прослушка изменений в полях и изменение видимости кнопки сохранить. Если произошло изменение - кнопка доступна.
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TaskContainer.getSelectedTask().getState().equals(getResources().getStringArray(R.array.states)[2])) {
                    saveItem.setVisible(false);
                } else if (TaskContainer.getSelectedTask().getState().equals(getResources().getStringArray(R.array.states)[1])) {
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

    /**
     * настройка отображения меню тулбара
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        saveItem = menu.findItem(R.id.action_save).setVisible(false);
        menu.findItem(R.id.action_edit).setVisible(false);
        menu.findItem(R.id.account_action).setVisible(false);
        menu.findItem(R.id.addTask).setVisible(false);
        menu.findItem(R.id.action_save_create).setVisible(false);
        menu.findItem(R.id.search_action).setVisible(false);

    }

    /**
     * обработка нажатия иконки меню
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_save) {
            saveAction();
        }
        //возврт назад
        if (id == android.R.id.home) {
            ((MainActivity) getActivity()).changeFragment("ShowTask");
        }

        return super.onOptionsItemSelected(item);
    }

    public void saveAction() {
        //проверка на пустое поле в названии
        if (name.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), R.string.empty_task_name, Toast.LENGTH_SHORT).show();
        } else {
            //если поле не пустое
            //открытие базы данных
            DBHelper dbHelper = new DBHelper(getContext());
            dbHelper.updateTask(name.getText().toString(), Utils.getUnixTime(date.getText().toString()), state.getSelectedItem().toString());

            //проверка на статус
            if (state.getSelectedItem().toString().equals(getResources().getStringArray(R.array.states)[1])) {
                //установка булевой сигнализирущей о том что статус задачи - закрыт
                config.setClosed(true);
                //получение всех дел
                List<SubTask> subTasks = dbHelper.getAllSubTasks(TaskContainer.getSelectedTask().getTaskID());
                //у каждого дела поставить статус "закрыто"
                for (SubTask s : subTasks) {
                    dbHelper.updateState(s.getId(), s.getTaskID(), s.getNameID());
                }
            }
            //переход на фрагмент main
            ((MainActivity) getActivity()).changeFragment("Main");
            //вызов метода убирания клавиатуры
            hideKeyboard(getContext());
            //закрытие БД
            dbHelper.close();
        }
    }

    /**
     * метод скрытия клавиатуры при выходе с фрагмента
     */
    private void hideKeyboard(@NonNull Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;
        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /**
     * инициализация календаря
     */
    private void initCal() {
        String[] _date = date.getText().toString().split("\\.");
        year_x = Integer.valueOf(_date[2]);
        int month = Integer.valueOf(_date[1]);
        month_x = month - 1;
        day_x = Integer.valueOf(_date[0]);
    }

    /**
     * сохранение данных при изменении ориентации
     *
     * @param outState bundle с сохраненными данными
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("DateCreate", date.getText().toString());
        super.onSaveInstanceState(outState);
    }

    /**
     * возврат данных при изменении ориентации
     *
     * @param savedInstanceState bundle с восттановленными данными
     */
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            date.setText(savedInstanceState.getString("DateCreate"));
            initCal();
        }
        super.onViewStateRestored(savedInstanceState);
    }


}
