package com.example.takunaka.taskapp;

import com.example.takunaka.taskapp.sqlQuerry.Task;

import java.util.ArrayList;

/**
 * Created by takunaka on 02.08.17.
 */

public class Configurator {


    private static Configurator instance;

    private int adapterPosition;
    private boolean isClosed = false;
    private boolean onlyOpened = true;
    private boolean FilterActive = false;
    private String filterDateFrom;
    private String filterDateTo;
    private ArrayList<Task> tasks;

    private Configurator(){

    }

    public static Configurator getInstance(){
        if(instance == null){
            instance = new Configurator();
        }return instance;
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

    public String getFilterDateFrom() {
        return filterDateFrom;
    }

    public void setFilterDateFrom(String filterDateFrom) {
        this.filterDateFrom = filterDateFrom;
    }

    public String getFilterDateTo() {
        return filterDateTo;
    }

    public void setFilterDateTo(String filterDateTo) {
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
