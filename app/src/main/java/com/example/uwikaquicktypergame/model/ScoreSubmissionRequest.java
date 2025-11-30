package com.example.uwikaquicktypergame.model;

import com.google.gson.annotations.SerializedName;

public class ScoreSubmissionRequest {
    @SerializedName("stage_id")
    private String stageId;

    @SerializedName("total_time_ms")
    private long totalTimeMs;

    @SerializedName("total_errors")
    private int totalErrors;

    public ScoreSubmissionRequest(String stageId, long totalTimeMs, int totalErrors) {
        this.stageId = stageId;
        this.totalTimeMs = totalTimeMs;
        this.totalErrors = totalErrors;
    }
}
