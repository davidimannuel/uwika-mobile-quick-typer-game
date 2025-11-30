package com.example.uwikaquicktypergame.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uwikaquicktypergame.R;
import com.example.uwikaquicktypergame.model.GameResult;
import com.example.uwikaquicktypergame.model.LeaderboardEntry;
import com.example.uwikaquicktypergame.network.ApiClient;
import com.example.uwikaquicktypergame.network.ApiService;
import com.example.uwikaquicktypergame.ui.adapter.LeaderboardAdapter;
import com.example.uwikaquicktypergame.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScoreActivity extends AppCompatActivity {

    private TextView textViewFinalScore, textViewYourScoreLabel, textViewResultTitle;
    private RecyclerView recyclerViewLeaderboard;
    private Button buttonBackToMenu;
    private ProgressBar progressBar;

    private ApiService apiService;
    private SessionManager sessionManager;
    private LeaderboardAdapter adapter;
    private List<LeaderboardEntry> leaderboardList = new ArrayList<>();

    private GameResult gameResult;

    private String stageId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        // Ambil data dari Intent
        gameResult = (GameResult) getIntent().getSerializableExtra("GAME_RESULT");
        stageId = getIntent().getStringExtra("STAGE_ID_ONLY");

        initViews(); // 1. Inisialisasi semua view
        setupRecyclerView(); // 2. Setup RecyclerView

        // ★★★ PINDAHKAN INISIALISASI KE SINI (SEBELUM DIGUNAKAN) ★★★
        apiService = ApiClient.getClient(this).create(ApiService.class);
        sessionManager = new SessionManager(this);

        // 3. Sekarang baru jalankan logika yang membutuhkan sessionManager dan apiService
        if (gameResult != null) {
            // MODE 1: Ada hasil game, tampilkan semuanya
            textViewFinalScore.setText(String.valueOf(gameResult.getFinalScore()));
            fetchLeaderboard(gameResult.getStageId()); // Aman untuk dipanggil
        } else if (stageId != null) {
            // MODE 2: Hanya lihat leaderboard
            textViewResultTitle.setVisibility(View.GONE);
            textViewYourScoreLabel.setVisibility(View.GONE);
            textViewFinalScore.setVisibility(View.GONE);
            fetchLeaderboard(stageId); // Aman untuk dipanggil
        } else {
            // Jika tidak ada data sama sekali, tampilkan error dan kembali
            Toast.makeText(this, "Failed to get stage data.", Toast.LENGTH_SHORT).show();
            finish();
            return; // Hentikan eksekusi lebih lanjut
        }

        buttonBackToMenu.setOnClickListener(v -> {
            finish(); // Cukup finish() karena akan kembali ke MainActivity
        });
    }

    private void initViews() {
        textViewResultTitle = findViewById(R.id.textViewResultTitle);
        textViewYourScoreLabel = findViewById(R.id.textViewYourScoreLabel);
        textViewFinalScore = findViewById(R.id.textViewFinalScore);
        recyclerViewLeaderboard = findViewById(R.id.recyclerViewLeaderboard);
        buttonBackToMenu = findViewById(R.id.buttonBackToMenu);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupRecyclerView() {
        adapter = new LeaderboardAdapter(leaderboardList);
        recyclerViewLeaderboard.setAdapter(adapter);
    }

    private void fetchLeaderboard(String stageIdToFetch) {
        showLoading(true);
        String token = "Bearer " + sessionManager.fetchAuthToken();
        int limit = 10;

        apiService.getLeaderboard(token, stageIdToFetch, limit).enqueue(new Callback<List<LeaderboardEntry>>() {
            // ... (sisa kode fetchLeaderboard tidak perlu diubah)
            @Override
            public void onResponse(@NonNull Call<List<LeaderboardEntry>> call, @NonNull Response<List<LeaderboardEntry>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    leaderboardList.clear();
                    leaderboardList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ScoreActivity.this, "Failed to load leaderboard.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<LeaderboardEntry>> call, @NonNull Throwable t) {
                showLoading(false);
                Toast.makeText(ScoreActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }
}
