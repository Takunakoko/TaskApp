package com.example.takunaka.taskapp.sqlQuerry;



/**
 * Created by takunaka on 04.08.17.
 */

public class UserContainer {
    private static int selectedID;
    private static String selectedName;
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

    public static String getFullName(){
        return getSelectedSurName() + " " + getSelectedName();
    }
}
