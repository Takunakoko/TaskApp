package com.example.takunaka.taskapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.takunaka.taskapp.Configurator;
import com.example.takunaka.taskapp.R;
import com.example.takunaka.taskapp.sqlQuerry.SubTask;

import java.util.List;

/**
 * Created by takunaka on 07.08.17.
 */

public class RecyclerViewCreateSubItemAdapt extends RecyclerView.Adapter<RecyclerViewCreateSubItemAdapt.ViewHolder> {

    private Configurator config = Configurator.getInstance();
    private List<SubTask> subItemsAdapter;
    private Context context;


    public RecyclerViewCreateSubItemAdapt(List<SubTask> subItems, Context context) {
        this.subItemsAdapter = subItems;
        this.context = context;
    }

    @Override
    public RecyclerViewCreateSubItemAdapt.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_item, parent, false);
        return new RecyclerViewCreateSubItemAdapt.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerViewCreateSubItemAdapt.ViewHolder holder, int position) {
        SubTask si = subItemsAdapter.get(position);
        //установка отображения элементов на странице
        holder.description.setText(si.getDescription());

    }

    @Override
    public int getItemCount() {
        return subItemsAdapter.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView description;

        public ViewHolder(View itemView) {
            super(itemView);
            //привязка элементов к xml файлу
            description = (TextView) itemView.findViewById(R.id.subItemDesc);


            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

        }
    }
}
