package com.example.takunaka.taskapp.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.takunaka.taskapp.MainActivity;
import com.example.takunaka.taskapp.R;
import com.example.takunaka.taskapp.Utils;
import com.example.takunaka.taskapp.adapters.RecyclerViewSubItemOnCreateAdapter;
import com.example.takunaka.taskapp.sql.DBHelper;
import com.example.takunaka.taskapp.sqlQuerry.SubTask;
import com.example.takunaka.taskapp.sqlQuerry.UserContainer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class CreateTaskFragment extends Fragment implements View.OnClickListener {

    //База данных
    private DBHelper dbHelper;
    //имя
    private EditText name;
    //дата
    private TextView date;
    //дата, выбранная в календаре
    private String dateCalSet;
    private View rootView;
    //календарь
    private int year_x, month_x, day_x;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    //recyclerView
    private RecyclerView rv;
    private RecyclerViewSubItemOnCreateAdapter adapter;
    //временный список дел
    private List<SubTask> subTasks;


    //SharedPref
    private final String SP_name = "Name";
    private final String SP_date = "Date";
    private final String SP_subSize = "STSize";
    private final String SP_description = "Description ";

    public CreateTaskFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_create_task, container, false);
        dbHelper = new DBHelper(getContext());
        name = (EditText) rootView.findViewById(R.id.NameCreateField);
        //уставнока фокуса на элементе
        name.requestFocus();
        date = (TextView) rootView.findViewById(R.id.DateCreateField);
        Button addSubTsk = (Button) rootView.findViewById(R.id.addSubShowTaskButtonOnCreate);
        rv = (RecyclerView) rootView.findViewById(R.id.recyclerViewOnCreate);

        //создание временного списка сабтасков
        subTasks = new ArrayList<>();
        //прослушка клика на кнопку "+"
        addSubTsk.setOnClickListener(this);
        //инициализация календаря
        initCal();
        //установка даты на сегодняшнее число
        date.setText(day_x + "." + (month_x + 1) + "." + year_x);
        //прослушка нажатия на дату и вызов календаря при нажатии
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog dialog = new DatePickerDialog(getContext(), R.style.Theme_AppCompat_Dialog,
                        mDateSetListener, year_x, month_x, day_x);
                if (dialog.getWindow() != null)
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                year_x = year;
                month_x = month;
                day_x = dayOfMonth;
                dateCalSet = dayOfMonth + "." + (month + 1) + "." + year;
                date.setText(dateCalSet);
            }
        };

        //инициализация адаптера сабтасков
        adapter = new RecyclerViewSubItemOnCreateAdapter(subTasks);
        rv.setHasFixedSize(true);
        rv.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(rootView.getContext());
        rv.setLayoutManager(llm);

        setHasOptionsMenu(true);
        //тулбар
        ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayShowHomeEnabled(true);
        }

        return rootView;
    }


    //настройка видимости элементов меню
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menu.findItem(R.id.action_edit).setVisible(false);
        menu.findItem(R.id.account_action).setVisible(false);
        menu.findItem(R.id.addTask).setVisible(false);
        menu.findItem(R.id.action_save).setVisible(false);
        menu.findItem(R.id.action_save_create).setVisible(true);
        menu.findItem(R.id.search_action).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        //настройка поведения при нажатии на кнопки меню
        if (id == R.id.action_save_create) {
            String nameText = String.valueOf(name.getText());
            String dateText = String.valueOf(date.getText());

            //проверка на пустое поле и отсутствие хотя бы одного дела
            if (nameText.isEmpty() || subTasks.isEmpty()) {
                //вывод предупреждения о пустых местах
                Toast.makeText(getContext(), R.string.empty_name_or_subtask, Toast.LENGTH_SHORT).show();
            } else {
                //
                dbHelper.createTask(nameText, Utils.getUnixTime(dateText), getResources().getStringArray(R.array.states)[0]);
                //
                dbHelper.createSubTask(subTasks);

                //вывод сообщения и переход обратно на главную страницу
                Toast.makeText(getContext(), R.string.task_created, Toast.LENGTH_SHORT).show();
                //переход на main фрагмент
                ((MainActivity) getActivity()).changeFragment("Main");
                //скрытие клавиатуры
                hideKeyboard(getContext());
                //закрытие бд
                dbHelper.close();

            }

        }
        if (id == android.R.id.home) {
            //переход на main фрагмент
            ((MainActivity) getActivity()).changeFragment("Main");
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * метод скрытия клавиатуры при переходе в другой фрагмент
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
        final Calendar cal = Calendar.getInstance();
        year_x = cal.get(Calendar.YEAR);
        month_x = cal.get(Calendar.MONTH);
        day_x = cal.get(Calendar.DAY_OF_MONTH);
    }


    @Override
    public void onClick(View v) {
        //отображение диалога и добавление дел в временный список по нажатию на "+"
        AlertDialog.Builder builder = new AlertDialog.Builder(new android.view.ContextThemeWrapper(getContext(), R.style.Theme_AppCompat_Dialog));
        final View dialogView = View.inflate(getContext(), R.layout.dialog_add_description, null);
        builder.setTitle(R.string.add_sub_task)
                .setView(dialogView)
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText description = (EditText) dialogView.findViewById(R.id.descriptionDialog);
                        subTasks.add(new SubTask(UserContainer.getSelectedID(), description.getText().toString(), "В работе"));
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton(R.string.abort, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    /**
     * сохранение данных при ихменении ротации
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(SP_name, name.getText().toString());
        outState.putString(SP_date, date.getText().toString());
        outState.putInt(SP_subSize, subTasks.size());
        for (int i = 0; i < subTasks.size(); i++) {
            outState.putString(SP_description + i, subTasks.get(i).getDescription());
        }
        super.onSaveInstanceState(outState);

    }

    /**
     * восстановление данных при изменении ротации
     */
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            name.setText(savedInstanceState.getString(SP_name));
            date.setText(savedInstanceState.getString(SP_date));
            for (int i = 0; i < savedInstanceState.getInt(SP_subSize); i++) {
                subTasks.add(new SubTask(UserContainer.getSelectedID(),
                        savedInstanceState.getString(SP_description + i),
                        getResources().getStringArray(R.array.states)[2]));
            }
            initSubTasksRV();
        }
        super.onViewStateRestored(savedInstanceState);
    }

    /**
     * инициализация recyclerView с списком сабтасков
     */
    private void initSubTasksRV() {
        adapter = new RecyclerViewSubItemOnCreateAdapter(subTasks);
        rv.setHasFixedSize(true);
        rv.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(rootView.getContext());
        rv.setLayoutManager(llm);
    }


}
