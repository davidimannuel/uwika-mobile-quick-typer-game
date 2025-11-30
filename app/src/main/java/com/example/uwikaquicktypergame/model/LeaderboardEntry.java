package com.example.uwikaquicktypergame.model;

import com.google.gson.annotations.SerializedName;

public class LeaderboardEntry {

    @SerializedName("username")
    private String username;

    @SerializedName("final_score")
    private int finalScore;

    @SerializedName("total_time_ms")
    private long totalTimeMs;

    // Getters
    public String getUsername() {
        return username;
    }

    public int getFinalScore() {
        return finalScore;
    }

    public long getTotalTimeMs() {
        return totalTimeMs;
    }
}
    