package com.example.takunaka.taskapp.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.takunaka.taskapp.Configurator;
import com.example.takunaka.taskapp.R;
import com.example.takunaka.taskapp.adapters.RecyclerViewAdapter;
import com.example.takunaka.taskapp.sql.DBTasksHelper;
import com.example.takunaka.taskapp.sqlQuerry.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MainFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private DBTasksHelper dbTasksHelper;
    private RecyclerViewAdapter adapter;
    private RecyclerView rv;
    private Switch mSwitch;
    private View rootView;
    private ArrayList<Task> tasks = new ArrayList<>();
    private Configurator configurator = Configurator.getInstance();

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

        mSwitch.setOnCheckedChangeListener(this);



        dbTasksHelper = new DBTasksHelper(rootView.getContext());

        rv.setHasFixedSize(true);
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
            tasks = dbTasksHelper.getOpenedTask();
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
        }else {
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        rv.setHasFixedSize(true);
        if (!isChecked){
            tasks = dbTasksHelper.getOpenedTask();
            if(tasks.size() != 0){
                sortlist(tasks);
                addMarks(tasks);
            }
            adapter = new RecyclerViewAdapter(tasks, rootView.getContext());
            adapter.notifyDataSetChanged();
            configurator.setOnlyOpened(true);

        }else {
            tasks = dbTasksHelper.getAllTasks();
            if(tasks.size() != 0){
                sortlist(tasks);
                addMarks(tasks);
            }
            adapter = new RecyclerViewAdapter(tasks, rootView.getContext());
            adapter.notifyDataSetChanged();
            configurator.setOnlyOpened(false);
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
}
