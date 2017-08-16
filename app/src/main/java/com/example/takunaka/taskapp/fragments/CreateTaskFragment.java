package com.example.takunaka.taskapp.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.example.takunaka.taskapp.R;
import com.example.takunaka.taskapp.adapters.RecyclerViewSubItemOnCreateAdapter;
import com.example.takunaka.taskapp.sql.DBHelper;
import com.example.takunaka.taskapp.sqlQuerry.SubTask;
import com.example.takunaka.taskapp.sqlQuerry.UserContainer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class CreateTaskFragment extends Fragment implements View.OnClickListener {

    private DBHelper dbHelper;
    private SQLiteDatabase dbHelperSQL;
    private EditText name;
    private TextView date;
    private String nameText;
    private String dateText;
    private String dateCalSet;
    private MainFragment mainFragment;
    private CreateTaskFragment createTaskFragment;
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
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_create_task, container, false);
        createTaskFragment = new CreateTaskFragment();
        dbHelper = new DBHelper(getContext());
        name = (EditText) rootView.findViewById(R.id.NameCreateField);
        //уставнока фокуса на элементе
        name.requestFocus();
        date = (TextView) rootView.findViewById(R.id.DateCreateField);
        addSubTsk = (Button) rootView.findViewById(R.id.addSubShowTaskButtonOnCreate);
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
                dateCalSet = dayOfMonth + "." + (month + 1) + "." + year;
                date.setText(dateCalSet);
            }
        };

        //инициализация адаптера сабтасков
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



    //настройка видимости элементов меню
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
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();
        //настройка поведения при нажатии на кнопки меню
        if (id == R.id.action_save_create) {
            nameText = String.valueOf(name.getText());
            dateText = String.valueOf(date.getText());
            //проверка на пустое поле и отсутствие хотя бы одного дела
            if(nameText.equals("") || subTasks.size() == 0){
                //вывод предупреждения о пустых местах
                Toast.makeText(getContext(), "Нужно ввести название задачи и создать хотя бы одно дело", Toast.LENGTH_SHORT).show();
            }else {
                //открытие базы данных
                dbHelperSQL = dbHelper.getWritableDatabase();
                final ContentValues cv = new ContentValues();
                cv.put(DBHelper.KEY_DESCRIPTION, nameText);
                cv.put(DBHelper.KEY_DATE, dateText);
                cv.put(DBHelper.KEY_STATE, "Выполняется");
                cv.put(DBHelper.KEY_NAMEID, UserContainer.getSelectedID());
                //вставка новых данных в таблицу тасков
                dbHelperSQL.insert(DBHelper.TABLE_TASKS, null, cv);
                int lastID = dbHelper.getLastTaskID();
                //получение айдишника только что созданной строки в таблице тасков
                for (SubTask s: subTasks){
                    //открытие базы данных
                    dbHelperSQL = dbHelper.getWritableDatabase();
                    //добавление в таблицу сабтасков из временного листа
                    final ContentValues cvSub = new ContentValues();
                    cvSub.put(DBHelper.KEY_DESCRIPTION, s.getDescription());
                    cvSub.put(DBHelper.KEY_STATE, s.getState());
                    cvSub.put(DBHelper.KEY_NAMEID, s.getNameID());
                    cvSub.put(DBHelper.KEY_TASKID, lastID);
                    dbHelperSQL.insert(dbHelper.TABLE_SUBTASK, null, cvSub);
                }
                dbHelperSQL.close();
                dbHelper.close();

                //вывод сообщения и переход обратно на главную страницу
                Toast.makeText(getContext(), "Задача создана", Toast.LENGTH_SHORT).show();
                mainFragment = new MainFragment();
                getFragmentManager().popBackStack();
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, mainFragment)
                        .commit();
                hideKeyboard(getContext());
                dbHelperSQL.close();
                dbHelper.close();

            }

        }
        if (id == android.R.id.home){
            mainFragment = new MainFragment();
            hideKeyboard(getContext());
            getFragmentManager().popBackStack();
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, mainFragment)
                    .commit();
        }

        return super.onOptionsItemSelected(item);
    }

    //метод скрытия клавиатуры при переходе в другой фрагмент
    public void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;
        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    //инициализация календаря
    public void initCal(){
        final Calendar cal = Calendar.getInstance();
        year_x = cal.get(Calendar.YEAR);
        month_x = cal.get(Calendar.MONTH);
        day_x = cal.get(Calendar.DAY_OF_MONTH);
    }

    //отображение диалога и добавление дел в временный список по нажатию на "+"
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

    //сохранение данных при ихменении ротации
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("Name", name.getText().toString());
        outState.putString("Date", date.getText().toString());
        outState.putInt("STSize", subTasks.size());
        for (int i = 0; i < subTasks.size(); i++){
            outState.putString("Description " + i, subTasks.get(i).getDescription() );
        }
        super.onSaveInstanceState(outState);

    }

    //восстановление данных при изменении ротации
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            name.setText(savedInstanceState.getString("Name"));
            date.setText(savedInstanceState.getString("Date"));
            for (int i = 0; i < savedInstanceState.getInt("STSize"); i++){
                subTasks.add(new SubTask(UserContainer.getSelectedID(), savedInstanceState.getString("Description " + i), "В работе"));
            }
            initSubTasksRV();
        }
        super.onViewStateRestored(savedInstanceState);
    }

    //инициализация списка сабтасков
    public void initSubTasksRV(){
        adapter = new RecyclerViewSubItemOnCreateAdapter(subTasks, rootView.getContext());
        rv.setHasFixedSize(true);
        rv.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(rootView.getContext());
        rv.setLayoutManager(llm);
    }
}
