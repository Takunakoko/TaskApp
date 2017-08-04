package com.example.takunaka.taskapp.sqlQuerry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by takunaka on 04.08.17.
 */

public class UsersContainer {
    private static List<Users> usersList = new ArrayList<>();

    public static List<Users> getUsersList() {
        return usersList;
    }

    public static void setUsersList(List<Users> usersList) {
        UsersContainer.usersList = usersList;
    }

    public static void addNewUser(int ID, String name){
        usersList.add(new Users(ID, name));
    }
}
