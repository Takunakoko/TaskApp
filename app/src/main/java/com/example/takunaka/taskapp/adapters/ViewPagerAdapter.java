package com.example.takunaka.taskapp.adapters;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.takunaka.taskapp.R;
import com.example.takunaka.taskapp.sql.DBSubTasksHelper;
import com.example.takunaka.taskapp.sqlQuerry.Task;
import com.example.takunaka.taskapp.sqlQuerry.TaskContainer;

import java.util.List;

/**
 * Created by takunaka on 02.08.17.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private static List<Task> listTaskAdapt;
    private FragmentManager fm;


    public ViewPagerAdapter(List<Task> listItems, FragmentManager fm) {
        super(fm);
        this.listTaskAdapt = listItems;
    }

    @Override
    public Fragment getItem(int position) {
        Task li = listTaskAdapt.get(position);
        Fragment fragment = new ViewPagerFragment();
        TaskContainer.setSelectedTaskID(li.getTaskID());
        TaskContainer.setSelectedDesription(li.getDesription());
        TaskContainer.setSelectedDate(li.getDate());
        TaskContainer.setSelectedState(li.getState());
        Bundle args = new Bundle();
        args.putString("Name", li.getDesription());
        args.putString("Date", li.getDate());
        args.putString("State", li.getState());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return listTaskAdapt.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Title";
    }

    public static class ViewPagerFragment extends Fragment {

        private TextView name;
        private TextView date;
        private TextView state;
        private Button addSubItemBtn;
        private View rootView;
        private DBSubTasksHelper dbSubTasksHelper;
        RecyclerViewSubItemAdapter adapter;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

            Bundle args = getArguments();

            rootView = inflater.inflate(R.layout.viewpager_item, container, false);

            name = (TextView) rootView.findViewById(R.id.NameUpdateField);
            date = (TextView) rootView.findViewById(R.id.DateUpdateField);
            state = (TextView) rootView.findViewById(R.id.StateUpdateField);
            addSubItemBtn = (Button) rootView.findViewById(R.id.addSubShowTaskButton);


            addSubItemBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog();
                }
            });

            name.setText(args.getString("Name"));
            date.setText(args.getString("Date"));
            state.setText(args.getString("State"));

            dbSubTasksHelper = new DBSubTasksHelper(rootView.getContext());
            RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.recyclerView2);
            adapter = new RecyclerViewSubItemAdapter(dbSubTasksHelper.getAllSubTasks(), rootView.getContext());
            rv.setHasFixedSize(true);
            rv.setAdapter(adapter);
            LinearLayoutManager llm = new LinearLayoutManager(rootView.getContext());
            rv.setLayoutManager(llm);
            return rootView;
        }



        public void showDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Добавить дело")
                    .setView(R.layout.dialog_add_description)
                    .setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

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


    }

}
