package com.example.takunaka.taskapp.fragments;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.takunaka.taskapp.Configurator;
import com.example.takunaka.taskapp.R;
import com.example.takunaka.taskapp.adapters.ViewPagerAdapter;
import com.example.takunaka.taskapp.sql.DBHelper;
import com.example.takunaka.taskapp.sqlQuerry.SubTask;
import com.example.takunaka.taskapp.sqlQuerry.TaskContainer;

import java.util.List;


public class ShowTaskFragment extends Fragment implements ViewPager.OnPageChangeListener {

    private ViewPagerAdapter mViewPagerAdapter;
    private ViewPager mViewPager;
    private UpdateFragment uFragment;
    private MainFragment mainFragment;
    private Configurator configurator = Configurator.getInstance();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_show_task, container, false);
        mViewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        //вызов метода инициализции PageViewer
        initPW();
        //добавление прослушки для PV
        mViewPager.addOnPageChangeListener(this);
        //включение меню тулбара
        setHasOptionsMenu(true);
        //кнопка назад
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();
        //инициализация PV
        initPW();
        mViewPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //проверка первого элемента. если элемент первый - вывод сообщения.
        if(position == 0 && positionOffset == 0 && positionOffsetPixels == 0){
            checkSubTaskState(position);
        }
    }

    @Override
    public void onPageSelected(int position) {
        //вывод сообшения для всех элементов кроме первого.
        if(position != 0){
            checkSubTaskState(position);
        }

    }
    // проверка сабтасков
    public void checkSubTaskState(int position){
        if(configurator.isOnlyOpened()) {
            TaskContainer.setSelectedTask(dbHelper.getOpenedSortedTask().get(position));
            //проверка статуса задачи из списка открытых задач
            if(TaskContainer.getSelectedTask().getState().equals("Выполняется")){
                int id = TaskContainer.getSelectedTask().getTaskID();
                //если истина - проверка статуса всех дел
                if(checkTasks(id)){
                    //если истина - предложение закрыть все задачи
                    showCloseDialog(id);
                }
            }
        }else {
            //тоже самое что и выше, но со всеми задачами
            TaskContainer.setSelectedTask(dbHelper.getAllSortedTasks().get(position));
            if(TaskContainer.getSelectedTask().getState().equals("Выполняется")){
                int id = TaskContainer.getSelectedTask().getTaskID();
                if(checkTasks(id)){
                    showCloseDialog(id);
                }
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menu.findItem(R.id.account_action).setVisible(false);
        menu.findItem(R.id.action_save).setVisible(false);
        menu.findItem(R.id.addTask).setVisible(false);
        menu.findItem(R.id.action_edit).setVisible(true);
        menu.findItem(R.id.action_save_create).setVisible(false);
        menu.findItem(R.id.search_action).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        //настройка кнопок меню
        if (id == R.id.action_edit) {
            uFragment = new UpdateFragment();
            getFragmentManager().beginTransaction()
            .replace(R.id.container, uFragment)
            .commit();
        }
        if(id == android.R.id.home){
            mainFragment = new MainFragment();
            getFragmentManager().popBackStack();
            getFragmentManager().beginTransaction()
            .replace(R.id.container, mainFragment)
            .commit();
        }
        return super.onOptionsItemSelected(item);
    }


    public void initPW(){
        //инициализация PageViewer
        dbHelper = new DBHelper(rootView.getContext());
        //Если активен фильтр по датам
        if(configurator.isFilterActive()){
            //инициализация со списком для выбранных задач(берется из временного хранилища. попадает туда при инициализациии списка)
            mViewPagerAdapter = new ViewPagerAdapter(configurator.getTasks(), getFragmentManager());
            mViewPager.setAdapter(mViewPagerAdapter);
            mViewPager.setCurrentItem(configurator.getAdapterPosition());
            TaskContainer.setSelectedTask(dbHelper.getOpenedSortedTask().get(configurator.getAdapterPosition()));
            mViewPagerAdapter.notifyDataSetChanged();
        }else {
            //если фильтр не активен
            if(configurator.isOnlyOpened()){
                //инициализация только открытых задач
                mViewPagerAdapter = new ViewPagerAdapter(dbHelper.getOpenedSortedTask(), getFragmentManager());
                mViewPager.setAdapter(mViewPagerAdapter);
                mViewPager.setCurrentItem(configurator.getAdapterPosition());
                TaskContainer.setSelectedTask(dbHelper.getOpenedSortedTask().get(configurator.getAdapterPosition()));
                mViewPagerAdapter.notifyDataSetChanged();
            }
            else {
                //инициализация со всем списком
                mViewPagerAdapter = new ViewPagerAdapter(dbHelper.getAllSortedTasks(), getFragmentManager());
                mViewPager.setAdapter(mViewPagerAdapter);
                mViewPager.setCurrentItem(configurator.getAdapterPosition());
                TaskContainer.setSelectedTask(dbHelper.getAllSortedTasks().get(configurator.getAdapterPosition()));
                mViewPagerAdapter.notifyDataSetChanged();
            }
        }



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //проверка дел на статус "в работе"
    public boolean checkTasks(int id){
        boolean needClose = true;
        List<SubTask> list = dbHelper.getAllSubTasks(id);
        for (SubTask subTask : list){
            if(subTask.getState().equals("В работе")){
                needClose = false;
            }
            //если в списке есть хотя бы одна задача в работе - возвращает ложь. Иначе - возвращает истину.
        }return needClose;
    }

    //предложение пользователю закрыть задачу если нет дел в работе
    public void showCloseDialog(final int id){
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.Theme_AppCompat_Dialog));
        builder.setTitle("Все дела в задаче закрыты! Закрыть задачу?")
                .setPositiveButton("Закрыть", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //если пользователь соглашается
                        //открываем базу данных
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ContentValues cv = new ContentValues();
                        cv.put(DBHelper.KEY_STATE, "Закрыта");
                        //вносим изменения
                        db.update(DBHelper.TABLE_TASKS, cv, DBHelper.KEY_ID + " = " + id, null);
                        //переходим обратно в список задач
                        mainFragment = new MainFragment();
                        getFragmentManager().popBackStack();
                        getFragmentManager().beginTransaction()
                                .replace(R.id.container, mainFragment)
                                .commit();
                    }
                })
                .setNegativeButton("Продолжить работу", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                });

        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

}
