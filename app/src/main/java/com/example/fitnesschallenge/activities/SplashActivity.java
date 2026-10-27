package com.example.fitnesschallenge.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitnesschallenge.R;
import com.example.fitnesschallenge.firebase.FirebaseAuthManager;
import com.example.fitnesschallenge.firebase.FirestoreManager;
import com.example.fitnesschallenge.models.User;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(this::checkAuthAndRoute, 1200);
    }

    private void checkAuthAndRoute() {
        FirebaseAuthManager authManager = FirebaseAuthManager.getInstance();

        if (!authManager.isLoggedIn()) {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
            return;
        }

        String uid = authManager.getCurrentUserId();
        if (uid == null) {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
            return;
        }

        FirestoreManager.getInstance().getUser(uid, new FirestoreManager.FirestoreCallback<User>() {
            @Override
            public void onSuccess(User user) {
                if (user.isAdmin()) {
                    startActivity(new Intent(SplashActivity.this, AdminDashboardActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                // In case user document does not exist or network issue, fallback to MainActivity
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}
