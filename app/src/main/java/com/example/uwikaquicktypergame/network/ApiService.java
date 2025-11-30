package com.example.uwikaquicktypergame.network;

import com.example.uwikaquicktypergame.model.AuthRequest;
import com.example.uwikaquicktypergame.model.LoginResponse;
import com.example.uwikaquicktypergame.model.ProfileResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {

    @POST("api/auth/register")
    Call<LoginResponse> register(@Body AuthRequest authRequest);

    @POST("api/auth/login")
    Call<LoginResponse> login(@Body AuthRequest authRequest);

    @GET("api/auth/profile")
    Call<ProfileResponse> getProfile(@Header("Authorization") String token);
}
    