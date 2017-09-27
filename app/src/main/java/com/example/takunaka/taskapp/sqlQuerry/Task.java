package com.example.takunaka.taskapp.sqlQuerry;

/**
 * Created by takunaka on 07.08.17.
 */

//класс-контруктор для создания тасков и получения его полей
public class Task {
    private int taskID;
    private String description;
    private int date;
    private String state;
    private int type;

    public Task(int taskID, String description, int date, String state) {
        this.taskID = taskID;
        this.description = description;
        this.date = date;
        this.state = state;
    }

    public int getTaskID() {
        return taskID;
    }

    public String getDescription() {
        return description;
    }

    public Integer getDate() {
        return date;
    }

    public String getState() {
        return state;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
