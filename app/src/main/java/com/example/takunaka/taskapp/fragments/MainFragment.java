package com.example.takunaka.taskapp.fragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.takunaka.taskapp.Configurator;
import com.example.takunaka.taskapp.R;
import com.example.takunaka.taskapp.adapters.RecyclerViewAdapter;
import com.example.takunaka.taskapp.sql.DBHelper;
import com.example.takunaka.taskapp.sqlQuerry.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class MainFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private DBHelper dbHelper;
    private RecyclerViewAdapter adapter;
    private RecyclerView rv;
    private Switch mSwitch;
    private View rootView;
    private ArrayList<Task> tasks = new ArrayList<>();
    private Configurator configurator = Configurator.getInstance();
    private ArrayList<Task> filtred;
    private TextView filterClose;
    private CreateTaskFragment createTaskFragment;

    private TextView dateFrom;
    private TextView dateTo;
    private int year_from, month_from, day_from;
    private int year_to, month_to, day_to;
    private DatePickerDialog.OnDateSetListener mDateFromSetListner;
    private DatePickerDialog.OnDateSetListener mDateToSetListner;
    private String dateFromSet;
    private String dateToSet;


    public MainFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        rootView =  inflater.inflate(R.layout.fragment_main, container, false);
        rv = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mSwitch = (Switch) rootView.findViewById(R.id.switcherClosed);
        filterClose = (TextView) rootView.findViewById(R.id.filterClose);
        filterClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configurator.setFilterActive(false);
                initRW();
            }
        });

        mSwitch.setOnCheckedChangeListener(this);
        dbHelper = new DBHelper(rootView.getContext());

        rv.setHasFixedSize(true);
        setHasOptionsMenu(true);

        //проверка фильтра
        initRW();
        //установка свитчера на основании булевой из конфигуратора
        if(configurator.isOnlyOpened()){
            mSwitch.setChecked(false);
        }else mSwitch.setChecked(true);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //если фильтр только отрытых включен
        if(configurator.isOnlyOpened()){
            //если фильтр активен
            if(configurator.isFilterActive()){
                filterClose.setVisibility(rootView.VISIBLE);
                tasks = dbHelper.getOpenedTask();
                //получаем список, фильтруем и ставим метки
                if(tasks.size() != 0){
                    filtred = sortlistFromTo(tasks, configurator.getFilterDateFrom(), configurator.getFilterDateTo());
                    if(filtred.size() != 0){
                        sortlist(filtred);
                        addMarks(filtred);
                    }
                    //инициализируем адаптер
                    adapter = new RecyclerViewAdapter(filtred, rootView.getContext());
                    adapter.notifyDataSetChanged();
                }
            }else {
                //если фильтр не активен
                filterClose.setVisibility(rootView.INVISIBLE);
                tasks = dbHelper.getOpenedTask();
                //получаем список, сортируем и ставим метки
                if(tasks.size() != 0){
                    sortlist(tasks);
                    addMarks(tasks);
                }
                //инициализируем адаптер
                adapter = new RecyclerViewAdapter(tasks, rootView.getContext());
                adapter.notifyDataSetChanged();
            }
            rv.setAdapter(adapter);
            rv.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            rv.setLayoutManager(llm);
        //если фильтр только открытых выключен
        }else {
            //проверяем фильтр по датам
            if(configurator.isFilterActive()) {
                //если включен - получаем список, сортируем, ставим метки и инициализируем адаптер
                filterClose.setVisibility(rootView.VISIBLE);
                tasks = dbHelper.getAllTasks();
                if (tasks.size() != 0) {
                    filtred = sortlistFromTo(tasks, configurator.getFilterDateFrom(), configurator.getFilterDateTo());
                    if(filtred.size() != 0){
                        sortlist(filtred);
                        addMarks(filtred);
                    }
                    adapter = new RecyclerViewAdapter(filtred, rootView.getContext());
                    adapter.notifyDataSetChanged();
                }
            } else {
                //если выключен - получаем список, сортируем, ставим метки и инициализируем адаптер
                filterClose.setVisibility(rootView.INVISIBLE);
                tasks = dbHelper.getAllTasks();
                if(tasks.size() != 0){
                    sortlist(tasks);
                    addMarks(tasks);
                }
                adapter = new RecyclerViewAdapter(tasks, rootView.getContext());
                adapter.notifyDataSetChanged();
                rv.setAdapter(adapter);
                rv.setHasFixedSize(true);
                LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                rv.setLayoutManager(llm);

            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        rv.setHasFixedSize(true);
        //проверка нажатия свитчера
        //если не включен
        if (!isChecked){
            //проверяем фильтр и инициализируем адаптер с сортированным списком
            if(configurator.isFilterActive()){
                filterClose.setVisibility(rootView.VISIBLE);
                tasks = dbHelper.getOpenedTask();
                if(tasks.size() != 0){
                    filtred = sortlistFromTo(tasks, configurator.getFilterDateFrom(), configurator.getFilterDateTo());
                    if(filtred.size() != 0){
                        sortlist(filtred);
                        addMarks(filtred);
                    }
                    adapter = new RecyclerViewAdapter(filtred, rootView.getContext());
                    adapter.notifyDataSetChanged();
                }
            }else {
                //проверяем фильтр и инициализируем адаптер с сортированным списком
                filterClose.setVisibility(rootView.INVISIBLE);
                tasks = dbHelper.getOpenedTask();
                if (tasks.size() != 0) {
                    sortlist(tasks);
                    addMarks(tasks);
                }
                adapter = new RecyclerViewAdapter(tasks, rootView.getContext());
                adapter.notifyDataSetChanged();
                configurator.setOnlyOpened(true);
            }
        }else {
        //если свитчер включен
            filterClose.setVisibility(rootView.VISIBLE);
            if(configurator.isFilterActive()){
                //проверяем фильтр и инициализируем адаптер с сортированным списком
                tasks = dbHelper.getAllTasks();
                if(tasks.size() != 0){
                    filtred = sortlistFromTo(tasks, configurator.getFilterDateFrom(), configurator.getFilterDateTo());
                    if(filtred.size() != 0){
                        sortlist(filtred);
                        addMarks(filtred);
                    }
                    adapter = new RecyclerViewAdapter(filtred, rootView.getContext());
                    adapter.notifyDataSetChanged();
                }
            }else {
                //проверяем фильтр и инициализируем адаптер с сортированным списком
                filterClose.setVisibility(rootView.INVISIBLE);
                tasks = dbHelper.getAllTasks();
                if (tasks.size() != 0) {
                    sortlist(tasks);
                    addMarks(tasks);
                }
                adapter = new RecyclerViewAdapter(tasks, rootView.getContext());
                adapter.notifyDataSetChanged();
                configurator.setOnlyOpened(false);
            }
        }
        rv.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
    }

    //метод сортировки дат, такой же как и в классе DBhelper
    public ArrayList<Task> sortlist(ArrayList<Task> sortdedTasks){
        Collections.sort(sortdedTasks, new Comparator<Task>() {
            @Override
            public int compare(Task t1, Task t2) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
                Date date1 = null;
                Date date2 = null;
                try {
                    date1 = formatter.parse(t1.getDate());
                    date2 = formatter.parse(t2.getDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return date1.compareTo(date2);
            }
        });
        return sortdedTasks;
    }

    //метод сортировки дат по фильтру
    public ArrayList<Task> sortlistFromTo(ArrayList<Task> sortdedTasks, String dateFrom, String dateTo){
                ArrayList<Task> sortedList = new ArrayList<>();
                //разбивка дат на массивы
                String[] from = configurator.getFilterDateFrom().split("\\.");
                String[] to = configurator.getFilterDateTo().split("\\.");
                for (Task task : sortdedTasks) {
                    //для каждого элемента
                    String[] taskDate = task.getDate().split("\\.");
                    //проверка
                    //если года from < searchDate < to   то просто добавляем. наш год точно находится в ренже поиска.
                    if((Integer.valueOf(from[2]) < Integer.valueOf(taskDate[2]))
                            && (Integer.valueOf(to[2]) > Integer.valueOf(taskDate[2]))) {
                        sortedList.add(task);
                        //если года from == searchDate или searchDate == to   то переходим к месяцам
                    }else if((Integer.valueOf(from[2]).equals(Integer.valueOf(taskDate[2])))
                            || (Integer.valueOf(to[2]).equals(Integer.valueOf(taskDate[2])))){
                        //если месяца from < searchDate < to   то просто добавляем. наш месяц точно находится в ренже поиска.
                            if((Integer.valueOf(from[1]) < Integer.valueOf(taskDate[1]))
                                    && (Integer.valueOf(to[1]) > Integer.valueOf(taskDate[1]))){
                                sortedList.add(task);
                                //если месяца from == searchDate или searchDate == to   то переходим к дням
                            }else if((Integer.valueOf(from[1]).equals(Integer.valueOf(taskDate[1])))
                                    || (Integer.valueOf(to[1]).equals(Integer.valueOf(taskDate[1])))){
                                //если дни from <= searchDate <= to   то добавляем. искомая комбинация в рендже.
                                if((Integer.valueOf(from[0]) <= Integer.valueOf(taskDate[0]))
                                        && (Integer.valueOf(to[0]) >= Integer.valueOf(taskDate[0]))){
                                    sortedList.add(task);
                            }
                        }
                    }
                }return sortedList;
    }

        //добавление марок для отображения списка
    public ArrayList<Task> addMarks(ArrayList<Task> sortedTask){
        ArrayList<Task> sortedWithMarks = sortedTask;
        String date = sortedWithMarks.get(0).getDate();
        //для первой даты сортированного списка установка типа "1"
        sortedWithMarks.get(0).setType(1);
        //для каждого последующего элемента списка
        for (int i = 1; i < sortedWithMarks.size(); i++){
            //если дата повторяется - ставим тип 2
            if(sortedWithMarks.get(i).getDate().equals(date)){
                sortedWithMarks.get(i).setType(2);
            }else {
                //если дата новая - ставим тип 1 и присваиваем переменной date новую дату
                sortedWithMarks.get(i).setType(1);
                date = sortedTask.get(i).getDate();
            }
        }
        return sortedWithMarks;
    }
    //настройка видимости элементов тулбара
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menu.findItem(R.id.action_edit).setVisible(false);
        menu.findItem(R.id.account_action).setVisible(true);
        menu.findItem(R.id.addTask).setVisible(true);
        menu.findItem(R.id.action_save).setVisible(false);
        menu.findItem(R.id.action_save_create).setVisible(false);
        menu.findItem(R.id.search_action).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //обработка нажатий на элементы тулбара
        int id = item.getItemId();

        if (id == R.id.search_action) {
            showSearchDialog();
        }
        if (id == R.id.addTask){
            createTaskFragment = new CreateTaskFragment();

            getFragmentManager().beginTransaction()
                    .add(createTaskFragment, "Main")
                    .replace(R.id.container, createTaskFragment)
                    .addToBackStack(null)
                    .commit();
        }

        return super.onOptionsItemSelected(item);
    }

    //диалог выбора дат для фильтра
    public void showSearchDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.Theme_AppCompat_Dialog));
        View view = View.inflate(getContext(), R.layout.dialog_search, null);
        dateFrom = (TextView) view.findViewById(R.id.dateFrom);
        dateTo = (TextView) view.findViewById(R.id.dateTo);
        //если фильтр активен - ставим выбранные даты в поля
        if(configurator.isFilterActive()){
            dateFrom.setText(configurator.getFilterDateFrom());
            dateTo.setText(configurator.getFilterDateTo());
            initCalIfFilter();
        }//если нет - инициализируем календарь
        else initCal();
        builder.setTitle("Выберите период")
                .setView(view)
                .setPositiveButton("Применить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //если пользователь не выбрал даты вывод сообщения
                        if(dateFrom.getText().equals("") || dateTo.getText().equals("")){
                            Toast.makeText(getContext(), "Нужно ввести даты поиска", Toast.LENGTH_SHORT).show();
                            showSearchDialog();
                        }else {
                            //если даты выбраны - установка фильтра
                        dateFromSet = dateFrom.getText().toString();
                        dateToSet = dateTo.getText().toString();
                        configurator.setFilterDateFrom(dateFromSet);
                        configurator.setFilterDateTo(dateToSet);
                        configurator.setFilterActive(true);
                            //и повторная инициализация RecyclerView
                        initRW();
                        }
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();


        //инициализация календаря для фильтра дат
        mDateFromSetListner = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                year_from = year;
                month_from = month;
                day_from = dayOfMonth;
                dateFromSet = dayOfMonth + "." + month + "." + year;
                dateFrom.setText(dateFromSet);
            }
        };

        dateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(getContext(), R.style.Theme_AppCompat_Dialog,
                        mDateFromSetListner, year_from, month_from, day_from);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));
                dialog.show();
            }
        });



        dateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(getContext(), R.style.Theme_AppCompat_Dialog,
                        mDateToSetListner, year_to, month_to, day_to);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));
                dialog.show();
            }
        });

        mDateToSetListner = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                year_to = year;
                month_to = month;
                day_to = dayOfMonth;
                dateToSet = dayOfMonth + "." + month + "." + year;
                dateTo.setText(dateToSet);
            }
        };


        alert.setCanceledOnTouchOutside(false);
        alert.show();


    }
    //инициализация пустого календаря
    public void initCal(){
        final Calendar cal = Calendar.getInstance();
        year_from = cal.get(Calendar.YEAR);
        month_from = cal.get(Calendar.MONTH);
        day_from = cal.get(Calendar.DAY_OF_MONTH);
        year_to = cal.get(Calendar.YEAR);
        month_to = cal.get(Calendar.MONTH);
        day_to = cal.get(Calendar.DAY_OF_MONTH);
    }
    //инициализация дат для календаря, если даты были выбраны ранее
    public void initCalIfFilter(){
        String[] datefrom = configurator.getFilterDateFrom().split("\\.");
        String[] dateto = configurator.getFilterDateTo().split("\\.");

        year_from = Integer.valueOf(datefrom[2]);
        int month = Integer.valueOf(datefrom[1]);
        month_from = month - 1;
        day_from = Integer.valueOf(datefrom[0]);

        year_to = Integer.valueOf(dateto[2]);
        int monthto = Integer.valueOf(dateto[1]);
        month_to = monthto - 1;
        day_to = Integer.valueOf(dateto[0]);
    }
    //инициализация списка
    public void initRW(){
        //проверка на только открытые задачи
        if(configurator.isOnlyOpened()){
            //проверка на фильтр
            if(configurator.isFilterActive()){
                filterClose.setVisibility(rootView.VISIBLE);
                tasks = dbHelper.getOpenedTask();
                //если фильтр активен
                if(tasks.size() != 0){
                    filtred = sortlistFromTo(tasks, dateFromSet, dateToSet);
                    if(filtred.size() != 0){
                        sortlist(filtred);
                        addMarks(filtred);
                    }
                    //инициаизируем адаптер списоком с сортировкой и метками
                    //отправляем фильтрованные таски во временное хранилище в конфигураторе
                    configurator.setTasks(filtred);
                    adapter = new RecyclerViewAdapter(filtred, rootView.getContext());
                    adapter.notifyDataSetChanged();
                }
            }else {
                //если фильтр не активен
                filterClose.setVisibility(rootView.INVISIBLE);
                tasks = dbHelper.getOpenedTask();
                if(tasks.size() != 0){
                    sortlist(tasks);
                    addMarks(tasks);
                }
                //инициаизируем адаптер списоком с сортировкой и метками
                adapter = new RecyclerViewAdapter(tasks, rootView.getContext());
                adapter.notifyDataSetChanged();
            }
            rv.setAdapter(adapter);
            rv.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            rv.setLayoutManager(llm);
        }else {
        //если фильтр закрытых не истина
            //проверяем фильтр дат
            if(configurator.isFilterActive()) {
                filterClose.setVisibility(rootView.VISIBLE);
                tasks = dbHelper.getAllTasks();
                if (tasks.size() != 0) {
                    filtred = sortlistFromTo(tasks, dateFromSet, dateToSet);
                    if(filtred.size() != 0){
                        sortlist(filtred);
                        addMarks(filtred);
                    }
                    //инициаизируем адаптер списоком с сортировкой и метками
                    //отправляем фильтрованные таски во временное хранилище в конфигураторе
                    configurator.setTasks(filtred);
                    adapter = new RecyclerViewAdapter(filtred, rootView.getContext());
                    adapter.notifyDataSetChanged();
                }
            } else {
                //если фильтр дат не включен
                filterClose.setVisibility(rootView.INVISIBLE);
                tasks = dbHelper.getAllTasks();
                if(tasks.size() != 0){
                    sortlist(tasks);
                    addMarks(tasks);
                }
                //инициаизируем адаптер списоком с сортировкой и метками
                adapter = new RecyclerViewAdapter(tasks, rootView.getContext());
                adapter.notifyDataSetChanged();
                rv.setAdapter(adapter);
                rv.setHasFixedSize(true);
                LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                rv.setLayoutManager(llm);

            }
        }
    }

}
