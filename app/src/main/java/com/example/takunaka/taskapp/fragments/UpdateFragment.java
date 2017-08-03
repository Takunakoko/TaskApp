package com.example.takunaka.taskapp.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.takunaka.taskapp.Configurator;
import com.example.takunaka.taskapp.R;


public class UpdateFragment extends Fragment {

    ShowTaskFragment showTaskFragment;

    public UpdateFragment() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_update, container, false);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menu.findItem(R.id.action_edit).setVisible(false);
        menu.findItem(R.id.account_action).setVisible(false);
        menu.findItem(R.id.action_save).setVisible(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            showTaskFragment = new ShowTaskFragment();
            fragmentTransaction.replace(R.id.container, showTaskFragment, "Update");
            fragmentTransaction.addToBackStack("Show");
            fragmentTransaction.commit();
        }

        return super.onOptionsItemSelected(item);
    }

}
