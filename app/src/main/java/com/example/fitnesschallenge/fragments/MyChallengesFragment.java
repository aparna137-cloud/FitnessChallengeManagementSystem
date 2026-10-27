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
import com.example.fitnesschallenge.activities.ChallengeDetailsActivity;
import com.example.fitnesschallenge.adapters.ParticipantChallengeAdapter;
import com.example.fitnesschallenge.firebase.FirebaseAuthManager;
import com.example.fitnesschallenge.firebase.FirestoreManager;
import com.example.fitnesschallenge.models.Challenge;
import com.example.fitnesschallenge.models.Participant;
import com.example.fitnesschallenge.utils.Constants;

import java.util.List;

public class MyChallengesFragment extends Fragment {

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView rvMyChallenges;
    private ProgressBar pbMyChallenges;
    private TextView tvEmptyMyChallenges;
    private ParticipantChallengeAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_challenges, container, false);
        initViews(view);
        setupRecyclerView();
        setupListeners();
        return view;
    }

    private void initViews(View view) {
        swipeRefresh = view.findViewById(R.id.swipeRefreshMyChallenges);
        rvMyChallenges = view.findViewById(R.id.rvMyChallenges);
        pbMyChallenges = view.findViewById(R.id.pbMyChallenges);
        tvEmptyMyChallenges = view.findViewById(R.id.tvEmptyMyChallenges);
    }

    private void setupRecyclerView() {
        adapter = new ParticipantChallengeAdapter(requireContext(), new ParticipantChallengeAdapter.OnParticipantActionListener() {
            @Override
            public void onChallengeClick(Participant participant) {
                // Fetch full challenge model to open ChallengeDetailsActivity
                FirestoreManager.getInstance().getChallenge(participant.getChallengeId(), new FirestoreManager.FirestoreCallback<Challenge>() {
                    @Override
                    public void onSuccess(Challenge challenge) {
                        if (isAdded()) {
                            Intent intent = new Intent(requireContext(), ChallengeDetailsActivity.class);
                            intent.putExtra(Constants.EXTRA_CHALLENGE, challenge);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                    }
                });
            }

            @Override
            public void onLogActivityClick(Participant participant) {
                Intent intent = new Intent(requireContext(), AddActivityActivity.class);
                intent.putExtra(Constants.EXTRA_CHALLENGE_ID, participant.getChallengeId());
                startActivity(intent);
            }
        });

        rvMyChallenges.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvMyChallenges.setAdapter(adapter);
    }

    private void setupListeners() {
        swipeRefresh.setOnRefreshListener(this::loadMyChallenges);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMyChallenges();
    }

    private void loadMyChallenges() {
        String uid = FirebaseAuthManager.getInstance().getCurrentUserId();
        if (uid == null) {
            swipeRefresh.setRefreshing(false);
            return;
        }

        swipeRefresh.setRefreshing(true);
        tvEmptyMyChallenges.setVisibility(View.GONE);

        FirestoreManager.getInstance().getMyChallenges(uid, new FirestoreManager.FirestoreCallback<List<Participant>>() {
            @Override
            public void onSuccess(List<Participant> participants) {
                if (!isAdded()) return;
                swipeRefresh.setRefreshing(false);
                pbMyChallenges.setVisibility(View.GONE);

                if (participants == null || participants.isEmpty()) {
                    tvEmptyMyChallenges.setVisibility(View.VISIBLE);
                    rvMyChallenges.setVisibility(View.GONE);
                } else {
                    tvEmptyMyChallenges.setVisibility(View.GONE);
                    rvMyChallenges.setVisibility(View.VISIBLE);
                    adapter.setParticipants(participants);
                }
            }

            @Override
            public void onFailure(Exception e) {
                if (!isAdded()) return;
                swipeRefresh.setRefreshing(false);
                pbMyChallenges.setVisibility(View.GONE);
                tvEmptyMyChallenges.setText("Error loading my challenges: " + e.getMessage());
                tvEmptyMyChallenges.setVisibility(View.VISIBLE);
            }
        });
    }
}
