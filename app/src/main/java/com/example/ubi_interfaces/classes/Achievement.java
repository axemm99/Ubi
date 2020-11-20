package com.example.ubi_interfaces.classes;

import android.util.Log;

import java.util.List;

public class Achievement {

    /* Os ids vão ser feitos pela app e não automaticamente pela db */
    private String category;
    private int achivId;
    private List<Rank> ranks;

    public Achievement() {
        /* Calcular o id */
        Log.d("Achievement!!!UP", this.achivId + "/" + this.category + "/" + String.valueOf(this.ranks));

    }


    public Achievement(String category, int achivId, List<Rank> ranks) {
        /* Calcular o id */
        Log.d("Achievement!!!", this.achivId + "/" + this.category + "/" + String.valueOf(this.ranks));

        this.achivId = achivId;
        this.category = category;
        this.ranks = ranks;
    }

    public String getCategory() {
        return this.category;
    }

    public int getAchivId() {
        return this.achivId;
    }

    public List<Rank> getRanks() {
        return this.ranks;
    }
}

