package com.example.fitnesschallenge.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.fitnesschallenge.R;
import com.example.fitnesschallenge.adapters.ParticipantChallengeAdapter;
import com.example.fitnesschallenge.firebase.FirebaseAuthManager;
import com.example.fitnesschallenge.firebase.FirestoreManager;
import com.example.fitnesschallenge.models.Challenge;
import com.example.fitnesschallenge.models.Participant;
import com.example.fitnesschallenge.models.User;
import com.example.fitnesschallenge.utils.Constants;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class UserDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_USER = "extra_user";

    private SwipeRefreshLayout swipeRefresh;
    private MaterialToolbar toolbar;
    private TextView tvName, tvEmail, tvStatusBadge, tvAge, tvBMI, tvGoal;
    private MaterialButton btnToggleSuspend, btnAssignChallenge;
    private RecyclerView rvAssignedChallenges;
    private TextView tvEmptyChallenges;
    private ParticipantChallengeAdapter adapter;

    private User targetUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        if (getIntent() != null && getIntent().hasExtra(EXTRA_USER)) {
            targetUser = (User) getIntent().getSerializableExtra(EXTRA_USER);
        }

        if (targetUser == null) {
            Toast.makeText(this, "Error: User data missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupListeners();
    }

    private void initViews() {
        swipeRefresh = findViewById(R.id.swipeRefreshUserDetails);
        toolbar = findViewById(R.id.toolbarUserDetails);
        tvName = findViewById(R.id.tvUserDetailName);
        tvEmail = findViewById(R.id.tvUserDetailEmail);
        tvStatusBadge = findViewById(R.id.tvUserDetailStatusBadge);
        tvAge = findViewById(R.id.tvUserDetailAge);
        tvBMI = findViewById(R.id.tvUserDetailBMI);
        tvGoal = findViewById(R.id.tvUserDetailGoal);
        btnToggleSuspend = findViewById(R.id.btnToggleSuspendUser);
        btnAssignChallenge = findViewById(R.id.btnAssignChallenge);
        rvAssignedChallenges = findViewById(R.id.rvUserAssignedChallenges);
        tvEmptyChallenges = findViewById(R.id.tvEmptyUserAssignedChallenges);

        populateUserData();
    }

    private void setupToolbar() {
        toolbar.setTitle(targetUser.getName().isEmpty() ? "User Details" : targetUser.getName());
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new ParticipantChallengeAdapter(this, new ParticipantChallengeAdapter.OnParticipantActionListener() {
            @Override
            public void onChallengeClick(Participant participant) {
            }

            @Override
            public void onLogActivityClick(Participant participant) {
            }
        });

        rvAssignedChallenges.setLayoutManager(new LinearLayoutManager(this));
        rvAssignedChallenges.setAdapter(adapter);
    }

    private void setupListeners() {
        swipeRefresh.setOnRefreshListener(this::verifyAdminAndLoadDetails);

        btnToggleSuspend.setOnClickListener(v -> confirmToggleSuspend());

        btnAssignChallenge.setOnClickListener(v -> showAssignChallengeDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        verifyAdminAndLoadDetails();
    }

    private void verifyAdminAndLoadDetails() {
        String currentUid = FirebaseAuthManager.getInstance().getCurrentUserId();
        if (currentUid == null) {
            finish();
            return;
        }

        FirestoreManager.getInstance().getUser(currentUid, new FirestoreManager.FirestoreCallback<User>() {
            @Override
            public void onSuccess(User admin) {
                if (!admin.isAdmin()) {
                    Toast.makeText(UserDetailsActivity.this, "Unauthorized access", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                loadUserAssignedChallenges();
            }

            @Override
            public void onFailure(Exception e) {
                finish();
            }
        });
    }

    private void populateUserData() {
        tvName.setText(targetUser.getName().isEmpty() ? "Anonymous Member" : targetUser.getName());
        tvEmail.setText(targetUser.getEmail());

        boolean isSuspended = targetUser.isSuspended();
        tvStatusBadge.setText(isSuspended ? "SUSPENDED" : "ACTIVE");
        tvStatusBadge.setTextColor(getResources().getColor(isSuspended ? R.color.status_error : R.color.status_active));

        btnToggleSuspend.setText(isSuspended ? "Unsuspend User" : "Suspend User");

        tvAge.setText(targetUser.getAge() + " yrs");
        tvBMI.setText(String.format("%.1f (%s)", targetUser.calculateBMI(), targetUser.getBMICategory()));
        tvGoal.setText(targetUser.getFitnessGoal());
    }

    private void loadUserAssignedChallenges() {
        swipeRefresh.setRefreshing(true);
        tvEmptyChallenges.setVisibility(View.GONE);

        FirestoreManager.getInstance().getMyChallenges(targetUser.getUid(), new FirestoreManager.FirestoreCallback<List<Participant>>() {
            @Override
            public void onSuccess(List<Participant> participants) {
                swipeRefresh.setRefreshing(false);
                if (participants == null || participants.isEmpty()) {
                    tvEmptyChallenges.setVisibility(View.VISIBLE);
                    rvAssignedChallenges.setVisibility(View.GONE);
                } else {
                    tvEmptyChallenges.setVisibility(View.GONE);
                    rvAssignedChallenges.setVisibility(View.VISIBLE);
                    adapter.setParticipants(participants);
                }
            }

            @Override
            public void onFailure(Exception e) {
                swipeRefresh.setRefreshing(false);
                tvEmptyChallenges.setText("Error loading challenges: " + e.getMessage());
                tvEmptyChallenges.setVisibility(View.VISIBLE);
            }
        });
    }

    private void confirmToggleSuspend() {
        boolean currentlySuspended = targetUser.isSuspended();
        String action = currentlySuspended ? "unsuspend" : "suspend";

        new AlertDialog.Builder(this)
                .setTitle(currentlySuspended ? "Unsuspend Account" : "Suspend Account")
                .setMessage("Are you sure you want to " + action + " user '" + targetUser.getName() + "'?")
                .setPositiveButton(currentlySuspended ? "Unsuspend" : "Suspend", (dialog, which) -> {
                    toggleSuspendUser(!currentlySuspended);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void toggleSuspendUser(boolean suspend) {
        swipeRefresh.setRefreshing(true);
        FirestoreManager.getInstance().setUserSuspendedStatus(targetUser.getUid(), suspend, new FirestoreManager.SimpleCallback() {
            @Override
            public void onSuccess() {
                swipeRefresh.setRefreshing(false);
                targetUser.setStatus(suspend ? "suspended" : "active");
                populateUserData();
                Toast.makeText(UserDetailsActivity.this, "User status updated!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(UserDetailsActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAssignChallengeDialog() {
        swipeRefresh.setRefreshing(true);
        FirestoreManager.getInstance().getChallenges(new FirestoreManager.FirestoreCallback<List<Challenge>>() {
            @Override
            public void onSuccess(List<Challenge> challenges) {
                swipeRefresh.setRefreshing(false);
                if (challenges == null || challenges.isEmpty()) {
                    Toast.makeText(UserDetailsActivity.this, "No challenges available to assign.", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<String> titles = new ArrayList<>();
                for (Challenge c : challenges) {
                    titles.add(c.getTitle() + " (" + c.getActivityType() + ")");
                }

                String[] challengeArray = titles.toArray(new String[0]);
                new AlertDialog.Builder(UserDetailsActivity.this)
                        .setTitle("Assign Challenge to " + targetUser.getName())
                        .setItems(challengeArray, (dialog, which) -> {
                            Challenge selected = challenges.get(which);
                            assignChallenge(selected.getId());
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }

            @Override
            public void onFailure(Exception e) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(UserDetailsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void assignChallenge(String challengeId) {
        swipeRefresh.setRefreshing(true);
        FirestoreManager.getInstance().joinChallenge(targetUser.getUid(), challengeId, new FirestoreManager.FirestoreCallback<String>() {
            @Override
            public void onSuccess(String result) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(UserDetailsActivity.this, "Challenge assigned successfully!", Toast.LENGTH_SHORT).show();
                loadUserAssignedChallenges();
            }

            @Override
            public void onFailure(Exception e) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(UserDetailsActivity.this, "Assignment failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}