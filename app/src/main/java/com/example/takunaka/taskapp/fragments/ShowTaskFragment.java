package com.example.takunaka.taskapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.takunaka.taskapp.Configurator;
import com.example.takunaka.taskapp.R;
import com.example.takunaka.taskapp.adapters.RecyclerViewSubItemAdapter;
import com.example.takunaka.taskapp.adapters.ViewPagerAdapter;
import com.example.takunaka.taskapp.tmpPack.SubTasks;
import com.example.takunaka.taskapp.tmpPack.Tasks;


public class ShowTaskFragment extends Fragment implements ViewPager.OnPageChangeListener {

    private ViewPagerAdapter mViewPagerAdapter;
    private ViewPager mViewPager;
    private Configurator configurator = Configurator.getInstance();


    public ShowTaskFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_show_task, container, false);
        mViewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        mViewPagerAdapter = new ViewPagerAdapter(Tasks.getListItems(), getFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setCurrentItem(configurator.getAdapterPosition());
        mViewPager.addOnPageChangeListener(this);

        return rootView;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
