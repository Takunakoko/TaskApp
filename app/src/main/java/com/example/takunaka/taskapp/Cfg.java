package com.example.takunaka.taskapp;

import com.example.takunaka.taskapp.sqlQuerry.Task;

import java.util.ArrayList;

/**
 * Created by takunaka on 02.08.17.
 */
//класс конфигуратор - синглтон
public class Cfg {

    private static Cfg instance;
    //позиция адаптера
    private int adapterPosition;
    //флаг для дел. если истина - закрывает все дела
    private boolean isClosed = false;
    //флаг для свитчера на главной странице.
    private boolean onlyOpened = true;
    //флаг для фильтра
    private boolean FilterActive = false;
    //даты фильтрации
    private long filterDateFrom;
    private long filterDateTo;
    //временное хранение тасков выбранных в фильтре
    private ArrayList<Task> tasks;

    private Cfg() {

    }

    public static Cfg getInstance() {
        if (instance == null) {
            instance = new Cfg();
        }
        return instance;
    }

    public int getAdapterPosition() {
        return adapterPosition;
    }

    public void setAdapterPosition(int adapterPosition) {
        this.adapterPosition = adapterPosition;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public boolean isOnlyOpened() {
        return onlyOpened;
    }

    public void setOnlyOpened(boolean onlyOpened) {
        this.onlyOpened = onlyOpened;
    }

    public long getFilterDateFrom() {
        return filterDateFrom;
    }

    public void setFilterDateFrom(long filterDateFrom) {
        this.filterDateFrom = filterDateFrom;
    }

    public long getFilterDateTo() {
        return filterDateTo;
    }

    public void setFilterDateTo(long filterDateTo) {
        this.filterDateTo = filterDateTo;
    }

    public boolean isFilterActive() {
        return FilterActive;
    }

    public void setFilterActive(boolean filterActive) {
        FilterActive = filterActive;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

}
