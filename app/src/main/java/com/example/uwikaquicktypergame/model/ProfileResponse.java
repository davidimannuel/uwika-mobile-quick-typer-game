package com.example.uwikaquicktypergame.model;

import com.google.gson.annotations.SerializedName;

public class ProfileResponse {
    @SerializedName("user_id")
    private String userId;

    @SerializedName("username")
    private String username;

    @SerializedName("role")
    private String role;

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}
    