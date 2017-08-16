package com.example.takunaka.taskapp.sqlQuerry;

/**
 * Created by takunaka on 07.08.17.
 */

//хранение выбранного таска
public class TaskContainer {

    private static Task selectedTask;

    public static Task getSelectedTask() {
        return selectedTask;
    }

    public static void setSelectedTask(Task selectedTask) {
        TaskContainer.selectedTask = selectedTask;
    }
}
