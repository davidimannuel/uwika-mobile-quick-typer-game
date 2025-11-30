package com.example.uwikaquicktypergame.network;

import com.example.uwikaquicktypergame.model.AuthRequest;
import com.example.uwikaquicktypergame.model.LoginResponse;
import com.example.uwikaquicktypergame.model.ProfileResponse;
import com.example.uwikaquicktypergame.model.ScoreSubmissionRequest;
import com.example.uwikaquicktypergame.model.ScoreSubmissionResponse;
import com.example.uwikaquicktypergame.model.Stage;
import com.example.uwikaquicktypergame.model.StageDetail;
import com.example.uwikaquicktypergame.model.LeaderboardEntry;
import retrofit2.http.Query;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @POST("api/auth/register")
    Call<LoginResponse> register(@Body AuthRequest authRequest);

    @POST("api/auth/login")
    Call<LoginResponse> login(@Body AuthRequest authRequest);

    @GET("api/auth/profile")
    Call<ProfileResponse> getProfile(@Header("Authorization") String token);

    @GET("api/stages")
    Call<List<Stage>> getActiveStages(@Header("Authorization") String token);

    @GET("api/stage/{id}")
    Call<StageDetail> getStageDetails(@Header("Authorization") String token, @Path("id") String stageId);

    @POST("api/score/submit")
    Call<ScoreSubmissionResponse> submitScore(@Header("Authorization") String token, @Body ScoreSubmissionRequest request);

    @GET("api/leaderboard")
    Call<List<LeaderboardEntry>> getLeaderboard(
            @Header("Authorization") String token,
            @Query("stage_id") String stageId,
            @Query("limit") int limit
    );
}
    