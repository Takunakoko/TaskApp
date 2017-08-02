package com.example.takunaka.taskapp.adapters;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import com.example.takunaka.taskapp.R;
import com.example.takunaka.taskapp.tmpPack.ListItem;
import com.example.takunaka.taskapp.tmpPack.SubTasks;

import java.util.List;

/**
 * Created by takunaka on 02.08.17.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private static List<ListItem> listItemsAdapt;
    private FragmentManager fm;


    public ViewPagerAdapter(List<ListItem> listItems, FragmentManager fm) {
        super(fm);
        this.listItemsAdapt = listItems;
    }

    @Override
    public Fragment getItem(int position) {
        ListItem li = listItemsAdapt.get(position);
        Fragment fragment = new ViewPagerFragment();
        Bundle args = new Bundle();
        args.putString("Name", li.getName());
        args.putString("Date", li.getDate());
        args.putString("State", li.getState());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return listItemsAdapt.size();
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

            RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.recyclerView2);
            RecyclerViewSubItemAdapter adapter = new RecyclerViewSubItemAdapter(SubTasks.getSubTasks(), rootView.getContext());
            rv.setHasFixedSize(true);
            rv.setAdapter(adapter);
            LinearLayoutManager llm = new LinearLayoutManager(rootView.getContext());
            rv.setLayoutManager(llm);

            return rootView;
        }

        public void showDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Добавить дело")
                    .setView(R.layout.dialog_signin)
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
