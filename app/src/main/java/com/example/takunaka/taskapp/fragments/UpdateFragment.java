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
import android.widget.EditText;

import com.example.takunaka.taskapp.Configurator;
import com.example.takunaka.taskapp.R;
import com.example.takunaka.taskapp.sql.DBTasksHelper;
import com.example.takunaka.taskapp.sqlQuerry.TaskContainer;


public class UpdateFragment extends Fragment {

    private ShowTaskFragment showTaskFragment;
    private EditText name;
    private EditText date;
    private EditText state;
    private DBTasksHelper dbTasksHelper;

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
        View rootView = inflater.inflate(R.layout.fragment_update, container, false);
        name = (EditText) rootView.findViewById(R.id.NameUpdateField);
        date = (EditText) rootView.findViewById(R.id.DateUpdateField);
        state = (EditText) rootView.findViewById(R.id.StateUpdateField);

        return rootView;


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
            dbTasksHelper = new DBTasksHelper(getContext());

            dbTasksHelper.updateRow("UPDATE " + dbTasksHelper.TABLE_TASKS + " SET "
                    + dbTasksHelper.KEY_DESCRIPTION + " = " + name.getText().toString() + ", "
                    + dbTasksHelper.KEY_DATE + " = " + date.getText().toString() + ", "
                    + dbTasksHelper.KEY_STATE + " = " + state.getText().toString()
                    + " WHERE " + dbTasksHelper.KEY_ID + " = " + TaskContainer.getSelectedTaskID());


            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            showTaskFragment = new ShowTaskFragment();
            fragmentTransaction.replace(R.id.container, showTaskFragment, "Update");
            fragmentTransaction.addToBackStack("Show");
            fragmentTransaction.commit();
        }

        return super.onOptionsItemSelected(item);
    }

}
