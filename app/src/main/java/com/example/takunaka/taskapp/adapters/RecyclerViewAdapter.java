package com.example.takunaka.taskapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.takunaka.taskapp.Configurator;
import com.example.takunaka.taskapp.R;
import com.example.takunaka.taskapp.fragments.ShowTaskFragment;
import com.example.takunaka.taskapp.sql.DBTasksHelper;
import com.example.takunaka.taskapp.sqlQuerry.Task;
import com.example.takunaka.taskapp.sqlQuerry.TaskContainer;

import java.util.ArrayList;
import java.util.List;


//класс адаптера отображения recyclerView
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Configurator config = Configurator.getInstance();
    private ShowTaskFragment stFragment;
    private ArrayList<Task> listTasks;
    private Context context;
    DBTasksHelper dbTasksHelper;
    private int viewTypeSelected = 0;


    public RecyclerViewAdapter(ArrayList<Task> listTasks, Context context) {
        this.listTasks = listTasks;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = 0;
        if(listTasks.get(position).getType() == 1){
            viewType = 1;
            viewTypeSelected = viewType;
        }else if(listTasks.get(position).getType() == 2){
            viewType = 2;
            viewTypeSelected = viewType;
        }
        return viewType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());

        switch (viewType){
            case 1:
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_header, parent, false);
                return new ViewHolder(v);
            case 2:
                View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
                return new ViewHolder(v2);
            default:
                View v3 = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
                return new ViewHolder(v3);
        }


    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Task li = listTasks.get(position);
        //установка отображения элементов на странице
        holder.name.setText(li.getDesription());
        holder.state.setText(li.getState());
        if(viewTypeSelected == 1){
            holder.headerDate.setText(li.getDate());
        }
        if(li.getState().equals("Выполняется")){
            holder.imageView.setBackgroundResource(R.drawable.ic_play_circle_filled_black_24dp);
            holder.state.setTextColor(Color.parseColor("#97B47B"));
        }else {
            holder.imageView.setBackgroundResource(R.drawable.ic_check_circle_black_24dp);
            holder.state.setTextColor(Color.parseColor("#CB809B"));
        }


    }

    @Override
    public int getItemCount() {
        return listTasks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView name;
        private TextView state;
        private TextView headerDate;
        private RelativeLayout rl;
        private ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            //привязка элементов к xml файлу
            name = (TextView) itemView.findViewById(R.id.Name);
            state = (TextView) itemView.findViewById(R.id.State);
            headerDate = (TextView) itemView.findViewById(R.id.header_title);
            rl = (RelativeLayout) itemView.findViewById(R.id.task);
            imageView = (ImageView) itemView.findViewById(R.id.imageViewStatus);
            rl.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            config.setAdapterPosition(getAdapterPosition());
            TaskContainer.setSelectedTaskID(getAdapterPosition());
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            stFragment = new ShowTaskFragment();
            ft.replace(R.id.container, stFragment, "Main");
            ft.addToBackStack(null);
            ft.commit();
        }

    }


}