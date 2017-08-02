package com.example.takunaka.taskapp.adapters;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.takunaka.taskapp.Configurator;
import com.example.takunaka.taskapp.R;
import com.example.takunaka.taskapp.fragments.ShowTaskFragment;
import com.example.takunaka.taskapp.tmpPack.ListItem;
import com.example.takunaka.taskapp.tmpPack.SubItem;
import com.example.takunaka.taskapp.tmpPack.SubTasks;

import java.util.List;


//класс адаптера отображения recyclerView
public class RecyclerViewSubItemAdapter extends RecyclerView.Adapter<RecyclerViewSubItemAdapter.ViewHolder> {

    private Configurator config = Configurator.getInstance();
    private List<SubItem> subItemsAdapter;
    private Context context;


    public RecyclerViewSubItemAdapter(List<SubItem> subItems, Context context) {
        this.subItemsAdapter = subItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SubItem si = subItemsAdapter.get(position);
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
            //// TODO: 02.08.17 dialog window to change/close/re-description
        }
    }

}