package com.example.takunaka.taskapp.adapters;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.EditText;
import android.widget.TextView;

import com.example.takunaka.taskapp.R;
import com.example.takunaka.taskapp.fragments.ShowTaskFragment;
import com.example.takunaka.taskapp.sql.DBSubTasksHelper;
import com.example.takunaka.taskapp.sqlQuerry.Task;
import com.example.takunaka.taskapp.sqlQuerry.TaskContainer;
import com.example.takunaka.taskapp.sqlQuerry.UserContainer;

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
        Bundle args = new Bundle();
        args.putInt("ID", position);
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
        private RecyclerView rv;
        private DBSubTasksHelper dbSubTasksHelper;
        private RecyclerViewSubItemAdapter adapter;
        private int selectedID;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

            Bundle args = getArguments();

            rootView = inflater.inflate(R.layout.viewpager_item, container, false);

            name = (TextView) rootView.findViewById(R.id.NameCreateField);
            date = (TextView) rootView.findViewById(R.id.DateCreateField);
            state = (TextView) rootView.findViewById(R.id.StateCreateField);
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
            selectedID = args.getInt("ID");

            dbSubTasksHelper = new DBSubTasksHelper(rootView.getContext());
            rv = (RecyclerView) rootView.findViewById(R.id.recyclerView2);
            adapter = new RecyclerViewSubItemAdapter(dbSubTasksHelper.getAllSubTasks(args.getInt("ID")), rootView.getContext());
            rv.setHasFixedSize(true);
            rv.setAdapter(adapter);
            LinearLayoutManager llm = new LinearLayoutManager(rootView.getContext());
            rv.setLayoutManager(llm);

            return rootView;
        }



        public void showDialog() {
            final DBSubTasksHelper dbSubTasksHelper = new DBSubTasksHelper(getContext());
            final SQLiteDatabase db = dbSubTasksHelper.getWritableDatabase();
            final ContentValues cv = new ContentValues();

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            final View dialogview = View.inflate(getContext(), R.layout.dialog_add_description, null);
            builder.setTitle("Добавить дело")
                    .setView(dialogview)
                    .setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText description = (EditText) dialogview.findViewById(R.id.descriptionDialog);
                            cv.put(DBSubTasksHelper.KEY_DESCRIPTION, description.getText().toString());
                            cv.put(DBSubTasksHelper.KEY_STATE, "В работе");
                            cv.put(DBSubTasksHelper.KEY_NAMEID, UserContainer.getSelectedID());
                            cv.put(DBSubTasksHelper.KEY_TASKID, TaskContainer.getSelectedTaskID());
                            db.insert(DBSubTasksHelper.TABLE_SUBTASK, null, cv);
                            adapter.updateSet(dbSubTasksHelper.getAllSubTasks(ShowTaskFragment.pos));
                            adapter.notifyDataSetChanged();
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
