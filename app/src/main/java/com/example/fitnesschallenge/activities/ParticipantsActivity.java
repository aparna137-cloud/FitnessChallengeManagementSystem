package com.example.fitnesschallenge.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnesschallenge.R;
import com.example.fitnesschallenge.adapters.ParticipantAdapter;
import com.example.fitnesschallenge.firebase.FirestoreManager;
import com.example.fitnesschallenge.models.Challenge;
import com.example.fitnesschallenge.models.Participant;
import com.example.fitnesschallenge.models.User;
import com.example.fitnesschallenge.utils.Constants;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticipantsActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RecyclerView rvParticipants;
    private ProgressBar pbParticipants;
    private TextView tvEmptyParticipants;
    private ParticipantAdapter adapter;

    private String challengeId;
    private Challenge challenge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants);

        challengeId = getIntent().getStringExtra(Constants.EXTRA_CHALLENGE_ID);
        challenge = (Challenge) getIntent().getSerializableExtra(Constants.EXTRA_CHALLENGE);

        initViews();
        setupToolbar();
        setupRecyclerView();
        loadParticipants();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbarParticipants);
        rvParticipants = findViewById(R.id.rvParticipants);
        pbParticipants = findViewById(R.id.pbParticipants);
        tvEmptyParticipants = findViewById(R.id.tvEmptyParticipants);

        if (challenge != null) {
            toolbar.setTitle(challenge.getTitle() + " - Participants");
        }
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new ParticipantAdapter(this);
        rvParticipants.setLayoutManager(new LinearLayoutManager(this));
        rvParticipants.setAdapter(adapter);
    }

    private void loadParticipants() {
        if (challengeId == null) {
            tvEmptyParticipants.setVisibility(View.VISIBLE);
            return;
        }

        pbParticipants.setVisibility(View.VISIBLE);
        tvEmptyParticipants.setVisibility(View.GONE);

        // Fetch users map to display friendly names instead of raw UIDs
        FirestoreManager.getInstance().getAllUsers(new FirestoreManager.FirestoreCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                Map<String, String> userNames = new HashMap<>();
                for (User u : users) {
                    userNames.put(u.getUid(), u.getName());
                }

                FirestoreManager.getInstance().getChallengeParticipants(challengeId, new FirestoreManager.FirestoreCallback<List<Participant>>() {
                    @Override
                    public void onSuccess(List<Participant> participants) {
                        pbParticipants.setVisibility(View.GONE);

                        if (participants == null || participants.isEmpty()) {
                            tvEmptyParticipants.setVisibility(View.VISIBLE);
                            rvParticipants.setVisibility(View.GONE);
                        } else {
                            tvEmptyParticipants.setVisibility(View.GONE);
                            rvParticipants.setVisibility(View.VISIBLE);

                            for (Participant p : participants) {
                                String name = userNames.get(p.getUserId());
                                if (name != null && !name.isEmpty()) {
                                    p.setUserName(name);
                                }
                            }

                            adapter.setParticipants(participants);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        pbParticipants.setVisibility(View.GONE);
                        tvEmptyParticipants.setText("Error loading participants: " + e.getMessage());
                        tvEmptyParticipants.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                pbParticipants.setVisibility(View.GONE);
                tvEmptyParticipants.setText("Error loading user records: " + e.getMessage());
                tvEmptyParticipants.setVisibility(View.VISIBLE);
            }
        });
    }
}
