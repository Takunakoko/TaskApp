package com.example.takunaka.taskapp.fragments;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.takunaka.taskapp.R;
import com.example.takunaka.taskapp.adapters.RecyclerViewAdapter;
import com.example.takunaka.taskapp.sql.DBTasksHelper;

public class MainFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private DBTasksHelper dbTasksHelper;
    private RecyclerViewAdapter adapter;
    private RecyclerView rv;
    private Switch mSwitch;
    private View rootView;
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
        adapter = new RecyclerViewAdapter(dbTasksHelper.getOpenedTask(), rootView.getContext());
        adapter.notifyDataSetChanged();
        rv.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        rv.setHasFixedSize(true);
        if (!isChecked){
            adapter = new RecyclerViewAdapter(dbTasksHelper.getOpenedTask(), rootView.getContext());
            adapter.notifyDataSetChanged();
        }else {
            adapter = new RecyclerViewAdapter(dbTasksHelper.getAllTasks(), rootView.getContext());
            adapter.notifyDataSetChanged();
        }
        rv.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
    }
}
