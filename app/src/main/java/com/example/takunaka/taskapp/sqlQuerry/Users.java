package com.example.takunaka.taskapp.sqlQuerry;

import android.support.annotation.NonNull;

/**
 * Created by takunaka on 04.08.17.
 */

//Класс-конструктор для создани юзера
public class Users {
    //Используется для адаптации вывода спиннера
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

    @NonNull
    @Override
    public String toString() {
        return userName + " " + userSurName;
    }
}
