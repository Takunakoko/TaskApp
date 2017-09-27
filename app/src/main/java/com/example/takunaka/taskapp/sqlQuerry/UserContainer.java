package com.example.takunaka.taskapp.sqlQuerry;


import android.support.annotation.NonNull;

/**
 * Created by takunaka on 04.08.17.
 */

//класс контейнер для хранения выбранного юзера
public class UserContainer {
    //Айдишник выбранного юзера
    private static int selectedID = 0;
    //Имя выбранного юзера
    private static String selectedName;
    //Фамилия выбранного юзера
    private static String selectedSurName;

    public static int getSelectedID() {
        return selectedID;
    }

    public static void setSelectedID(int selectedID) {
        UserContainer.selectedID = selectedID;
    }

    public static String getSelectedName() {
        return selectedName;
    }

    public static void setSelectedName(String selectedName) {
        UserContainer.selectedName = selectedName;
    }

    public static String getSelectedSurName() {
        return selectedSurName;
    }

    public static void setSelectedSurName(String selectedSurName) {
        UserContainer.selectedSurName = selectedSurName;
    }

    @NonNull
    public static String getFullName() {
        return getSelectedSurName() + " " + getSelectedName();
    }
}
