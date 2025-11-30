package com.example.uwikaquicktypergame.model;

import com.google.gson.annotations.SerializedName;

public class Stage {
    @SerializedName("id") // Sesuaikan dengan response API Anda ("id" bukan "stage_id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("difficulty")
    private String difficulty;

    @SerializedName("is_active")
    private boolean isActive;

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDifficulty() { return difficulty; }
}
