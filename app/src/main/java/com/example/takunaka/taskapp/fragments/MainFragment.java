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
import com.example.takunaka.taskapp.MainActivity;
import com.example.takunaka.taskapp.R;
import com.example.takunaka.taskapp.adapters.RecyclerViewAdapter;
import com.example.takunaka.taskapp.sql.DBTasksHelper;
import com.example.takunaka.taskapp.sqlQuerry.Task;
import com.example.takunaka.taskapp.sqlQuerry.TaskContainer;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.zip.Inflater;

public class MainFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private DBTasksHelper dbTasksHelper;
    private RecyclerViewAdapter adapter;
    private RecyclerView rv;
    private Switch mSwitch;
    private View rootView;
    private ArrayList<Task> tasks = new ArrayList<>();
    private Configurator configurator = Configurator.getInstance();
    private ArrayList<Task> filtred;
    private TextView filterClose;


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



        dbTasksHelper = new DBTasksHelper(rootView.getContext());


        rv.setHasFixedSize(true);
        setHasOptionsMenu(true);


        if(configurator.isFilterActive()){
            tasks = dbTasksHelper.getAllTasks();
            filterClose.setVisibility(rootView.VISIBLE);
            if(tasks.size() != 0){
                filtred = sortlistFromTo(tasks, dateFromSet, dateToSet);
                sortlist(filtred);
                addMarks(filtred);
                adapter = new RecyclerViewAdapter(filtred, rootView.getContext());
                adapter.notifyDataSetChanged();
                rv.setAdapter(adapter);
                LinearLayoutManager llaym = new LinearLayoutManager(getActivity());
                rv.setLayoutManager(llaym);
            }
        } else {
            filterClose.setVisibility(rootView.INVISIBLE);
            tasks = dbTasksHelper.getOpenedTask();
            if(tasks.size() != 0){
                sortlist(tasks);
                addMarks(tasks);
            }
            adapter = new RecyclerViewAdapter(tasks, rootView.getContext());
            adapter.notifyDataSetChanged();
            rv.setAdapter(adapter);
            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            rv.setLayoutManager(llm);

        }

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
        if(configurator.isOnlyOpened()){
            if(configurator.isFilterActive()){
                filterClose.setVisibility(rootView.VISIBLE);
                tasks = dbTasksHelper.getOpenedTask();
                if(tasks.size() != 0){
                    filtred = sortlistFromTo(tasks, configurator.getFilterDateFrom(), configurator.getFilterDateTo());
                    sortlist(filtred);
                    addMarks(filtred);
                    adapter = new RecyclerViewAdapter(filtred, rootView.getContext());
                    adapter.notifyDataSetChanged();
                }
            }else {
                filterClose.setVisibility(rootView.INVISIBLE);
                tasks = dbTasksHelper.getOpenedTask();
                if(tasks.size() != 0){
                    sortlist(tasks);
                    addMarks(tasks);
                }
                adapter = new RecyclerViewAdapter(tasks, rootView.getContext());
                adapter.notifyDataSetChanged();
            }
            rv.setAdapter(adapter);
            rv.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            rv.setLayoutManager(llm);
        }else {

            if(configurator.isFilterActive()) {
                filterClose.setVisibility(rootView.VISIBLE);
                tasks = dbTasksHelper.getAllTasks();
                if (tasks.size() != 0) {
                    filtred = sortlistFromTo(tasks, configurator.getFilterDateFrom(), configurator.getFilterDateTo());
                    sortlist(filtred);
                    addMarks(filtred);
                    adapter = new RecyclerViewAdapter(filtred, rootView.getContext());
                    adapter.notifyDataSetChanged();
                }
            } else {
                filterClose.setVisibility(rootView.INVISIBLE);
                tasks = dbTasksHelper.getAllTasks();
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
        if (!isChecked){

            if(configurator.isFilterActive()){
                filterClose.setVisibility(rootView.VISIBLE);
                tasks = dbTasksHelper.getOpenedTask();
                if(tasks.size() != 0){
                    filtred = sortlistFromTo(tasks, configurator.getFilterDateFrom(), configurator.getFilterDateTo());
                    sortlist(filtred);
                    addMarks(filtred);
                    adapter = new RecyclerViewAdapter(filtred, rootView.getContext());
                    adapter.notifyDataSetChanged();
                }
            }else {
                filterClose.setVisibility(rootView.INVISIBLE);
                tasks = dbTasksHelper.getOpenedTask();
                if (tasks.size() != 0) {
                    sortlist(tasks);
                    addMarks(tasks);
                }
                adapter = new RecyclerViewAdapter(tasks, rootView.getContext());
                adapter.notifyDataSetChanged();
                configurator.setOnlyOpened(true);
            }
        }else {
            filterClose.setVisibility(rootView.VISIBLE);
            if(configurator.isFilterActive()){
                tasks = dbTasksHelper.getAllTasks();
                if(tasks.size() != 0){
                    filtred = sortlistFromTo(tasks, configurator.getFilterDateFrom(), configurator.getFilterDateTo());
                    sortlist(filtred);
                    addMarks(filtred);
                    adapter = new RecyclerViewAdapter(filtred, rootView.getContext());
                    adapter.notifyDataSetChanged();
                }
            }else {
                filterClose.setVisibility(rootView.INVISIBLE);
                tasks = dbTasksHelper.getAllTasks();
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


    public ArrayList<Task> sortlistFromTo(ArrayList<Task> sortdedTasks, String dateFrom, String dateTo){
                ArrayList<Task> sortedList = new ArrayList<>();
                String[] from = configurator.getFilterDateFrom().split("\\.");
                String[] to = configurator.getFilterDateTo().split("\\.");
                for (Task task : sortdedTasks) {
                    String[] taskDate = task.getDate().split("\\.");
                    if(Integer.valueOf(from[0]) <= Integer.valueOf(taskDate[0])
                            && Integer.valueOf(to[0]) >= Integer.valueOf(taskDate[0])){
                        if(Integer.valueOf(from[1]) <= Integer.valueOf(taskDate[1]) &&
                                Integer.valueOf(to[1]) >= Integer.valueOf(taskDate[1])){
                            if(Integer.valueOf(from[2]) <= Integer.valueOf(taskDate[2]) &&
                                    Integer.valueOf(to[2]) >= Integer.valueOf(taskDate[2])) {
                                sortedList.add(task);
                            }
                        }
                    }
                }
        return sortedList;
    }


    public ArrayList<Task> addMarks(ArrayList<Task> sortedTask){
        ArrayList<Task> sortedWithMarks = sortedTask;
        String date = sortedWithMarks.get(0).getDate();
        sortedWithMarks.get(0).setType(1);

        for (int i = 1; i < sortedWithMarks.size(); i++){
            if(sortedWithMarks.get(i).getDate().equals(date)){
                sortedWithMarks.get(i).setType(2);
            }else {
                sortedWithMarks.get(i).setType(1);
                date = sortedTask.get(i).getDate();
            }
        }
        return sortedWithMarks;
    }

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

        int id = item.getItemId();

        if (id == R.id.search_action) {
            showSearchDialog();
        }

        return super.onOptionsItemSelected(item);
    }


    public void showSearchDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.Theme_AppCompat_Dialog));
        View view = View.inflate(getContext(), R.layout.dialog_search, null);
        dateFrom = (TextView) view.findViewById(R.id.dateFrom);
        dateTo = (TextView) view.findViewById(R.id.dateTo);
        if(configurator.isFilterActive()){
            dateFrom.setText(configurator.getFilterDateFrom());
            dateTo.setText(configurator.getFilterDateTo());
            initCalIfFilter();
        }else initCal();
        builder.setTitle("Выберите период")
                .setView(view)
                .setPositiveButton("Применить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(dateFrom.getText().equals("") || dateTo.getText().equals("")){
                            Toast.makeText(getContext(), "Нужно ввести даты поиска", Toast.LENGTH_SHORT).show();
                            showSearchDialog();
                        }else {
                        dateFromSet = dateFrom.getText().toString();
                        dateToSet = dateTo.getText().toString();
                        configurator.setFilterDateFrom(dateFromSet);
                        configurator.setFilterDateTo(dateToSet);
                        configurator.setFilterActive(true);
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
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });



        dateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(getContext(), R.style.Theme_AppCompat_Dialog,
                        mDateToSetListner, year_to, month_to, day_to);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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

    public void initCal(){
        final Calendar cal = Calendar.getInstance();
        year_from = cal.get(Calendar.YEAR);
        month_from = cal.get(Calendar.MONTH);
        day_from = cal.get(Calendar.DAY_OF_MONTH);
        year_to = cal.get(Calendar.YEAR);
        month_to = cal.get(Calendar.MONTH);
        day_to = cal.get(Calendar.DAY_OF_MONTH);
    }

    public void initCalIfFilter(){
        final Calendar cal = Calendar.getInstance();
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

    public Dialog showDateFromPickDialog(){
        return new DatePickerDialog(getContext(), dFromPickerListner, year_from, month_from, day_from );
    }
    public Dialog showDateToPickDialog(){
        return new DatePickerDialog(getContext(), dToPickerListner, year_to, month_to, day_to );
    }

    private DatePickerDialog.OnDateSetListener dFromPickerListner
            = new DatePickerDialog.OnDateSetListener(){

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            year_from = year;
            month_from = month;
            day_from = dayOfMonth;
        }
    };

    private DatePickerDialog.OnDateSetListener dToPickerListner
            = new DatePickerDialog.OnDateSetListener(){

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            year_to = year;
            month_to = month;
            day_to = dayOfMonth;
        }
    };


    public void initRW(){
        if(configurator.isOnlyOpened()){
            if(configurator.isFilterActive()){
                filterClose.setVisibility(rootView.VISIBLE);
                tasks = dbTasksHelper.getOpenedTask();
                if(tasks.size() != 0){
                    filtred = sortlistFromTo(tasks, dateFromSet, dateToSet);
                    sortlist(filtred);
                    addMarks(filtred);
                    configurator.setTasks(filtred);
                    adapter = new RecyclerViewAdapter(filtred, rootView.getContext());
                    adapter.notifyDataSetChanged();
                }
            }else {
                filterClose.setVisibility(rootView.INVISIBLE);
                tasks = dbTasksHelper.getOpenedTask();
                if(tasks.size() != 0){
                    sortlist(tasks);
                    addMarks(tasks);
                }
                adapter = new RecyclerViewAdapter(tasks, rootView.getContext());
                adapter.notifyDataSetChanged();
            }
            rv.setAdapter(adapter);
            rv.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            rv.setLayoutManager(llm);
        }else {

            if(configurator.isFilterActive()) {
                filterClose.setVisibility(rootView.VISIBLE);
                tasks = dbTasksHelper.getAllTasks();
                if (tasks.size() != 0) {
                    filtred = sortlistFromTo(tasks, dateFromSet, dateToSet);
                    sortlist(filtred);
                    addMarks(filtred);
                    configurator.setTasks(filtred);
                    adapter = new RecyclerViewAdapter(filtred, rootView.getContext());
                    adapter.notifyDataSetChanged();
                }
            } else {
                filterClose.setVisibility(rootView.INVISIBLE);
                tasks = dbTasksHelper.getAllTasks();
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

}
