package com.example.takunaka.taskapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.takunaka.taskapp.Configurator;
import com.example.takunaka.taskapp.R;
import com.example.takunaka.taskapp.sql.DBSubTasksHelper;
import com.example.takunaka.taskapp.sqlQuerry.SubTask;

import java.util.List;


//класс адаптера отображения recyclerView
public class RecyclerViewSubItemAdapter extends RecyclerView.Adapter<RecyclerViewSubItemAdapter.ViewHolder> {

    private List<SubTask> subItemsAdapter;
    private Context context;
    private DBSubTasksHelper dbSubTasksHelper;
    private SubTask subTask;
    private boolean isClosed;

    public RecyclerViewSubItemAdapter(List<SubTask> subItems, Context context, boolean isClosed) {
        this.subItemsAdapter = subItems;
        this.context = context;
        this.isClosed = isClosed;
    }

    public void updateSet(List<SubTask> subTasks){
        this.subItemsAdapter.clear();
        this.subItemsAdapter = subTasks;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        subTask = subItemsAdapter.get(position);
        dbSubTasksHelper = new DBSubTasksHelper(context);
        //установка отображения элементов на странице
        holder.description.setText(subTask.getDescription());

        if(isClosed){
            for (SubTask subTask: subItemsAdapter){
                dbSubTasksHelper.updateState(subTask.getId(), subTask.getTaskID(), subTask.getNameID());
            }
        }

        if(subTask.getState().equals("В работе")){
            holder.relativeLayout.setBackgroundColor(Color.parseColor("#424242"));
        } else {
            holder.relativeLayout.setBackgroundColor(Color.parseColor("#282828"));
            holder.description.setTextColor(Color.parseColor("#817BB4"));
            holder.description.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            holder.stateCheck.setEnabled(false);
            holder.stateCheck.setChecked(true);
        }

    }
    @Override
    public int getItemCount() {
        return subItemsAdapter.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView description;
        private CheckBox stateCheck;
        private RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            //привязка элементов к xml файлу
            description = (TextView) itemView.findViewById(R.id.subItemDesc);
            stateCheck = (CheckBox) itemView.findViewById(R.id.checkBox);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.subTaskItem);

            stateCheck.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            SubTask si = subItemsAdapter.get(getAdapterPosition());
            relativeLayout.setBackgroundColor(Color.parseColor("#282828"));
            description.setTextColor(Color.parseColor("#817BB4"));
            description.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            stateCheck.setEnabled(false);

            dbSubTasksHelper.updateState(si.getId(), si.getTaskID(), si.getNameID());
        }
    }


}