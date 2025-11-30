package com.example.uwikaquicktypergame.model;

public class AuthRequest {
    private String username;
    private String password;

    public AuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getter bisa ditambahkan jika perlu, tapi untuk request body, constructor sudah cukup.
}
    