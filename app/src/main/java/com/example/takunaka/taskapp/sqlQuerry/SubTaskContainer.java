package com.example.takunaka.taskapp.sqlQuerry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by takunaka on 07.08.17.
 */

public class SubTaskContainer {
    private List<SubTask> listSubTasks= new ArrayList<>();

    public List<SubTask> getListSubTasks() {
        return listSubTasks;
    }

    public void setListSubTasks(List<SubTask> listSubTasks) {
        this.listSubTasks = listSubTasks;
    }

    private void addToList(SubTask st){
        listSubTasks.add(st);
    }
}
