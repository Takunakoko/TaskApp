package com.example.takunaka.taskapp;

import com.example.takunaka.taskapp.fragments.CreateTaskFragment;
import com.example.takunaka.taskapp.fragments.MainFragment;
import com.example.takunaka.taskapp.fragments.ShowTaskFragment;
import com.example.takunaka.taskapp.fragments.UpdateFragment;

/**
 * Created by takunaka on 02.08.17.
 */

public class Configurator {


    private static Configurator instance;

    private Configurator(){

    }

    public static Configurator getInstance(){
        if(instance == null){
            instance = new Configurator();
        }return instance;
    }

    private int adapterPosition;

    private boolean isClosed = false;

    private boolean onlyOpened = true;

    public int getAdapterPosition() {
        return adapterPosition;
    }

    public void setAdapterPosition(int adapterPosition) {
        this.adapterPosition = adapterPosition;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public boolean isOnlyOpened() {
        return onlyOpened;
    }

    public void setOnlyOpened(boolean onlyOpened) {
        this.onlyOpened = onlyOpened;
    }

}
