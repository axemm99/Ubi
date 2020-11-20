package com.example.ubi_interfaces.classes;

import android.util.Log;

public class Rank {

    private String name;
    private int goal;

    public Rank() {
        Log.d("Rankssssssssss!!!UP", "RANKKKKK");

    }
    public Rank(String name, int goal) {
        Log.d("Rankssssssssss!!!DOWn", "RANKKKKK");

        this.name = name;
        this.goal = goal;
    }

    public String getName() {
        return this.name;
    }

    public int getGoal() {
        return this.goal;
    }
}
