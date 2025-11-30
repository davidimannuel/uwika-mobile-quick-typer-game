package com.example.uwikaquicktypergame.model;

import java.io.Serializable;

// Pastikan kelas ini mengimplementasikan Serializable agar bisa dikirim via Intent
public class GameResult implements Serializable {
    private final String stageId;
    private final int finalScore;
    private final long totalTimeMs;
    private final int totalErrors;

    public GameResult(String stageId, int finalScore, long totalTimeMs, int totalErrors) {
        this.stageId = stageId;
        this.finalScore = finalScore;
        this.totalTimeMs = totalTimeMs;
        this.totalErrors = totalErrors;
    }

    // Getters
    public String getStageId() { return stageId; }
    public int getFinalScore() { return finalScore; }
    public long getTotalTimeMs() { return totalTimeMs; }
    public int getTotalErrors() { return totalErrors; }
}
