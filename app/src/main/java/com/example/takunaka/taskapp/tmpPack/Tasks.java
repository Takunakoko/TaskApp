package com.example.takunaka.taskapp.tmpPack;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by takunaka on 01.08.17.
 */

public class Tasks {

    private static List<ListItem> listItems = new ArrayList<ListItem>();

    public static List<ListItem> getListItems() {
        return listItems;
    }

    //Удалить при подключении SQL
    static {
        for(int i = 0; i < 10; i++){
            ListItem li = new ListItem("Random name"+i, "Random date" +i, "StartState");
            listItems.add(li);
        }

    }
}
