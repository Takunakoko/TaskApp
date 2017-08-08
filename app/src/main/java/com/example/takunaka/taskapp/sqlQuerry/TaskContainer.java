package com.example.takunaka.taskapp.sqlQuerry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by takunaka on 07.08.17.
 */

public class TaskContainer {

    private static int selectedTaskID;
    private static String selectedDesription;
    private static String selectedDate;
    private static String selectedState;
    private static Task selectedTask;


    public static int getSelectedTaskID() {
        return selectedTaskID;
    }

    public static void setSelectedTaskID(int selectedTaskID) {
        TaskContainer.selectedTaskID = selectedTaskID;
    }

    public static String getSelectedDesription() {
        return selectedDesription;
    }

    public static void setSelectedDesription(String selectedDesription) {
        TaskContainer.selectedDesription = selectedDesription;
    }

    public static String getSelectedDate() {
        return selectedDate;
    }

    public static void setSelectedDate(String selectedDate) {
        TaskContainer.selectedDate = selectedDate;
    }

    public static String getSelectedState() {
        return selectedState;
    }

    public static void setSelectedState(String selectedState) {
        TaskContainer.selectedState = selectedState;
    }

    public static Task getSelectedTask() {
        return selectedTask;
    }

    public static void setSelectedTask(Task selectedTask) {
        TaskContainer.selectedTask = selectedTask;
    }
}
