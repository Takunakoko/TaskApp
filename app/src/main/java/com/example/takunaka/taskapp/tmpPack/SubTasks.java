package com.example.takunaka.taskapp.tmpPack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by takunaka on 01.08.17.
 */

public class SubTasks {
    private static List<SubItem> subTasks= new ArrayList<>();

    static {
        for(int i = 0; i < 6; i++){
            SubItem si = new SubItem("Дело " + 1);
            subTasks.add(si);
        }
    }

    public static List<SubItem> getSubTasks() {
        return subTasks;
    }

    public static void setSubTasks(List<SubItem> subTasks) {
        SubTasks.subTasks = subTasks;
    }
}
