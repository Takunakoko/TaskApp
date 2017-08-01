package com.example.takunaka.taskapp.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.takunaka.taskapp.R;
import com.example.takunaka.taskapp.adapters.RecyclerViewAdapter;
import com.example.takunaka.taskapp.tmpPack.Tasks;


public class MainFragment extends Fragment {

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
        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        rv.setHasFixedSize(true);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(Tasks.getListItems(), inflater.getContext());
        rv.setAdapter(adapter);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        return rootView;
    }


}
