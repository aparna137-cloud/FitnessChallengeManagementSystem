package com.example.fitnesschallenge.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnesschallenge.R;
import com.example.fitnesschallenge.adapters.ActivityAdapter;
import com.example.fitnesschallenge.firebase.FirebaseAuthManager;
import com.example.fitnesschallenge.firebase.FirestoreManager;
import com.example.fitnesschallenge.models.Activity;
import com.example.fitnesschallenge.models.Challenge;
import com.example.fitnesschallenge.models.Participant;
import com.example.fitnesschallenge.utils.Constants;
import com.example.fitnesschallenge.utils.DateUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ChallengeDetailsActivity extends AppCompatActivity {

    private Challenge challenge;
    private MaterialToolbar toolbar;
    private TextView tvActivityType, tvPoints, tvTitle, tvDescription, tvTarget, tvDuration;
    private LinearLayout layoutUserProgress;
    private TextView tvProgressPercent, tvProgressValues;
    private ProgressBar pbDetailProgress, pbDetailAction;
    private MaterialButton btnJoinChallenge, btnLogActivityForThis;
    private RecyclerView rvChallengeActivities;
    private TextView tvEmptyChallengeActivities;
    private ActivityAdapter activityAdapter;

    private boolean isAlreadyJoined = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_details);

        challenge = (Challenge) getIntent().getSerializableExtra(Constants.EXTRA_CHALLENGE);
        if (challenge == null) {
            Toast.makeText(this, "Challenge not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        populateChallengeDetails();
        setupRecyclerView();
        setupListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbarChallengeDetails);
        tvActivityType = findViewById(R.id.tvDetailActivityType);
        tvPoints = findViewById(R.id.tvDetailPoints);
        tvTitle = findViewById(R.id.tvDetailTitle);
        tvDescription = findViewById(R.id.tvDetailDescription);
        tvTarget = findViewById(R.id.tvDetailTarget);
        tvDuration = findViewById(R.id.tvDetailDuration);
        layoutUserProgress = findViewById(R.id.layoutUserProgress);
        tvProgressPercent = findViewById(R.id.tvDetailProgressPercent);
        tvProgressValues = findViewById(R.id.tvDetailProgressValues);
        pbDetailProgress = findViewById(R.id.pbDetailProgress);
        pbDetailAction = findViewById(R.id.pbDetailAction);
        btnJoinChallenge = findViewById(R.id.btnJoinChallenge);
        btnLogActivityForThis = findViewById(R.id.btnLogActivityForThis);
        rvChallengeActivities = findViewById(R.id.rvChallengeActivities);
        tvEmptyChallengeActivities = findViewById(R.id.tvEmptyChallengeActivities);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void populateChallengeDetails() {
        tvActivityType.setText(challenge.getActivityType());
        tvPoints.setText(challenge.getPoints() + " pts");
        tvTitle.setText(challenge.getTitle());
        tvDescription.setText(challenge.getDescription());
        tvTarget.setText(challenge.getTargetFormatted());

        String start = DateUtils.formatDisplayDate(challenge.getStartDate());
        String end = DateUtils.formatDisplayDate(challenge.getEndDate());
        tvDuration.setText(start + " - " + end);
    }

    private void setupRecyclerView() {
        activityAdapter = new ActivityAdapter(this);
        rvChallengeActivities.setLayoutManager(new LinearLayoutManager(this));
        rvChallengeActivities.setAdapter(activityAdapter);
    }

    private void setupListeners() {
        btnJoinChallenge.setOnClickListener(v -> {
            if (!isAlreadyJoined) {
                performJoinChallenge();
            }
        });

        btnLogActivityForThis.setOnClickListener(v -> {
            Intent intent = new Intent(ChallengeDetailsActivity.this, AddActivityActivity.class);
            intent.putExtra(Constants.EXTRA_CHALLENGE_ID, challenge.getId());
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkParticipationStatus();
        loadChallengeActivities();
    }

    private void checkParticipationStatus() {
        String uid = FirebaseAuthManager.getInstance().getCurrentUserId();
        if (uid == null) return;

        pbDetailAction.setVisibility(View.VISIBLE);
        btnJoinChallenge.setEnabled(false);

        FirestoreManager.getInstance().getMyChallenges(uid, new FirestoreManager.FirestoreCallback<List<Participant>>() {
            @Override
            public void onSuccess(List<Participant> participants) {
                pbDetailAction.setVisibility(View.GONE);
                Participant currentParticipant = null;

                for (Participant p : participants) {
                    if (challenge.getId().equals(p.getChallengeId())) {
                        currentParticipant = p;
                        break;
                    }
                }

                if (currentParticipant != null) {
                    isAlreadyJoined = true;
                    btnJoinChallenge.setText(R.string.btn_already_joined);
                    btnJoinChallenge.setEnabled(false);
                    btnJoinChallenge.setBackgroundColor(ContextCompat.getColor(ChallengeDetailsActivity.this, R.color.text_secondary));

                    layoutUserProgress.setVisibility(View.VISIBLE);
                    btnLogActivityForThis.setVisibility(View.VISIBLE);

                    int pct = currentParticipant.getProgressPercentage();
                    tvProgressPercent.setText(pct + "%");
                    pbDetailProgress.setProgress(pct);

                    String compStr = currentParticipant.getCompletedValue() == (long) currentParticipant.getCompletedValue() ?
                            String.valueOf((long) currentParticipant.getCompletedValue()) : String.format("%.1f", currentParticipant.getCompletedValue());
                    String targStr = currentParticipant.getTargetValue() == (long) currentParticipant.getTargetValue() ?
                            String.valueOf((long) currentParticipant.getTargetValue()) : String.format("%.1f", currentParticipant.getTargetValue());

                    tvProgressValues.setText(String.format("Completed %s of %s %s • Earned %d pts",
                            compStr, targStr, currentParticipant.getUnit(), currentParticipant.getTotalPoints()));
                } else {
                    isAlreadyJoined = false;
                    btnJoinChallenge.setText(R.string.btn_join_challenge);
                    btnJoinChallenge.setEnabled(true);
                    btnJoinChallenge.setBackgroundColor(ContextCompat.getColor(ChallengeDetailsActivity.this, R.color.primary));
                    layoutUserProgress.setVisibility(View.GONE);
                    btnLogActivityForThis.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Exception e) {
                pbDetailAction.setVisibility(View.GONE);
                btnJoinChallenge.setEnabled(true);
            }
        });
    }

    private void performJoinChallenge() {
        String uid = FirebaseAuthManager.getInstance().getCurrentUserId();
        if (uid == null) return;

        pbDetailAction.setVisibility(View.VISIBLE);
        btnJoinChallenge.setEnabled(false);

        FirestoreManager.getInstance().joinChallenge(uid, challenge.getId(), new FirestoreManager.FirestoreCallback<String>() {
            @Override
            public void onSuccess(String participantDocId) {
                pbDetailAction.setVisibility(View.GONE);
                Toast.makeText(ChallengeDetailsActivity.this, "Successfully joined challenge in Firestore!", Toast.LENGTH_SHORT).show();
                checkParticipationStatus();
            }

            @Override
            public void onFailure(Exception e) {
                pbDetailAction.setVisibility(View.GONE);
                btnJoinChallenge.setEnabled(true);
                Toast.makeText(ChallengeDetailsActivity.this, "Failed to join: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadChallengeActivities() {
        String uid = FirebaseAuthManager.getInstance().getCurrentUserId();
        if (uid == null) return;

        FirestoreManager.getInstance().getUserActivitiesForChallenge(uid, challenge.getId(), new FirestoreManager.FirestoreCallback<List<Activity>>() {
            @Override
            public void onSuccess(List<Activity> activities) {
                if (activities == null || activities.isEmpty()) {
                    tvEmptyChallengeActivities.setVisibility(View.VISIBLE);
                    rvChallengeActivities.setVisibility(View.GONE);
                } else {
                    tvEmptyChallengeActivities.setVisibility(View.GONE);
                    rvChallengeActivities.setVisibility(View.VISIBLE);
                    activityAdapter.setActivities(activities);
                }
            }

            @Override
            public void onFailure(Exception e) {
                tvEmptyChallengeActivities.setVisibility(View.VISIBLE);
            }
        });
    }
}
