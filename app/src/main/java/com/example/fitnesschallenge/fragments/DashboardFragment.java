package com.example.fitnesschallenge.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.fitnesschallenge.R;
import com.example.fitnesschallenge.activities.AddActivityActivity;
import com.example.fitnesschallenge.activities.MainActivity;
import com.example.fitnesschallenge.adapters.ActivityAdapter;
import com.example.fitnesschallenge.firebase.FirebaseAuthManager;
import com.example.fitnesschallenge.firebase.FirestoreManager;
import com.example.fitnesschallenge.models.Activity;
import com.example.fitnesschallenge.models.User;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class DashboardFragment extends Fragment {

    private SwipeRefreshLayout swipeRefresh;
    private TextView tvDashboardUserName;
    private TextView tvTotalPoints, tvActiveChallenges, tvCompletedChallenges, tvTotalActivities;
    private TextView tvOverallProgressPct, tvNoActivities;
    private ProgressBar pbOverallProgress;
    private MaterialButton btnQuickBrowseChallenges, btnQuickLogActivity;
    private View cardActiveChallenges;
    private RecyclerView rvRecentActivities;
    private ActivityAdapter activityAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        initViews(view);
        setupRecyclerView();
        setupListeners();
        return view;
    }

    private void initViews(View view) {
        swipeRefresh = view.findViewById(R.id.swipeRefreshDashboard);
        tvDashboardUserName = view.findViewById(R.id.tvDashboardUserName);
        tvTotalPoints = view.findViewById(R.id.tvTotalPointsStat);
        tvActiveChallenges = view.findViewById(R.id.tvActiveChallengesStat);
        tvCompletedChallenges = view.findViewById(R.id.tvCompletedChallengesStat);
        tvTotalActivities = view.findViewById(R.id.tvTotalActivitiesStat);
        tvOverallProgressPct = view.findViewById(R.id.tvOverallProgressPercentage);
        pbOverallProgress = view.findViewById(R.id.pbOverallProgress);
        tvNoActivities = view.findViewById(R.id.tvNoActivities);
        btnQuickBrowseChallenges = view.findViewById(R.id.btnQuickBrowseChallenges);
        btnQuickLogActivity = view.findViewById(R.id.btnQuickLogActivity);
        cardActiveChallenges = view.findViewById(R.id.cardActiveChallenges);
        rvRecentActivities = view.findViewById(R.id.rvRecentActivities);
    }

    private void setupRecyclerView() {
        activityAdapter = new ActivityAdapter(requireContext());
        rvRecentActivities.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvRecentActivities.setAdapter(activityAdapter);
    }

    private void setupListeners() {
        swipeRefresh.setOnRefreshListener(this::loadDashboardData);

        btnQuickBrowseChallenges.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).switchToTab(R.id.nav_challenges);
            }
        });

        btnQuickLogActivity.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), AddActivityActivity.class));
        });

        if (cardActiveChallenges != null) {
            cardActiveChallenges.setOnClickListener(v -> {
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new MyChallengesFragment())
                        .addToBackStack(null)
                        .commit();
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDashboardData();
    }

    private void loadDashboardData() {
        String uid = FirebaseAuthManager.getInstance().getCurrentUserId();
        if (uid == null) {
            swipeRefresh.setRefreshing(false);
            return;
        }

        swipeRefresh.setRefreshing(true);

        // 1. Fetch user profile for greeting
        FirestoreManager.getInstance().getUser(uid, new FirestoreManager.FirestoreCallback<User>() {
            @Override
            public void onSuccess(User user) {
                if (isAdded()) {
                    tvDashboardUserName.setText(user.getName().isEmpty() ? "Fitness Champion" : user.getName());
                }
            }

            @Override
            public void onFailure(Exception e) {
            }
        });

        // 2. Fetch live stats
        FirestoreManager.getInstance().getUserStatistics(uid, new FirestoreManager.FirestoreCallback<FirestoreManager.UserStats>() {
            @Override
            public void onSuccess(FirestoreManager.UserStats stats) {
                if (!isAdded()) return;

                tvTotalPoints.setText(String.valueOf(stats.totalPoints));
                tvActiveChallenges.setText(String.valueOf(stats.activeChallenges));
                tvCompletedChallenges.setText(String.valueOf(stats.completedChallenges));
                tvTotalActivities.setText(String.valueOf(stats.totalActivities));

                tvOverallProgressPct.setText(stats.overallProgressPct + "%");
                pbOverallProgress.setProgress(stats.overallProgressPct);
            }

            @Override
            public void onFailure(Exception e) {
            }
        });

        // 3. Fetch recent activities (limit to top 5)
        FirestoreManager.getInstance().getUserActivities(uid, new FirestoreManager.FirestoreCallback<List<Activity>>() {
            @Override
            public void onSuccess(List<Activity> list) {
                if (!isAdded()) return;
                swipeRefresh.setRefreshing(false);

                if (list == null || list.isEmpty()) {
                    tvNoActivities.setVisibility(View.VISIBLE);
                    rvRecentActivities.setVisibility(View.GONE);
                } else {
                    tvNoActivities.setVisibility(View.GONE);
                    rvRecentActivities.setVisibility(View.VISIBLE);
                    List<Activity> subList = list.size() > 5 ? list.subList(0, 5) : list;
                    activityAdapter.setActivities(subList);
                }
            }

            @Override
            public void onFailure(Exception e) {
                if (isAdded()) {
                    swipeRefresh.setRefreshing(false);
                    tvNoActivities.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}