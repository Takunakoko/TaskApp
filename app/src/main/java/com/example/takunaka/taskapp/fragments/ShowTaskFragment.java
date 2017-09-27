package com.example.takunaka.taskapp.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.takunaka.taskapp.Cfg;
import com.example.takunaka.taskapp.MainActivity;
import com.example.takunaka.taskapp.R;
import com.example.takunaka.taskapp.adapters.ViewPagerAdapter;
import com.example.takunaka.taskapp.sql.DBHelper;
import com.example.takunaka.taskapp.sqlQuerry.SubTask;
import com.example.takunaka.taskapp.sqlQuerry.TaskContainer;

import java.util.List;


public class ShowTaskFragment extends Fragment implements ViewPager.OnPageChangeListener {

    private ViewPagerAdapter mViewPagerAdapter;
    private ViewPager mViewPager;
    private Cfg cfg = Cfg.getInstance();
    private DBHelper dbHelper;
    private View rootView;

    public ShowTaskFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_show_task, container, false);
        mViewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        //вызов метода инициализции PageViewer
        initPV();
        //добавление прослушки для PV
        mViewPager.addOnPageChangeListener(this);
        //включение меню тулбара
        setHasOptionsMenu(true);
        //кнопка назад
        ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayShowHomeEnabled(true);
        }
        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();
        //инициализация PV
        initPV();
        mViewPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //проверка первого элемента. если элемент первый - вывод сообщения.
        if (position == 0 && positionOffset == 0 && positionOffsetPixels == 0) {
            checkSubTaskState(position);
        }
    }

    @Override
    public void onPageSelected(int position) {
        //вывод сообшения для всех элементов кроме первого.
        if (position != 0) {
            checkSubTaskState(position);
        }

    }

    /**
     * метод проверки статуса у дел
     * если все дела закрыты - спрашивает закрыть ли задачу
     *
     * @param position позиция для определения выбранной задачи
     */
    private void checkSubTaskState(int position) {
        if (cfg.isOnlyOpened()) {
            TaskContainer.setSelectedTask(dbHelper.getTasks("openedSort", 0, 0).get(position));
            //проверка статуса задачи из списка открытых задач
            if (TaskContainer.getSelectedTask().getState().equals(getResources().getStringArray(R.array.states)[0])) {
                int id = TaskContainer.getSelectedTask().getTaskID();
                //если истина - проверка статуса всех дел
                if (checkTasks(id)) {
                    //если истина - предложение закрыть все задачи
                    showCloseDialog(id);
                }
            }
        } else {
            TaskContainer.setSelectedTask(dbHelper.getTasks("allSort", 0, 0).get(position));
            //проверка статуса задачи из списка открытых задач
            if (TaskContainer.getSelectedTask().getState().equals(getResources().getStringArray(R.array.states)[0])) {
                int id = TaskContainer.getSelectedTask().getTaskID();
                //если истина - проверка статуса всех дел
                if (checkTasks(id)) {
                    //если истина - предложение закрыть все задачи
                    showCloseDialog(id);
                }
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menu.findItem(R.id.account_action).setVisible(false);
        menu.findItem(R.id.action_save).setVisible(false);
        menu.findItem(R.id.addTask).setVisible(false);
        menu.findItem(R.id.action_edit).setVisible(true);
        menu.findItem(R.id.action_save_create).setVisible(false);
        menu.findItem(R.id.search_action).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        //настройка кнопок меню
        if (id == R.id.action_edit) {
            //переход на фрагмент Update
            ((MainActivity) getActivity()).changeFragment("Update");
        }
        if (id == android.R.id.home) {
            //переход на фрагмент Main
            ((MainActivity) getActivity()).changeFragment("Main");
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * метод инициализации pageViewer
     */
    private void initPV() {
        //инициализация PageViewer
        dbHelper = new DBHelper(rootView.getContext());
        //Если активен фильтр по датам
        if (cfg.isFilterActive()) {
            //инициализация со списком для выбранных задач(берется из временного хранилища. попадает туда при инициализациии списка)
            mViewPagerAdapter = new ViewPagerAdapter(cfg.getTasks(), getFragmentManager());
            //установка выбранной задачи в контейнер
            TaskContainer.setSelectedTask(dbHelper.getTasks("openedSort", 0, 0).get(cfg.getAdapterPosition()));
        } else {
            //если фильтр не активен
            if (cfg.isOnlyOpened()) {
                //инициализация только открытых задач
                mViewPagerAdapter = new ViewPagerAdapter(dbHelper.getTasks("openedSort", 0, 0), getFragmentManager());
                //установка выбранной задачи в контейнер
                TaskContainer.setSelectedTask(dbHelper.getTasks("openedSort", 0, 0).get(cfg.getAdapterPosition()));
            } else {
                //инициализация со всем списком
                mViewPagerAdapter = new ViewPagerAdapter(dbHelper.getTasks("allSort", 0, 0), getFragmentManager());
                //установка выбранной задачи в контейнер
                TaskContainer.setSelectedTask(dbHelper.getTasks("allSort", 0, 0).get(cfg.getAdapterPosition()));
            }
        }
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setCurrentItem(cfg.getAdapterPosition());
        mViewPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * проверка на необходимость закрыть задачу в связи с отсутствием открытых дел
     *
     * @param id идентификатор задачи
     * @return возвращает истину, если нет ни одного дела со статусом в работе
     */
    private boolean checkTasks(int id) {
        boolean needClose = true;
        List<SubTask> list = dbHelper.getAllSubTasks(id);
        for (SubTask subTask : list) {
            if (subTask.getState().equals(getResources().getStringArray(R.array.states)[2])) {
                needClose = false;
            }
            //если в списке есть хотя бы одна задача в работе - возвращает ложь. Иначе - возвращает истину.
        }
        return needClose;
    }

    /**
     * предложение пользователю закрыть задачу если нет дел в работе
     *
     * @param id идентификатор открытой задачи
     */
    private void showCloseDialog(final int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.Theme_AppCompat_Dialog));
        builder.setTitle(R.string.all_task_closed_question)
                //кнопка закрыть
                .setPositiveButton(R.string.all_task_close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //внесение в БД изменений
                        dbHelper.closeAllSubTasks(getResources().getStringArray(R.array.states)[1], id);
                        //переходим обратно в список задач
                        ((MainActivity) getActivity()).changeFragment("Main");
                    }
                })
                //отказ
                .setNegativeButton(R.string.all_task_continue, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        //закрывает диалог
                        dialog.cancel();

                    }
                });
        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

}
