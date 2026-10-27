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
import com.example.fitnesschallenge.activities.ChallengeDetailsActivity;
import com.example.fitnesschallenge.adapters.ChallengeAdapter;
import com.example.fitnesschallenge.firebase.FirestoreManager;
import com.example.fitnesschallenge.models.Challenge;
import com.example.fitnesschallenge.utils.Constants;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class ChallengesFragment extends Fragment {

    private SwipeRefreshLayout swipeRefresh;
    private ChipGroup chipGroupFilter;
    private RecyclerView rvChallenges;
    private ProgressBar pbChallenges;
    private TextView tvEmptyChallenges;
    private MaterialButton btnNavMyChallenges;
    private ChallengeAdapter challengeAdapter;

    private List<Challenge> allChallenges = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenges, container, false);
        initViews(view);
        setupRecyclerView();
        setupListeners();
        return view;
    }

    private void initViews(View view) {
        swipeRefresh = view.findViewById(R.id.swipeRefreshChallenges);
        chipGroupFilter = view.findViewById(R.id.chipGroupFilter);
        rvChallenges = view.findViewById(R.id.rvChallenges);
        pbChallenges = view.findViewById(R.id.pbChallenges);
        tvEmptyChallenges = view.findViewById(R.id.tvEmptyChallenges);
        btnNavMyChallenges = view.findViewById(R.id.btnNavMyChallenges);
    }

    private void setupRecyclerView() {
        challengeAdapter = new ChallengeAdapter(requireContext(), false, new ChallengeAdapter.OnChallengeClickListener() {
            @Override
            public void onChallengeClick(Challenge challenge) {
                Intent intent = new Intent(requireContext(), ChallengeDetailsActivity.class);
                intent.putExtra(Constants.EXTRA_CHALLENGE, challenge);
                startActivity(intent);
            }

            @Override
            public void onEditClick(Challenge challenge) {
            }

            @Override
            public void onDeleteClick(Challenge challenge) {
            }
        });

        rvChallenges.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvChallenges.setAdapter(challengeAdapter);
    }

    private void setupListeners() {
        swipeRefresh.setOnRefreshListener(this::loadChallenges);

        if (btnNavMyChallenges != null) {
            btnNavMyChallenges.setOnClickListener(v -> {
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new MyChallengesFragment())
                        .addToBackStack(null)
                        .commit();
            });
        }

        chipGroupFilter.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.contains(R.id.chipActive)) {
                filterChallenges(Constants.STATUS_ACTIVE);
            } else if (checkedIds.contains(R.id.chipCompleted)) {
                filterChallenges(Constants.STATUS_COMPLETED);
            } else {
                filterChallenges(null);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadChallenges();
    }

    private void loadChallenges() {
        swipeRefresh.setRefreshing(true);
        tvEmptyChallenges.setVisibility(View.GONE);

        FirestoreManager.getInstance().getChallenges(new FirestoreManager.FirestoreCallback<List<Challenge>>() {
            @Override
            public void onSuccess(List<Challenge> challenges) {
                if (!isAdded()) return;
                swipeRefresh.setRefreshing(false);
                pbChallenges.setVisibility(View.GONE);
                allChallenges = challenges != null ? challenges : new ArrayList<>();

                int checkedChipId = chipGroupFilter.getCheckedChipId();
                if (checkedChipId == R.id.chipActive) {
                    filterChallenges(Constants.STATUS_ACTIVE);
                } else if (checkedChipId == R.id.chipCompleted) {
                    filterChallenges(Constants.STATUS_COMPLETED);
                } else {
                    filterChallenges(null);
                }
            }

            @Override
            public void onFailure(Exception e) {
                if (!isAdded()) return;
                swipeRefresh.setRefreshing(false);
                pbChallenges.setVisibility(View.GONE);
                tvEmptyChallenges.setText("Error loading challenges: " + e.getMessage());
                tvEmptyChallenges.setVisibility(View.VISIBLE);
            }
        });
    }

    private void filterChallenges(@Nullable String statusFilter) {
        List<Challenge> filtered = new ArrayList<>();
        for (Challenge c : allChallenges) {
            if (statusFilter == null || statusFilter.equalsIgnoreCase(c.getStatus())) {
                filtered.add(c);
            }
        }

        challengeAdapter.setChallenges(filtered);

        if (filtered.isEmpty()) {
            tvEmptyChallenges.setText("No challenges found.");
            tvEmptyChallenges.setVisibility(View.VISIBLE);
        } else {
            tvEmptyChallenges.setVisibility(View.GONE);
        }
    }
}