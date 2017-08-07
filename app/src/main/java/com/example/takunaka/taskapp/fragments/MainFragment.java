package com.example.takunaka.taskapp.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.takunaka.taskapp.R;
import com.example.takunaka.taskapp.adapters.RecyclerViewAdapter;
import com.example.takunaka.taskapp.sql.DBTasksHelper;

public class MainFragment extends Fragment {

    private DBTasksHelper dbTasksHelper;
    private RecyclerViewAdapter adapter;
    private RecyclerView rv;

    public MainFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.fragment_main, container, false);
        rv = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        dbTasksHelper = new DBTasksHelper(rootView.getContext());
        rv.setHasFixedSize(true);
        adapter = new RecyclerViewAdapter(dbTasksHelper.getAllTasks(), inflater.getContext());
        rv.setAdapter(adapter);



        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        notifyData();

    }

    public void notifyData(){
        adapter.notifyDataSetChanged();
    }

}
