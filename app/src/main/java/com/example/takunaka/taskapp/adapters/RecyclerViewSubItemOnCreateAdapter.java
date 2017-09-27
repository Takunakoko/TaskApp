package com.example.takunaka.taskapp.adapters;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.takunaka.taskapp.R;
import com.example.takunaka.taskapp.sqlQuerry.SubTask;

import java.util.List;

//класс адаптера отображения recyclerView
public class RecyclerViewSubItemOnCreateAdapter extends RecyclerView.Adapter<RecyclerViewSubItemOnCreateAdapter.ViewHolder> {

    private List<SubTask> subItemsAdapter;

    public RecyclerViewSubItemOnCreateAdapter(List<SubTask> subItems) {
        this.subItemsAdapter = subItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        SubTask subTask = subItemsAdapter.get(position);
        //установка отображения элементов на странице
        holder.description.setText(subTask.getDescription());
        //покраска новых дел в нужный цвет
        holder.relativeLayout.setBackgroundColor(Color.parseColor("#424242"));
        //уставнока чекбокса неактивным
        holder.stateCheck.setEnabled(false);

    }
    @Override
    public int getItemCount() {
        return subItemsAdapter.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView description;
        private CheckBox stateCheck;
        private RelativeLayout relativeLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            //привязка элементов к xml файлу
            description = (TextView) itemView.findViewById(R.id.subItemDesc);
            stateCheck = (CheckBox) itemView.findViewById(R.id.checkBox);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.subTaskItem);
        }
    }
}