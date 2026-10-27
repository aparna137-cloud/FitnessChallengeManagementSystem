package com.example.fitnesschallenge.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ProgressBar;
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
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminUserProgressActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefresh;
    private MaterialToolbar toolbar;
    private TextInputEditText etSearchProgress;
    private ChipGroup chipGroupProgressFilter;
    private RecyclerView rvUserProgress;
    private ProgressBar pbUserProgress;
    private TextView tvEmptyUserProgress;
    private ParticipantChallengeAdapter adapter;

    private List<Participant> allParticipants = new ArrayList<>();
    private Map<String, User> userMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_progress);

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupListeners();
    }

    private void initViews() {
        swipeRefresh = findViewById(R.id.swipeRefreshUserProgress);
        toolbar = findViewById(R.id.toolbarUserProgress);
        etSearchProgress = findViewById(R.id.etSearchProgress);
        chipGroupProgressFilter = findViewById(R.id.chipGroupProgressFilter);
        rvUserProgress = findViewById(R.id.rvUserProgress);
        pbUserProgress = findViewById(R.id.pbUserProgress);
        tvEmptyUserProgress = findViewById(R.id.tvEmptyUserProgress);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new ParticipantChallengeAdapter(this, new ParticipantChallengeAdapter.OnParticipantActionListener() {
            @Override
            public void onChallengeClick(Participant participant) {
                FirestoreManager.getInstance().getChallenge(participant.getChallengeId(), new FirestoreManager.FirestoreCallback<Challenge>() {
                    @Override
                    public void onSuccess(Challenge challenge) {
                        Intent intent = new Intent(AdminUserProgressActivity.this, ChallengeDetailsActivity.class);
                        intent.putExtra(Constants.EXTRA_CHALLENGE, challenge);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Exception e) {
                    }
                });
            }

            @Override
            public void onLogActivityClick(Participant participant) {
            }
        });

        rvUserProgress.setLayoutManager(new LinearLayoutManager(this));
        rvUserProgress.setAdapter(adapter);
    }

    private void setupListeners() {
        swipeRefresh.setOnRefreshListener(this::verifyAdminAndLoadProgress);

        if (etSearchProgress != null) {
            etSearchProgress.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    applyFilter();
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        if (chipGroupProgressFilter != null) {
            chipGroupProgressFilter.setOnCheckedStateChangeListener((group, checkedIds) -> applyFilter());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        verifyAdminAndLoadProgress();
    }

    private void verifyAdminAndLoadProgress() {
        String currentUid = FirebaseAuthManager.getInstance().getCurrentUserId();
        if (currentUid == null) {
            finish();
            return;
        }

        FirestoreManager.getInstance().getUser(currentUid, new FirestoreManager.FirestoreCallback<User>() {
            @Override
            public void onSuccess(User admin) {
                if (!admin.isAdmin()) {
                    Toast.makeText(AdminUserProgressActivity.this, "Unauthorized access", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                loadAllProgressData();
            }

            @Override
            public void onFailure(Exception e) {
                finish();
            }
        });
    }

    private void loadAllProgressData() {
        swipeRefresh.setRefreshing(true);
        tvEmptyUserProgress.setVisibility(View.GONE);

        // Fetch users map first to map user names
        FirestoreManager.getInstance().getAllUsers(new FirestoreManager.FirestoreCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                userMap.clear();
                if (users != null) {
                    for (User u : users) {
                        userMap.put(u.getUid(), u);
                    }
                }

                FirestoreManager.getInstance().getAllParticipants(new FirestoreManager.FirestoreCallback<List<Participant>>() {
                    @Override
                    public void onSuccess(List<Participant> participants) {
                        swipeRefresh.setRefreshing(false);
                        pbUserProgress.setVisibility(View.GONE);
                        allParticipants = participants != null ? participants : new ArrayList<>();

                        for (Participant p : allParticipants) {
                            User u = userMap.get(p.getUserId());
                            if (u != null) {
                                p.setUserName(u.getName().isEmpty() ? u.getEmail() : u.getName());
                            }
                        }

                        applyFilter();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        swipeRefresh.setRefreshing(false);
                        pbUserProgress.setVisibility(View.GONE);
                        tvEmptyUserProgress.setText("Error loading progress: " + e.getMessage());
                        tvEmptyUserProgress.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                swipeRefresh.setRefreshing(false);
                pbUserProgress.setVisibility(View.GONE);
                tvEmptyUserProgress.setText("Error loading users: " + e.getMessage());
                tvEmptyUserProgress.setVisibility(View.VISIBLE);
            }
        });
    }

    private void applyFilter() {
        String searchQuery = etSearchProgress != null && etSearchProgress.getText() != null ? etSearchProgress.getText().toString().trim().toLowerCase() : "";
        int checkedChipId = chipGroupProgressFilter != null ? chipGroupProgressFilter.getCheckedChipId() : View.NO_ID;

        List<Participant> filtered = new ArrayList<>();
        for (Participant p : allParticipants) {
            String title = p.getChallengeTitle().toLowerCase();
            String userName = p.getUserName().toLowerCase();

            boolean matchesSearch = title.contains(searchQuery) || userName.contains(searchQuery);

            boolean matchesStatus = true;
            if (checkedChipId == R.id.chipProgressCompleted) {
                matchesStatus = Constants.STATUS_COMPLETED.equalsIgnoreCase(p.getStatus());
            } else if (checkedChipId == R.id.chipProgressInProgress) {
                matchesStatus = Constants.STATUS_ACTIVE.equalsIgnoreCase(p.getStatus()) && p.getCompletedValue() > 0;
            } else if (checkedChipId == R.id.chipProgressNotStarted) {
                matchesStatus = Constants.STATUS_ACTIVE.equalsIgnoreCase(p.getStatus()) && p.getCompletedValue() == 0;
            }

            if (matchesSearch && matchesStatus) {
                filtered.add(p);
            }
        }

        adapter.setParticipants(filtered);

        if (filtered.isEmpty()) {
            tvEmptyUserProgress.setText("No matching progress records found.");
            tvEmptyUserProgress.setVisibility(View.VISIBLE);
            rvUserProgress.setVisibility(View.GONE);
        } else {
            tvEmptyUserProgress.setVisibility(View.GONE);
            rvUserProgress.setVisibility(View.VISIBLE);
        }
    }
}