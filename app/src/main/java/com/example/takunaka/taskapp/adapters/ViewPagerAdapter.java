package com.example.takunaka.taskapp.adapters;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.example.takunaka.taskapp.Cfg;
import com.example.takunaka.taskapp.R;
import com.example.takunaka.taskapp.Utils;
import com.example.takunaka.taskapp.sql.DBHelper;
import com.example.takunaka.taskapp.sqlQuerry.Task;
import com.example.takunaka.taskapp.sqlQuerry.UserContainer;

import java.util.List;


public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    //лист с элементами pageViewer
    private static List<Task> listTaskAdapt;
    private static final String bundle_ID = "ID";
    private static final String bundle_NAME = "NAME";
    private static final String bundle_DATE = "DATE";
    private static final String bundle_STATE = "STATE";

    public ViewPagerAdapter(@NonNull List<Task> listItems, @NonNull FragmentManager fm) {
        super(fm);
        listTaskAdapt = listItems;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        //завязка данных в бандл для ViewPager
        Task li = listTaskAdapt.get(position);
        Fragment fragment = new ViewPagerFragment();
        Bundle args = new Bundle();
        args.putInt(ViewPagerAdapter.bundle_ID, li.getTaskID());
        args.putString(ViewPagerAdapter.bundle_NAME, li.getDescription());
        args.putInt(ViewPagerAdapter.bundle_DATE, li.getDate());
        args.putString(ViewPagerAdapter.bundle_STATE, li.getState());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return listTaskAdapt.size();
    }


    public static class ViewPagerFragment extends Fragment {

        private TextView name;
        private TextView date;
        private TextView state;
        private Button addSubItemBtn;
        private View rootView;
        private RecyclerView rv;
        private DBHelper dbHelper;
        private RecyclerViewSubItemAdapter adapter;
        private int selectedID;
        private Cfg config = Cfg.getInstance();

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
            //отркытие бандла с данными
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

            //привязка данных из бандла к нужным полям
            name.setText(args.getString(ViewPagerAdapter.bundle_NAME));
            date.setText(Utils.getStringDate(args.getInt(ViewPagerAdapter.bundle_DATE)));
            state.setText(args.getString(ViewPagerAdapter.bundle_STATE));
            selectedID = args.getInt(ViewPagerAdapter.bundle_ID);
            //инициализация БД
            dbHelper = new DBHelper(rootView.getContext());
            rv = (RecyclerView) rootView.findViewById(R.id.recyclerView2);

            //проверка на статус. если статус "Закрыта" - не отображать добавление дел
            if (state.getText().equals(getResources().getStringArray(R.array.states)[1])) {
                config.setClosed(true);
                addSubItemBtn.setVisibility(View.INVISIBLE);
                //иначе разрешить добавление дел
            } else {
                config.setClosed(false);
                addSubItemBtn.setVisibility(View.VISIBLE);
            }
            //инициализация ресайклвью для дел
            initRV();
            return rootView;
        }

        /**
         * диалог добавления дела
         */
        public void showDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(new android.view.ContextThemeWrapper(getContext(), R.style.Theme_AppCompat_Dialog));
            final View dialogView = View.inflate(getContext(), R.layout.dialog_add_description, null);
            builder.setTitle(R.string.add_sub_task)
                    .setView(dialogView)
                    .setPositiveButton(R.string.add_sub_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText description = (EditText) dialogView.findViewById(R.id.descriptionDialog);
                            //доавбление дела в таблицу в базе данных
                            dbHelper.createSubTask(description.getText().toString(), getResources().getStringArray(R.array.states)[2], UserContainer.getSelectedID(), selectedID);
                            //переинициализация RV сабайтемов
                            initRV();
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton(R.string.abort, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(@NonNull DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        @Override
        public void onResume() {
            super.onResume();
            //инициализация ресайкл вью
            initRV();
            adapter.updateSet(dbHelper.getAllSubTasks(selectedID));
            adapter.notifyDataSetChanged();
        }

        /**
         * метод инициализации RV адаптера дел
         * Дополнительно в нем проверка флага булевой переменной.
         * Если булевая = истина - закрыть все дела.
         */
        public void initRV() {
            adapter = new RecyclerViewSubItemAdapter(dbHelper.getAllSubTasks(selectedID), rootView.getContext(), config.isClosed());
            rv.setHasFixedSize(true);
            rv.setAdapter(adapter);
            LinearLayoutManager llm = new LinearLayoutManager(rootView.getContext());
            rv.setLayoutManager(llm);
        }
    }
}
