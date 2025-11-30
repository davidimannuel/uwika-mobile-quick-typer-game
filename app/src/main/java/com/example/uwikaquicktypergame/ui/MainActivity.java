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

import com.example.uwikaquicktypergame.R;
import com.example.uwikaquicktypergame.model.ProfileResponse;
import com.example.uwikaquicktypergame.network.ApiClient;
import com.example.uwikaquicktypergame.network.ApiService;
import com.example.uwikaquicktypergame.ui.auth.LoginActivity;
import com.example.uwikaquicktypergame.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.recyclerview.widget.RecyclerView;

import com.example.uwikaquicktypergame.model.ProfileResponse;
import com.example.uwikaquicktypergame.model.Stage;
import com.example.uwikaquicktypergame.ui.adapter.StageAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView textViewWelcome;
    private Button buttonLogout;
    private RecyclerView recyclerViewStages;
    private ProgressBar progressBar;

    private ApiService apiService;
    private SessionManager sessionManager;
    private StageAdapter stageAdapter;
    private List<Stage> stageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi
        textViewWelcome = findViewById(R.id.textViewWelcome);
        buttonLogout = findViewById(R.id.buttonLogout);
        recyclerViewStages = findViewById(R.id.recyclerViewStages);
        progressBar = findViewById(R.id.progressBar);

        sessionManager = new SessionManager(this);
        apiService = ApiClient.getClient(this).create(ApiService.class);

        setupRecyclerView();

        buttonLogout.setOnClickListener(v -> logoutUser());

        fetchUserProfile(); // Untuk mendapatkan nama user
        fetchActiveStages(); // Untuk mengisi RecyclerView
    }

    private void setupRecyclerView() {
        stageAdapter = new StageAdapter(stageList, stage -> {
            // Ketika sebuah stage di-klik
            Toast.makeText(this, "Loading " + stage.getName(), Toast.LENGTH_SHORT).show();
            // Pindah ke GameActivity dengan membawa stage ID
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            intent.putExtra("STAGE_ID", stage.getId());
            startActivity(intent);
        });
        recyclerViewStages.setAdapter(stageAdapter);
    }


    private void fetchActiveStages() {
        showLoading(true);
        String token = "Bearer " + sessionManager.fetchAuthToken();

        apiService.getActiveStages(token).enqueue(new Callback<List<Stage>>() {
            @Override
            public void onResponse(@NonNull Call<List<Stage>> call, @NonNull Response<List<Stage>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    stageList.clear();
                    stageList.addAll(response.body());
                    stageAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to load stages.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Stage>> call, @NonNull Throwable t) {
                showLoading(false);
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserProfile() {
        // (Kode fetchUserProfile yang sudah Anda miliki, ubah untuk set textViewWelcome)
        String token = "Bearer " + sessionManager.fetchAuthToken();
        apiService.getProfile(token).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    textViewWelcome.setText("Welcome, " + response.body().getUsername() + "!");
                }
            }
            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                // Handle failure
            }
        });
    }

    private void logoutUser() {
        // Hapus token dari SharedPreferences
        sessionManager.clearAuthToken();

        // Arahkan kembali ke LoginActivity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        Toast.makeText(this, "Logout berhasil", Toast.LENGTH_SHORT).show();
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }
}
    