package com.example.uwikaquicktypergame.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("user_id")
    private String userId;

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("token_expires_at")
    private String tokenExpiresAt;

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenExpiresAt() {
        return tokenExpiresAt;
    }
}
    