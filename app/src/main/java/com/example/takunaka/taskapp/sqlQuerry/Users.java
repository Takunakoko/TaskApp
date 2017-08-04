package com.example.takunaka.taskapp.sqlQuerry;

/**
 * Created by takunaka on 04.08.17.
 */

public class Users {

    private int userID;
    private String userName;

    public Users(int iD, String name) {
        this.userID = iD;
        this.userName = name;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int ID) {
        userID = ID;
    }

    public String getName() {
        return userName;
    }

    public void setName(String name) {
        userName = name;
    }
}
