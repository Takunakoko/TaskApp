package com.example.takunaka.taskapp.sqlQuerry;

/**
 * Created by takunaka on 04.08.17.
 */

public class Users {

    private int userID;
    private String userName;
    private String userSurName;

    public Users(int iD, String name, String userSurName) {
        this.userID = iD;
        this.userName = name;
        this.userSurName = userSurName;
    }

    public int getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }
    public String getUserSurName() {
        return userSurName;
    }

    @Override
    public String toString () {
        return userName + " " + userSurName;
    }
}
