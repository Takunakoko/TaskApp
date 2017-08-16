package com.example.takunaka.taskapp.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.takunaka.taskapp.Configurator;
import com.example.takunaka.taskapp.MainActivity;
import com.example.takunaka.taskapp.R;
import com.example.takunaka.taskapp.fragments.MainFragment;
import com.example.takunaka.taskapp.fragments.ShowTaskFragment;
import com.example.takunaka.taskapp.sql.DBHelper;
import com.example.takunaka.taskapp.sqlQuerry.SubTask;
import com.example.takunaka.taskapp.sqlQuerry.TaskContainer;

import java.util.List;


//класс адаптера отображения recyclerView
public class RecyclerViewSubItemAdapter extends RecyclerView.Adapter<RecyclerViewSubItemAdapter.ViewHolder> {

    private List<SubTask> subItemsAdapter;
    private Context context;
    private DBHelper dbHelper;
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
        dbHelper = new DBHelper(context);
        //установка отображения элементов на странице
        holder.description.setText(subTask.getDescription());
        //проверка булевой статуса
        if(isClosed){
            //если булевая true - закрыть все дела
            for (SubTask subTask: subItemsAdapter){
                dbHelper.updateState(subTask.getId(), subTask.getTaskID(), subTask.getNameID());
            }
        }
        //Изменение отображения элементов в соответствии со статусом
        if(subTask.getState().equals("В работе")){
            //если в работе - ставим цвет
            holder.relativeLayout.setBackgroundColor(Color.parseColor("#424242"));
        } else {
            //если закрыта - ставим другой цвет текста и бэкграунда
            holder.relativeLayout.setBackgroundColor(Color.parseColor("#282828"));
            holder.description.setTextColor(Color.parseColor("#817BB4"));
            //ставим размер шрифта
            holder.description.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            //ставим чекбокс не доступным и отмеченным
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

        //метод обработки чекбокса статуса
        @Override
        public void onClick(View v) {
            //получаем сабтаск на основании нажатой позиции
            SubTask si = subItemsAdapter.get(getAdapterPosition());
            //ставим его цвет, размер и возможность изменения
            relativeLayout.setBackgroundColor(Color.parseColor("#282828"));
            description.setTextColor(Color.parseColor("#817BB4"));
            description.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            stateCheck.setEnabled(false);
            //обновляем в базе данных данные о том что чекбокс закрыт
            dbHelper.updateState(si.getId(), si.getTaskID(), si.getNameID());
            //проверяем если все дела в этой задаче закрыты
            if(checkTasks(TaskContainer.getSelectedTask().getTaskID())){
                //предлагаем закрыть задачу
                showCloseDialog(TaskContainer.getSelectedTask().getTaskID());
            }


        }
        //проверка всех дел этой задачи
        public boolean checkTasks(int id){
            boolean needClose = true;
            List<SubTask> list = dbHelper.getAllSubTasks(id);
            for (SubTask subTask : list){
                if(subTask.getState().equals("В работе")){
                    needClose = false;
                }
                //возвращает правду если больше нет ни одного дела. иначе - ложь
            } return needClose;
        }

        //показывает диалог пользователю о том что больше дел нет
        public void showCloseDialog(final int id){
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.Theme_AppCompat_Dialog));
            builder.setTitle("Все дела в задаче закрыты! Закрыть задачу?")
                    .setPositiveButton("Закрыть", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //если пользователь соглашается - закрываем задачу
                            //открываем БД
                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                            ContentValues cv = new ContentValues();
                            cv.put(DBHelper.KEY_STATE, "Закрыта");
                            db.update(DBHelper.TABLE_TASKS, cv, DBHelper.KEY_ID + " = " + id, null);
                            //вносим изменения в БД
                            MainActivity activity = (MainActivity) context;
                            db.close();
                            dbHelper.close();
                            //возвращаемся обратно на главную страницу
                            activity.getSupportFragmentManager().popBackStack();
                            activity.getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container, new MainFragment())
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


}