package com.example.takunaka.taskapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.takunaka.taskapp.Configurator;
import com.example.takunaka.taskapp.R;
import com.example.takunaka.taskapp.adapters.ViewPagerAdapter;
import com.example.takunaka.taskapp.sql.DBTasksHelper;
import com.example.takunaka.taskapp.sqlQuerry.Task;
import com.example.takunaka.taskapp.sqlQuerry.TaskContainer;

import java.util.ArrayList;


public class ShowTaskFragment extends Fragment implements ViewPager.OnPageChangeListener {

    private ViewPagerAdapter mViewPagerAdapter;
    private ViewPager mViewPager;
    private UpdateFragment uFragment;
    private MainFragment mainFragment;
    private Configurator configurator = Configurator.getInstance();
    private DBTasksHelper dbTasksHelper;
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
        initPW();

        mViewPager.addOnPageChangeListener(this);

        setHasOptionsMenu(true);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();
        initPW();
        mViewPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if(configurator.isOnlyOpened()) {
            TaskContainer.setSelectedTask(dbTasksHelper.getOpenedSortedTask().get(position));
        }else {
            TaskContainer.setSelectedTask(dbTasksHelper.getAllSortedTasks().get(position));
        }
    }

    @Override
    public void onPageSelected(int position) {
        if(configurator.isOnlyOpened()) {
            TaskContainer.setSelectedTask(dbTasksHelper.getOpenedSortedTask().get(position));
        }else {
            TaskContainer.setSelectedTask(dbTasksHelper.getAllSortedTasks().get(position));
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

        if (id == R.id.action_edit) {
            uFragment = new UpdateFragment();
            getFragmentManager().beginTransaction()
            .replace(R.id.container, uFragment)
            .commit();
        }
        if(id == android.R.id.home){
            mainFragment = new MainFragment();
            getFragmentManager().beginTransaction()
            .replace(R.id.container, mainFragment)
            .commit();
        }
        return super.onOptionsItemSelected(item);
    }


    public void initPW(){
        dbTasksHelper = new DBTasksHelper(rootView.getContext());
        if(configurator.isFilterActive()){
            mViewPagerAdapter = new ViewPagerAdapter(configurator.getTasks(), getFragmentManager());
            mViewPager.setAdapter(mViewPagerAdapter);
            mViewPager.setCurrentItem(configurator.getAdapterPosition());
            TaskContainer.setSelectedTask(dbTasksHelper.getOpenedSortedTask().get(configurator.getAdapterPosition()));
            mViewPagerAdapter.notifyDataSetChanged();
        }else {
            if(configurator.isOnlyOpened()){
                mViewPagerAdapter = new ViewPagerAdapter(dbTasksHelper.getOpenedSortedTask(), getFragmentManager());
                mViewPager.setAdapter(mViewPagerAdapter);
                mViewPager.setCurrentItem(configurator.getAdapterPosition());
                TaskContainer.setSelectedTask(dbTasksHelper.getOpenedSortedTask().get(configurator.getAdapterPosition()));
                mViewPagerAdapter.notifyDataSetChanged();

            }
            else {
                dbTasksHelper = new DBTasksHelper(rootView.getContext());
                mViewPagerAdapter = new ViewPagerAdapter(dbTasksHelper.getAllSortedTasks(), getFragmentManager());
                mViewPager.setAdapter(mViewPagerAdapter);
                mViewPager.setCurrentItem(configurator.getAdapterPosition());
                TaskContainer.setSelectedTask(dbTasksHelper.getAllSortedTasks().get(configurator.getAdapterPosition()));
                mViewPagerAdapter.notifyDataSetChanged();
            }
        }



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
