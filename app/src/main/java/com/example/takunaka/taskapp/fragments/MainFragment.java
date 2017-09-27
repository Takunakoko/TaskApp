package com.example.takunaka.taskapp.fragments;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
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

import com.example.takunaka.taskapp.Cfg;
import com.example.takunaka.taskapp.MainActivity;
import com.example.takunaka.taskapp.R;
import com.example.takunaka.taskapp.Utils;
import com.example.takunaka.taskapp.adapters.RecyclerViewAdapter;
import com.example.takunaka.taskapp.sql.DBHelper;
import com.example.takunaka.taskapp.sqlQuerry.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class MainFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    //база данных
    private DBHelper dbHelper;
    //главный ресайклер
    private RecyclerViewAdapter adapter;
    private RecyclerView rv;
    private View rootView;
    //список задач всех и фильтрованных
    @NonNull
    private ArrayList<Task> tasks = new ArrayList<>();

    private Cfg cfg = Cfg.getInstance();
    //текствью закрытия фильтра
    private TextView filterClose;

    //календарь
    private TextView dateFrom;
    private TextView dateTo;
    private int year_from, month_from, day_from;
    private int year_to, month_to, day_to;
    private DatePickerDialog.OnDateSetListener mDateFromSetListner;
    private DatePickerDialog.OnDateSetListener mDateToSetListner;
    //даты в фильтре
    private String dateFromSet;
    private String dateToSet;


    public MainFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        rv = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        Switch mSwitch = (Switch) rootView.findViewById(R.id.switcherClosed);
        filterClose = (TextView) rootView.findViewById(R.id.filterClose);
        filterClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cfg.setFilterActive(false);
                initRV();
            }
        });
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        mSwitch.setOnCheckedChangeListener(this);
        dbHelper = new DBHelper(rootView.getContext());

        rv.setHasFixedSize(true);
        setHasOptionsMenu(true);

        //проверка фильтра
        initRV();
        //установка свитчера на основании булевой из конфигуратора
        if (cfg.isOnlyOpened()) {
            mSwitch.setChecked(false);
        } else mSwitch.setChecked(true);

        ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(false);
            bar.setDisplayShowHomeEnabled(false);
        }


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //если фильтр только отрытых включен
        if (cfg.isOnlyOpened()) {
            //если фильтр активен
            checkFilterOnlyOpenedTask();
            //если фильтр только открытых выключен
        } else {
            //проверяем фильтр по датам
            checkFilterAllTask();
        }
    }

    /**
     * проверка фильтра со списком только открытых
     */
    public void checkFilterOnlyOpenedTask() {
        //проверяем активен ли фильтр
        if (cfg.isFilterActive()) {
            //если фильтр активен ставим возможность снятия фильтра
            filterClose.setVisibility(View.VISIBLE);
            //получаем список откртых задач
            tasks = dbHelper.getTasks("openedFilter", (int) cfg.getFilterDateFrom(), (int) cfg.getFilterDateTo());
            //отправляем фильтрованные таски во временное хранилище в конфигураторе
            cfg.setTasks(tasks);
            //инициализируем адаптер

        } else {
            //если фильтр не активен
            filterClose.setVisibility(View.INVISIBLE);
            tasks = dbHelper.getTasks("openedSort", 0, 0);
        }
        adapter = new RecyclerViewAdapter(tasks, rootView.getContext());
        adapter.notifyDataSetChanged();
        //инициаизируем адаптер списоком с сортировкой и метками
        rv.setAdapter(adapter);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
    }

    /**
     * проверка фильтра со списком всех задач
     */
    public void checkFilterAllTask() {
        //
        if (cfg.isFilterActive()) {
            //если включен - получаем список всех задач, сортируем, ставим метки и инициализируем адаптер
            filterClose.setVisibility(View.VISIBLE);
            tasks = dbHelper.getTasks("allFilter", (int) cfg.getFilterDateFrom(), (int) cfg.getFilterDateTo());
            //отправляем фильтрованные таски во временное хранилище в конфигураторе
            cfg.setTasks(tasks);

        } else {
            //если выключен - получаем список, сортируем, ставим метки и инициализируем адаптер
            filterClose.setVisibility(View.INVISIBLE);
            tasks = dbHelper.getTasks("allSort", 0, 0);
        }
        adapter = new RecyclerViewAdapter(tasks, rootView.getContext());
        adapter.notifyDataSetChanged();
        //инициаизируем адаптер списоком с сортировкой и метками
        rv.setAdapter(adapter);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //проверка изменения свитчера
        if (!isChecked) { //если не включен
            //устанавливает текущее состояние ползунка для правильной инициализации списка
            cfg.setOnlyOpened(true);
            //проверяем фильтр и инициализируем адаптер с сортированным списком открытых задач
            checkFilterOnlyOpenedTask();
        } else { //если свитчер включен
            //устанавливает текущее состояние ползунка для правильной инициализации списка
            cfg.setOnlyOpened(false);
            //проверяем фильтр и инициализируем адаптер с сортированным списком всех задач
            checkFilterAllTask();
        }
    }

    //настройка видимости элементов тулбара
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menu.findItem(R.id.action_edit).setVisible(false);
        menu.findItem(R.id.account_action).setVisible(true);
        menu.findItem(R.id.addTask).setVisible(true);
        menu.findItem(R.id.action_save).setVisible(false);
        menu.findItem(R.id.action_save_create).setVisible(false);
        menu.findItem(R.id.search_action).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //обработка нажатий на элементы тулбара
        int id = item.getItemId();
        if (id == R.id.search_action) {
            showSearchDialog();
        }
        if (id == R.id.addTask) {
            ((MainActivity) getActivity()).changeFragment("Create");
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * диалог выбора дат для фильтра
     */
    public void showSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.Theme_AppCompat_Dialog));
        View view = View.inflate(getContext(), R.layout.dialog_search, null);
        dateFrom = (TextView) view.findViewById(R.id.dateFrom);
        dateTo = (TextView) view.findViewById(R.id.dateTo);
        //если фильтр активен
        if (cfg.isFilterActive()) {
            //ставим выбранные даты в поля
            dateFrom.setText(Utils.getStringDate((int) cfg.getFilterDateFrom()));
            dateTo.setText(Utils.getStringDate((int) cfg.getFilterDateTo()));
            initCalIfFilter();
        }//если нет - инициализируем календарь
        else initCal();
        builder.setTitle(R.string.filter_period)
                .setView(view)
                .setPositiveButton(R.string.select, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //если пользователь не выбрал даты вывод сообщения
                        if (dateFrom.getText().toString().isEmpty() || dateTo.getText().toString().isEmpty()) {
                            Toast.makeText(getContext(), R.string.empty_filter, Toast.LENGTH_SHORT).show();
                            showSearchDialog();
                        } else {
                            //если даты выбраны - установка фильтра
                            dateFromSet = dateFrom.getText().toString();
                            dateToSet = dateTo.getText().toString();
                            cfg.setFilterDateFrom(Utils.getUnixTime(dateFrom.getText().toString()));
                            cfg.setFilterDateTo(Utils.getUnixTime(dateTo.getText().toString()));
                            cfg.setFilterActive(true);
                            //и повторная инициализация RecyclerView
                            initRV();
                        }
                    }
                })
                .setNegativeButton(R.string.abort, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
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
                if (dialog.getWindow() != null)
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));
                dialog.show();
            }
        });
        dateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(getContext(), R.style.Theme_AppCompat_Dialog,
                        mDateToSetListner, year_to, month_to, day_to);
                if (dialog.getWindow() != null)
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

    /**
     * инициализация пустого календаря
     */
    public void initCal() {
        Calendar cal = Calendar.getInstance();
        year_from = cal.get(Calendar.YEAR);
        month_from = cal.get(Calendar.MONTH);
        day_from = cal.get(Calendar.DAY_OF_MONTH);
        year_to = cal.get(Calendar.YEAR);
        month_to = cal.get(Calendar.MONTH);
        day_to = cal.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * инициализация дат для календаря, если даты были выбраны ранее
     */
    public void initCalIfFilter() {
        String[] dateFrom = Utils.getStringDate((int) cfg.getFilterDateFrom()).split("\\.");
        String[] dateTo = Utils.getStringDate((int) cfg.getFilterDateTo()).split("\\.");

        year_from = Integer.valueOf(dateFrom[2]);
        int month = Integer.valueOf(dateFrom[1]);
        month_from = month - 1;
        day_from = Integer.valueOf(dateFrom[0]);

        year_to = Integer.valueOf(dateTo[2]);
        int monthTo = Integer.valueOf(dateTo[1]);
        month_to = monthTo - 1;
        day_to = Integer.valueOf(dateTo[0]);
    }

    /**
     * инициализация списка
     */
    public void initRV() {
        //проверка на только открытые задачи
        if (cfg.isOnlyOpened()) {
            //проверка на фильтр дат
            checkFilterOnlyOpenedTask();
        } else {
            //если фильтр закрытых не истина
            //проверяем фильтр дат
            checkFilterAllTask();
        }
    }

}
