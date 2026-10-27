package com.example.fitnesschallenge.fragments;

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
import com.example.fitnesschallenge.adapters.LeaderboardAdapter;
import com.example.fitnesschallenge.firebase.FirebaseAuthManager;
import com.example.fitnesschallenge.firebase.FirestoreManager;

import java.util.List;

public class LeaderboardFragment extends Fragment {

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView rvLeaderboard;
    private ProgressBar pbLeaderboard;
    private TextView tvEmptyLeaderboard;
    private LeaderboardAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        initViews(view);
        setupRecyclerView();
        setupListeners();
        return view;
    }

    private void initViews(View view) {
        swipeRefresh = view.findViewById(R.id.swipeRefreshLeaderboard);
        rvLeaderboard = view.findViewById(R.id.rvLeaderboard);
        pbLeaderboard = view.findViewById(R.id.pbLeaderboard);
        tvEmptyLeaderboard = view.findViewById(R.id.tvEmptyLeaderboard);
    }

    private void setupRecyclerView() {
        String currentUid = FirebaseAuthManager.getInstance().getCurrentUserId();
        adapter = new LeaderboardAdapter(requireContext(), currentUid);
        rvLeaderboard.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvLeaderboard.setAdapter(adapter);
    }

    private void setupListeners() {
        swipeRefresh.setOnRefreshListener(this::loadLeaderboard);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadLeaderboard();
    }

    private void loadLeaderboard() {
        swipeRefresh.setRefreshing(true);
        tvEmptyLeaderboard.setVisibility(View.GONE);

        FirestoreManager.getInstance().getLeaderboard(new FirestoreManager.FirestoreCallback<List<FirestoreManager.LeaderboardEntry>>() {
            @Override
            public void onSuccess(List<FirestoreManager.LeaderboardEntry> entries) {
                if (!isAdded()) return;
                swipeRefresh.setRefreshing(false);
                pbLeaderboard.setVisibility(View.GONE);

                if (entries == null || entries.isEmpty()) {
                    tvEmptyLeaderboard.setVisibility(View.VISIBLE);
                    rvLeaderboard.setVisibility(View.GONE);
                } else {
                    tvEmptyLeaderboard.setVisibility(View.GONE);
                    rvLeaderboard.setVisibility(View.VISIBLE);
                    adapter.setEntries(entries);
                }
            }

            @Override
            public void onFailure(Exception e) {
                if (!isAdded()) return;
                swipeRefresh.setRefreshing(false);
                pbLeaderboard.setVisibility(View.GONE);
                tvEmptyLeaderboard.setText("Error loading leaderboard: " + e.getMessage());
                tvEmptyLeaderboard.setVisibility(View.VISIBLE);
            }
        });
    }
}
