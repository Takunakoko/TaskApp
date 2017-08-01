package com.example.takunaka.taskapp.tmpPack;


public class ListItem {

    private String name;
    private String date;
    private String state;

    public ListItem(String name, String date, String state) {
        this.name = name;
        this.date = date;
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}