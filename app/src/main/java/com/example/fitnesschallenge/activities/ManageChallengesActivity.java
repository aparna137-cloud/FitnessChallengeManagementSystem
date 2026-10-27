package com.example.fitnesschallenge.activities;

import android.app.AlertDialog;
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

import com.example.fitnesschallenge.R;
import com.example.fitnesschallenge.adapters.ChallengeAdapter;
import com.example.fitnesschallenge.firebase.FirebaseAuthManager;
import com.example.fitnesschallenge.firebase.FirestoreManager;
import com.example.fitnesschallenge.models.Challenge;
import com.example.fitnesschallenge.models.User;
import com.example.fitnesschallenge.utils.Constants;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class ManageChallengesActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextInputEditText etSearchChallenge;
    private ChipGroup chipGroupFilter;
    private RecyclerView rvChallenges;
    private ProgressBar pbChallenges;
    private TextView tvEmptyChallenges;
    private ExtendedFloatingActionButton fabAddChallenge;
    private ChallengeAdapter adapter;

    private List<Challenge> allChallenges = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_challenges);

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbarManageChallenges);
        etSearchChallenge = findViewById(R.id.etSearchChallenge);
        chipGroupFilter = findViewById(R.id.chipGroupAdminFilter);
        rvChallenges = findViewById(R.id.rvManageChallenges);
        pbChallenges = findViewById(R.id.pbManageChallenges);
        tvEmptyChallenges = findViewById(R.id.tvEmptyManageChallenges);
        fabAddChallenge = findViewById(R.id.fabAddChallenge);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new ChallengeAdapter(this, true, new ChallengeAdapter.OnChallengeClickListener() {
            @Override
            public void onChallengeClick(Challenge challenge) {
                Intent intent = new Intent(ManageChallengesActivity.this, ParticipantsActivity.class);
                intent.putExtra(Constants.EXTRA_CHALLENGE_ID, challenge.getId());
                intent.putExtra(Constants.EXTRA_CHALLENGE, challenge);
                startActivity(intent);
            }

            @Override
            public void onEditClick(Challenge challenge) {
                Intent intent = new Intent(ManageChallengesActivity.this, AddEditChallengeActivity.class);
                intent.putExtra(Constants.EXTRA_CHALLENGE, challenge);
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Challenge challenge) {
                showChallengeActionDialog(challenge);
            }
        });

        rvChallenges.setLayoutManager(new LinearLayoutManager(this));
        rvChallenges.setAdapter(adapter);
    }

    private void setupListeners() {
        fabAddChallenge.setOnClickListener(v -> {
            startActivity(new Intent(ManageChallengesActivity.this, AddEditChallengeActivity.class));
        });

        if (etSearchChallenge != null) {
            etSearchChallenge.addTextChangedListener(new TextWatcher() {
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

        if (chipGroupFilter != null) {
            chipGroupFilter.setOnCheckedStateChangeListener((group, checkedIds) -> applyFilter());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        verifyAdminAndLoadChallenges();
    }

    private void verifyAdminAndLoadChallenges() {
        String currentUid = FirebaseAuthManager.getInstance().getCurrentUserId();
        if (currentUid == null) {
            finish();
            return;
        }

        FirestoreManager.getInstance().getUser(currentUid, new FirestoreManager.FirestoreCallback<User>() {
            @Override
            public void onSuccess(User admin) {
                if (!admin.isAdmin()) {
                    Toast.makeText(ManageChallengesActivity.this, "Unauthorized access", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                loadChallenges();
            }

            @Override
            public void onFailure(Exception e) {
                finish();
            }
        });
    }

    private void loadChallenges() {
        pbChallenges.setVisibility(View.VISIBLE);
        tvEmptyChallenges.setVisibility(View.GONE);

        FirestoreManager.getInstance().getChallenges(new FirestoreManager.FirestoreCallback<List<Challenge>>() {
            @Override
            public void onSuccess(List<Challenge> challenges) {
                pbChallenges.setVisibility(View.GONE);
                allChallenges = challenges != null ? challenges : new ArrayList<>();
                applyFilter();
            }

            @Override
            public void onFailure(Exception e) {
                pbChallenges.setVisibility(View.GONE);
                tvEmptyChallenges.setText("Error loading challenges: " + e.getMessage());
                tvEmptyChallenges.setVisibility(View.VISIBLE);
            }
        });
    }

    private void applyFilter() {
        String searchQuery = etSearchChallenge != null && etSearchChallenge.getText() != null ? etSearchChallenge.getText().toString().trim().toLowerCase() : "";
        int checkedChipId = chipGroupFilter != null ? chipGroupFilter.getCheckedChipId() : View.NO_ID;

        List<Challenge> filtered = new ArrayList<>();
        for (Challenge c : allChallenges) {
            boolean matchesSearch = c.getTitle().toLowerCase().contains(searchQuery) || c.getActivityType().toLowerCase().contains(searchQuery);

            boolean matchesStatus = true;
            if (checkedChipId == R.id.chipAdminActive) {
                matchesStatus = "active".equalsIgnoreCase(c.getStatus());
            } else if (checkedChipId == R.id.chipAdminCompleted) {
                matchesStatus = "completed".equalsIgnoreCase(c.getStatus());
            } else if (checkedChipId == R.id.chipAdminDeactivated) {
                matchesStatus = "deactivated".equalsIgnoreCase(c.getStatus());
            }

            if (matchesSearch && matchesStatus) {
                filtered.add(c);
            }
        }

        adapter.setChallenges(filtered);

        if (filtered.isEmpty()) {
            tvEmptyChallenges.setText("No matching challenges found.");
            tvEmptyChallenges.setVisibility(View.VISIBLE);
            rvChallenges.setVisibility(View.GONE);
        } else {
            tvEmptyChallenges.setVisibility(View.GONE);
            rvChallenges.setVisibility(View.VISIBLE);
        }
    }

    private void showChallengeActionDialog(Challenge challenge) {
        String[] options = {"Edit Challenge", "Deactivate Challenge (Soft Delete)", "Permanently Delete"};
        new AlertDialog.Builder(this)
                .setTitle("Manage Challenge: " + challenge.getTitle())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        Intent intent = new Intent(ManageChallengesActivity.this, AddEditChallengeActivity.class);
                        intent.putExtra(Constants.EXTRA_CHALLENGE, challenge);
                        startActivity(intent);
                    } else if (which == 1) {
                        deactivateChallenge(challenge.getId());
                    } else if (which == 2) {
                        confirmHardDeleteChallenge(challenge);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deactivateChallenge(String challengeId) {
        pbChallenges.setVisibility(View.VISIBLE);
        FirestoreManager.getInstance().deactivateChallenge(challengeId, new FirestoreManager.SimpleCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(ManageChallengesActivity.this, "Challenge soft-deactivated successfully!", Toast.LENGTH_SHORT).show();
                loadChallenges();
            }

            @Override
            public void onFailure(Exception e) {
                pbChallenges.setVisibility(View.GONE);
                Toast.makeText(ManageChallengesActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmHardDeleteChallenge(Challenge challenge) {
        new AlertDialog.Builder(this)
                .setTitle("Permanently Delete Challenge")
                .setMessage("Are you sure you want to permanently delete '" + challenge.getTitle() + "'? Prefer soft-deactivation if users have historical logs.")
                .setPositiveButton("Delete Permanently", (dialog, which) -> {
                    deleteChallenge(challenge.getId());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteChallenge(String challengeId) {
        pbChallenges.setVisibility(View.VISIBLE);
        FirestoreManager.getInstance().deleteChallenge(challengeId, new FirestoreManager.SimpleCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(ManageChallengesActivity.this, "Challenge deleted from Firestore!", Toast.LENGTH_SHORT).show();
                loadChallenges();
            }

            @Override
            public void onFailure(Exception e) {
                pbChallenges.setVisibility(View.GONE);
                Toast.makeText(ManageChallengesActivity.this, "Delete failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}