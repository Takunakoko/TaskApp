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

import com.example.takunaka.taskapp.Configurator;
import com.example.takunaka.taskapp.R;
import com.example.takunaka.taskapp.adapters.ViewPagerAdapter;
import com.example.takunaka.taskapp.sql.DBTasksHelper;
import com.example.takunaka.taskapp.sqlQuerry.Task;
import com.example.takunaka.taskapp.sqlQuerry.TaskContainer;


public class ShowTaskFragment extends Fragment implements ViewPager.OnPageChangeListener {

    private ViewPagerAdapter mViewPagerAdapter;
    private ViewPager mViewPager;
    private UpdateFragment uFragment;
    private Configurator configurator = Configurator.getInstance();
    private DBTasksHelper dbTasksHelper;
    private View rootView;
    public ShowTaskFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_show_task, container, false);
        mViewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        initPW();

        mViewPager.addOnPageChangeListener(this);

        setHasOptionsMenu(true);
        //((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();
        mViewPagerAdapter.notifyDataSetChanged();

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mViewPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menu.findItem(R.id.action_edit).setVisible(true);
        menu.findItem(R.id.account_action).setVisible(false);
        menu.findItem(R.id.action_save).setVisible(false);
        menu.findItem(R.id.addTask).setVisible(false);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_edit) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            uFragment = new UpdateFragment();
            fragmentTransaction.replace(R.id.container, uFragment, "Show");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
        return super.onOptionsItemSelected(item);
    }


    public void initPW(){
        dbTasksHelper = new DBTasksHelper(rootView.getContext());
        mViewPagerAdapter = new ViewPagerAdapter(dbTasksHelper.getAllTasks(), getFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setCurrentItem(configurator.getAdapterPosition());
        TaskContainer.setSelectedTask(dbTasksHelper.getAllTasks().get(configurator.getAdapterPosition()));
    }

}
