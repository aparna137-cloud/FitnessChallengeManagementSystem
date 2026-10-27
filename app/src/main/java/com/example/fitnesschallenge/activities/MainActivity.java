package com.example.fitnesschallenge.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.fitnesschallenge.R;
import com.example.fitnesschallenge.fragments.ChallengesFragment;
import com.example.fitnesschallenge.fragments.DashboardFragment;
import com.example.fitnesschallenge.fragments.LeaderboardFragment;
import com.example.fitnesschallenge.fragments.ProfileFragment;
import com.example.fitnesschallenge.fragments.ProgressFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavView = findViewById(R.id.bottomNavView);

        if (savedInstanceState == null) {
            loadFragment(new DashboardFragment());
        }

        bottomNavView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new DashboardFragment();
            } else if (itemId == R.id.nav_challenges) {
                selectedFragment = new ChallengesFragment();
            } else if (itemId == R.id.nav_progress) {
                selectedFragment = new ProgressFragment();
            } else if (itemId == R.id.nav_leaderboard) {
                selectedFragment = new LeaderboardFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    public void switchToTab(int navItemId) {
        if (bottomNavView != null) {
            bottomNavView.setSelectedItemId(navItemId);
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}