package com.example.takunaka.taskapp;

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

    public int getAdapterPosition() {
        return adapterPosition;
    }

    public void setAdapterPosition(int adapterPosition) {
        this.adapterPosition = adapterPosition;
    }
}
