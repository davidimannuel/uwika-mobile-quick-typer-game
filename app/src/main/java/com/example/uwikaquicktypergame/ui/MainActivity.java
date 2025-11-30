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

public class MainActivity extends AppCompatActivity {

    private TextView textViewUsername, textViewUserId, textViewRole;
    private Button buttonLogout;
    private ProgressBar progressBar;

    private ApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi Views
        textViewUsername = findViewById(R.id.textViewUsername);
        textViewUserId = findViewById(R.id.textViewUserId);
        textViewRole = findViewById(R.id.textViewRole);
        buttonLogout = findViewById(R.id.buttonLogout);
        progressBar = findViewById(R.id.progressBar);

        // Inisialisasi network dan session
        sessionManager = new SessionManager(this);
        apiService = ApiClient.getClient(this).create(ApiService.class);

        // Fetch profile data
        fetchUserProfile();

        // Setup logout button
        buttonLogout.setOnClickListener(v -> logoutUser());
    }

    private void fetchUserProfile() {
        showLoading(true);

        String token = sessionManager.fetchAuthToken();
        if (token == null) {
            // Ini seharusnya tidak terjadi jika alur login benar, tapi sebagai pengaman
            // Interceptor di ApiClient akan menangani redirect
            return;
        }

        // Menambahkan "Bearer " di depan token sesuai kontrak API
        Call<ProfileResponse> call = apiService.getProfile("Bearer " + token);

        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    ProfileResponse profile = response.body();
                    // Tampilkan data ke UI
                    textViewUsername.setText(profile.getUsername());
                    textViewUserId.setText(profile.getUserId());
                    textViewRole.setText(profile.getRole());
                } else {
                    // Jika gagal, interceptor di ApiClient akan auto-logout jika error 401
                    // Jika error lain, tampilkan pesan
                    if (response.code() != 401) {
                        Toast.makeText(MainActivity.this, "Gagal memuat profil.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                showLoading(false);
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
    