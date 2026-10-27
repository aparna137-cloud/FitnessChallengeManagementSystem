package com.example.fitnesschallenge.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.fitnesschallenge.R;
import com.example.fitnesschallenge.firebase.FirebaseAuthManager;
import com.example.fitnesschallenge.firebase.FirestoreManager;
import com.example.fitnesschallenge.models.User;
import com.example.fitnesschallenge.utils.SampleDataHelper;
import com.google.android.material.button.MaterialButton;

public class AdminDashboardActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefresh;
    private TextView tvAdminEmail;
    private TextView tvStatUsers, tvStatActiveUsers, tvStatChallenges, tvStatActiveChallenges;
    private TextView tvStatParticipants, tvStatCompletionRate;
    private MaterialButton btnManageChallenges, btnManageUsers, btnInspectProgress, btnSeedData, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        initViews();
        setupListeners();
    }

    private void initViews() {
        swipeRefresh = findViewById(R.id.swipeRefreshAdminDashboard);
        tvAdminEmail = findViewById(R.id.tvAdminEmail);
        tvStatUsers = findViewById(R.id.tvAdminStatUsers);
        tvStatActiveUsers = findViewById(R.id.tvAdminStatActiveUsers);
        tvStatChallenges = findViewById(R.id.tvAdminStatChallenges);
        tvStatActiveChallenges = findViewById(R.id.tvAdminStatActiveChallenges);
        tvStatParticipants = findViewById(R.id.tvAdminStatParticipants);
        tvStatCompletionRate = findViewById(R.id.tvAdminStatCompletionRate);

        btnManageChallenges = findViewById(R.id.btnAdminManageChallenges);
        btnManageUsers = findViewById(R.id.btnAdminManageUsers);
        btnInspectProgress = findViewById(R.id.btnAdminInspectProgress);
        btnSeedData = findViewById(R.id.btnAdminSeedData);
        btnLogout = findViewById(R.id.btnAdminLogout);

        String email = FirebaseAuthManager.getInstance().getCurrentUserEmail();
        if (email != null) tvAdminEmail.setText(email);
    }

    private void setupListeners() {
        swipeRefresh.setOnRefreshListener(this::verifyAdminAndLoadStats);

        btnManageChallenges.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, ManageChallengesActivity.class));
        });

        btnManageUsers.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, ManageUsersActivity.class));
        });

        btnInspectProgress.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminUserProgressActivity.class));
        });

        btnSeedData.setOnClickListener(v -> seedSampleChallenges());

        btnLogout.setOnClickListener(v -> confirmLogout());
    }

    @Override
    protected void onResume() {
        super.onResume();
        verifyAdminAndLoadStats();
    }

    private void verifyAdminAndLoadStats() {
        String uid = FirebaseAuthManager.getInstance().getCurrentUserId();
        if (uid == null) {
            redirectToLogin("Session expired. Please log in.");
            return;
        }

        // Security Authorization Check: Verify user role in Firestore
        FirestoreManager.getInstance().getUser(uid, new FirestoreManager.FirestoreCallback<User>() {
            @Override
            public void onSuccess(User user) {
                if (!user.isAdmin()) {
                    redirectToLogin("Unauthorized access: Admin privilege required.");
                    return;
                }
                loadAdminStatistics();
            }

            @Override
            public void onFailure(Exception e) {
                redirectToLogin("Authentication error: " + e.getMessage());
            }
        });
    }

    private void redirectToLogin(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        FirebaseAuthManager.getInstance().logout();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void loadAdminStatistics() {
        swipeRefresh.setRefreshing(true);

        FirestoreManager.getInstance().getAdminStatistics(new FirestoreManager.FirestoreCallback<FirestoreManager.AdminStats>() {
            @Override
            public void onSuccess(FirestoreManager.AdminStats stats) {
                swipeRefresh.setRefreshing(false);
                tvStatUsers.setText(String.valueOf(stats.totalUsers));
                tvStatActiveUsers.setText("Active: " + stats.activeUsers);
                tvStatChallenges.setText(String.valueOf(stats.totalChallenges));
                tvStatActiveChallenges.setText("Active: " + stats.activeChallenges);
                tvStatParticipants.setText(String.valueOf(stats.totalParticipants));
                tvStatCompletionRate.setText(stats.overallCompletionRatePct + "%");
            }

            @Override
            public void onFailure(Exception e) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(AdminDashboardActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void seedSampleChallenges() {
        btnSeedData.setEnabled(false);
        Toast.makeText(this, "Seeding sample challenges into Cloud Firestore...", Toast.LENGTH_SHORT).show();

        SampleDataHelper.seedSampleChallenges(FirestoreManager.getInstance(), new SampleDataHelper.OnSeedCompleteListener() {
            @Override
            public void onSuccess(String message) {
                btnSeedData.setEnabled(true);
                Toast.makeText(AdminDashboardActivity.this, message, Toast.LENGTH_LONG).show();
                loadAdminStatistics();
            }

            @Override
            public void onError(String error) {
                btnSeedData.setEnabled(true);
                Toast.makeText(AdminDashboardActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_logout_title)
                .setMessage(R.string.confirm_logout_msg)
                .setPositiveButton("Logout", (dialog, which) -> {
                    FirebaseAuthManager.getInstance().logout();
                    Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}