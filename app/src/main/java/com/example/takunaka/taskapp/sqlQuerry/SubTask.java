package com.example.takunaka.taskapp.sqlQuerry;

/**
 * Created by takunaka on 07.08.17.
 */

public class SubTask {
    private int id;
    private int nameID;
    private int taskID;
    private String description;
    private String state;

    public SubTask(int id, int nameID, int taskID, String description, String state) {
        this.id = id;
        this.nameID = nameID;
        this.taskID = taskID;
        this.description = description;
        this.state = state;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNameID() {
        return nameID;
    }

    public void setNameID(int nameID) {
        this.nameID = nameID;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
