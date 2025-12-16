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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.recyclerview.widget.RecyclerView;

import com.example.uwikaquicktypergame.model.ProfileResponse;
import com.example.uwikaquicktypergame.model.Stage;
import com.example.uwikaquicktypergame.ui.adapter.StageAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton fabTutorial;
    private TextView textViewWelcome;
    private Button buttonLogout;
    private RecyclerView recyclerViewStages;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ApiService apiService;
    private SessionManager sessionManager;
    private StageAdapter stageAdapter;
    private List<Stage> stageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        // Inisialisasi
        textViewWelcome = findViewById(R.id.textViewWelcome);
        buttonLogout = findViewById(R.id.buttonLogout);
        recyclerViewStages = findViewById(R.id.recyclerViewStages);
        progressBar = findViewById(R.id.progressBar);
        fabTutorial = findViewById(R.id.fabTutorial);
        fabTutorial.setOnClickListener(view -> {
            showTutorialDialog();
        });

        sessionManager = new SessionManager(this);
        apiService = ApiClient.getClient(this).create(ApiService.class);

        setupRecyclerView();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchActiveStages();
        });

        buttonLogout.setOnClickListener(v -> logoutUser());

        fetchUserProfile(); // Untuk mendapatkan nama user
        fetchActiveStages(); // Untuk mengisi RecyclerView
    }

    private void initViews() {
        recyclerViewStages = findViewById(R.id.recyclerViewStages);
        progressBar = findViewById(R.id.progressBar);
        fabTutorial = findViewById(R.id.fabTutorial);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
    }

    private void showTutorialDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("How to Play");

        String message = "Welcome to Quick Typer Game!\n\n" +
                "1. Choose a stage from the list.\n" +
                "2. You can either 'Start Game' or 'View Leaderboard'.\n" +
                "3. When the game starts, type the given phrase as fast and accurately as you can.\n" +
                "4. Your time and errors will be recorded.\n" +
                "5. At the end, your final score will be calculated and you can see how you rank!\n\n" +
                "Good luck and type fast!";

        builder.setMessage(message);
        builder.setPositiveButton("Got it!", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.create().show();
    }

    private void setupRecyclerView() {
        stageAdapter = new StageAdapter(stageList, stage -> {        // Saat sebuah stage di-klik, tampilkan dialog pilihan
            showStageOptionsDialog(stage);
        });
        recyclerViewStages.setAdapter(stageAdapter);
    }

    private void showStageOptionsDialog(Stage stage) {
        CharSequence[] options = new CharSequence[]{"Start Game", "View Leaderboard"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(stage.getName()); // Judul dialog adalah nama stage
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // Pilihan "Start Game" (indeks 0)
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("STAGE_ID", stage.getId());
                startActivity(intent);
            } else {
                // Pilihan "View Leaderboard" (indeks 1)
                Intent intent = new Intent(MainActivity.this, ScoreActivity.class);
                // Kirim hanya Stage ID, karena tidak ada hasil permainan
                intent.putExtra("STAGE_ID_ONLY", stage.getId());
                startActivity(intent);
            }
        });
        builder.show();
    }


    private void fetchActiveStages() {
        if (!swipeRefreshLayout.isRefreshing()) {
            showLoading(true);
        }

        String token = "Bearer " + sessionManager.fetchAuthToken();

        apiService.getActiveStages(token).enqueue(new Callback<List<Stage>>() {
            @Override
            public void onResponse(@NonNull Call<List<Stage>> call, @NonNull Response<List<Stage>> response) {
                showLoading(false);
                swipeRefreshLayout.setRefreshing(false);

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
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }
}
    