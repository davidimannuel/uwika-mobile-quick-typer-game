package com.example.uwikaquicktypergame.model;

import com.google.gson.annotations.SerializedName;

public class ScoreSubmissionResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("final_score")
    private int finalScore;

    // Getters
    public String getStatus() { return status; }
    public int getFinalScore() { return finalScore; }
}
