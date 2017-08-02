package com.example.takunaka.taskapp.adapters;

import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.takunaka.taskapp.Configurator;
import com.example.takunaka.taskapp.MainActivity;
import com.example.takunaka.taskapp.R;
import com.example.takunaka.taskapp.fragments.ShowTaskFragment;
import com.example.takunaka.taskapp.tmpPack.ListItem;

import java.util.List;


//класс адаптера отображения recyclerView
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Configurator config = Configurator.getInstance();
    private ShowTaskFragment stFragment;
    private List<ListItem> listItemsAdapter;
    private Context context;


    public RecyclerViewAdapter(List<ListItem> listItems, Context context) {
        this.listItemsAdapter = listItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ListItem li = listItemsAdapter.get(position);
        //установка отображения элементов на странице
        holder.name.setText(li.getName());
        holder.state.setText(li.getState());


    }

    @Override
    public int getItemCount() {
        return listItemsAdapter.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView name;
        private TextView state;

        public ViewHolder(View itemView) {
            super(itemView);
            //привязка элементов к xml файлу
            name = (TextView) itemView.findViewById(R.id.Name);
            state = (TextView) itemView.findViewById(R.id.State);
            itemView.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            config.setAdapterPosition(getAdapterPosition());
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            stFragment = new ShowTaskFragment();
            ft.replace(R.id.container, stFragment);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

}