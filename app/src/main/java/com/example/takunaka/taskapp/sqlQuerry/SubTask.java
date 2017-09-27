package com.example.takunaka.taskapp.sqlQuerry;

/**
 * Created by takunaka on 07.08.17.
 */

//класс конструктор для создания сабтаска(Дела)
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

    public SubTask(int nameID, String description, String state) {
        this.nameID = nameID;
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

    public int getTaskID() {
        return taskID;
    }

    public String getDescription() {
        return description;
    }

    public String getState() {
        return state;
    }

}
