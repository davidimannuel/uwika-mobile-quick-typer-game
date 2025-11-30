package com.example.uwikaquicktypergame.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.uwikaquicktypergame.R;
import com.example.uwikaquicktypergame.model.Phrase;
import com.example.uwikaquicktypergame.model.ScoreSubmissionRequest;
import com.example.uwikaquicktypergame.model.ScoreSubmissionResponse;
import com.example.uwikaquicktypergame.model.StageDetail;
import com.example.uwikaquicktypergame.network.ApiClient;
import com.example.uwikaquicktypergame.network.ApiService;
import com.example.uwikaquicktypergame.utils.SessionManager;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameActivity extends AppCompatActivity {

    // Views
    private TextView textViewPhraseProgress, textViewTimer, textViewPhraseToType, textViewErrorCount;
    private EditText editTextUserInput;
    private ProgressBar progressBar;

    // Game Logic
    private ApiService apiService;
    private SessionManager sessionManager;
    private String stageId;
    private List<Phrase> phrases;
    private int currentPhraseIndex = 0;
    private int totalErrors = 0;
    private long startTime = 0L;
    private boolean isTimerRunning = false;

    private int currentPhraseErrors = 0;

    // Timer
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = SystemClock.uptimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int m = (int) ((millis % 1000) / 10);
            textViewTimer.setText(String.format(Locale.getDefault(), "%d.%02ds", seconds, m));
            timerHandler.postDelayed(this, 50);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Get stage ID from MainActivity
        stageId = getIntent().getStringExtra("STAGE_ID");

        // Initialize
        initViews();
        sessionManager = new SessionManager(this);
        apiService = ApiClient.getClient(this).create(ApiService.class);

        // Fetch stage details
        fetchStageDetails();

        // Setup listener for user input
        setupInputListener();
    }

    private void initViews() {
        textViewPhraseProgress = findViewById(R.id.textViewPhraseProgress);
        textViewTimer = findViewById(R.id.textViewTimer);
        textViewPhraseToType = findViewById(R.id.textViewPhraseToType);
        textViewErrorCount = findViewById(R.id.textViewErrorCount);
        editTextUserInput = findViewById(R.id.editTextUserInput);
        progressBar = findViewById(R.id.progressBar);

        // Hide game elements until data is loaded
        editTextUserInput.setVisibility(View.GONE);
    }

    private void fetchStageDetails() {
        showLoading(true);
        String token = "Bearer " + sessionManager.fetchAuthToken();

        apiService.getStageDetails(token, stageId).enqueue(new Callback<StageDetail>() {
            @Override
            public void onResponse(@NonNull Call<StageDetail> call, @NonNull Response<StageDetail> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    phrases = response.body().getPhrases();
                    if (phrases != null && !phrases.isEmpty()) {
                        startGame();
                    } else {
                        Toast.makeText(GameActivity.this, "This stage has no phrases.", Toast.LENGTH_LONG).show();
                        finish(); // Go back if stage is empty
                    }
                } else {
                    Toast.makeText(GameActivity.this, "Failed to load stage details.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<StageDetail> call, @NonNull Throwable t) {
                showLoading(false);
                Toast.makeText(GameActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void startGame() {
        editTextUserInput.setVisibility(View.VISIBLE);
        currentPhraseIndex = 0;
        totalErrors = 0;
        displayCurrentPhrase();
    }

    private void displayCurrentPhrase() {
        if (currentPhraseIndex < phrases.size()) {
            Phrase phrase = phrases.get(currentPhraseIndex);
            textViewPhraseToType.setText(phrase.getText());
            editTextUserInput.setText("");
            editTextUserInput.setTextColor(getResources().getColor(android.R.color.black));
            textViewPhraseProgress.setText(String.format(Locale.getDefault(), "Phrase: %d / %d", currentPhraseIndex + 1, phrases.size()));
            currentPhraseErrors = 0; // Reset error untuk setiap frasa baru
            textViewErrorCount.setText(String.valueOf(totalErrors)); // Tampilkan total error dari frasa sebelumnya
        }
    }

    private void setupInputListener() {
        editTextUserInput.addTextChangedListener(new TextWatcher() {
            private String previousText = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousText = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isTimerRunning && s.length() > 0) {
                    startTime = SystemClock.uptimeMillis();
                    timerHandler.postDelayed(timerRunnable, 0);
                    isTimerRunning = true;
                }

                String currentInput = s.toString();
                String targetPhrase = textViewPhraseToType.getText().toString();

                // Logika deteksi kesalahan sederhana
                if (!targetPhrase.startsWith(currentInput)) {
                    // Jika input tidak lagi cocok dengan awal frasa, ini adalah kesalahan.
                    // Cek apakah ini kesalahan baru (bukan karena menghapus karakter yang sudah salah)
                    if(currentInput.length() > previousText.length()){
                        currentPhraseErrors++;
                        totalErrors++; // Langsung update total error
                        textViewErrorCount.setText(String.valueOf(totalErrors));
                    }
                    editTextUserInput.setTextColor(getResources().getColor(R.color.error_red));
                } else {
                    editTextUserInput.setTextColor(getResources().getColor(android.R.color.black));
                }
                previousText = currentInput;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals(textViewPhraseToType.getText().toString())) {
                    goToNextPhrase();
                }
            }
        });
    }

    private void checkInputRealtime(String userInput) {
        String currentPhrase = textViewPhraseToType.getText().toString();

        // Cek jika input yang diketik cocok dengan awal dari frasa target.
        // Ini adalah cara paling sederhana dan efektif untuk validasi real-time.
        if (!currentPhrase.startsWith(userInput)) {
            // Jika input tidak cocok, warnai teks menjadi merah.
            editTextUserInput.setTextColor(getResources().getColor(R.color.error_red));
        } else {
            // Jika cocok, kembalikan warna teks ke hitam.
            editTextUserInput.setTextColor(getResources().getColor(android.R.color.black));
        }
    }

    private void checkErrors(String userInput) {
        String currentPhraseText = textViewPhraseToType.getText().toString();
        int errors = 0;
        for (int i = 0; i < userInput.length(); i++) {
            if (i >= currentPhraseText.length() || userInput.charAt(i) != currentPhraseText.charAt(i)) {
                errors++;
            }
        }
        // This is a simple error check. We only update totalErrors when a phrase is complete.
        // For a live error counter, a more complex logic is needed. We will count total errors at the end of a phrase.
    }


    private void goToNextPhrase() {
        // Kita sudah menghitung error secara real-time, jadi kita hanya perlu pindah.
        currentPhraseIndex++;
        if (currentPhraseIndex >= phrases.size()) {
            finishGame();
        } else {
            displayCurrentPhrase();
        }
    }


    private void finishGame() {
        timerHandler.removeCallbacks(timerRunnable);
        long totalTimeMs = SystemClock.uptimeMillis() - startTime;

        Toast.makeText(this, "Stage Complete!", Toast.LENGTH_SHORT).show();
        editTextUserInput.setEnabled(false);

        // Panggil submitScore, yang SEKARANG akan menangani perpindahan activity
        submitScore(totalTimeMs, totalErrors);
    }

    private void submitScore(long time, int errors) {
        showLoading(true);
        String token = "Bearer " + sessionManager.fetchAuthToken();
        ScoreSubmissionRequest request = new ScoreSubmissionRequest(stageId, time, errors);

        apiService.submitScore(token, request).enqueue(new Callback<ScoreSubmissionResponse>() {
            @Override
            public void onResponse(@NonNull Call<ScoreSubmissionResponse> call, @NonNull Response<ScoreSubmissionResponse> response) {
                showLoading(false);
                int finalScore = 0;
                if (response.isSuccessful() && response.body() != null) {
                    finalScore = response.body().getFinalScore();
                    Toast.makeText(GameActivity.this, "Score submitted successfully!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(GameActivity.this, "Failed to submit score. You can see your result.", Toast.LENGTH_SHORT).show();
                }
                // ★★★ SELALU PINDAH HALAMAN ★★★
                // Buat objek hasil untuk dikirim ke ScoreActivity
                // com.example.uwikaquicktypergame.model.GameResult result = new com.example.uwikaquicktypergame.model.GameResult(stageId, finalScore, time, errors);
                // Intent intent = new Intent(GameActivity.this, ScoreActivity.class);
                // intent.putExtra("GAME_RESULT", result);
                // startActivity(intent);
                finish(); // Tutup GameActivity
            }

            @Override
            public void onFailure(@NonNull Call<ScoreSubmissionResponse> call, @NonNull Throwable t) {
                showLoading(false);
                Toast.makeText(GameActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();

                // ★★★ SELALU PINDAH HALAMAN ★★★
                // com.example.uwikaquicktypergame.model.GameResult result = new com.example.uwikaquicktypergame.model.GameResult(stageId, 0, time, errors);
                // Intent intent = new Intent(GameActivity.this, ScoreActivity.class);
                // intent.putExtra("GAME_RESULT", result);
                // startActivity(intent);
                finish(); // Tutup GameActivity
            }
        });
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    // Levenshtein distance is a good algorithm for counting typing errors.
    public static int calculateLevenshteinDistance(String s1, String s2) {
        // (Implementation can be found online, simple version below)
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = Math.min(dp[i - 1][j - 1] + (s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1),
                            Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1));
                }
            }
        }
        return dp[s1.length()][s2.length()];
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop timer to prevent memory leaks
        timerHandler.removeCallbacks(timerRunnable);
    }
}
