package com.example.takunaka.taskapp.sqlQuerry;

/**
 * Created by takunaka on 07.08.17.
 */

//класс-контруктор для создания таска и получения его полей
public class Task {
    private int taskID;
    private int nameID;
    private String desription;
    private String date;
    private String state;
    private int type;

    public Task(int taskID, int nameID, String desription, String date, String state) {
        this.taskID = taskID;
        this.nameID = nameID;
        this.desription = desription;
        this.date = date;
        this.state = state;
    }

    public int getTaskID() {
        return taskID;
    }

    public String getDesription() {
        return desription;
    }

    public String getDate() {
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
