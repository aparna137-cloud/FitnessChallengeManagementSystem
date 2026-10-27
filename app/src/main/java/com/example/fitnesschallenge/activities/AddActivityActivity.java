package com.example.fitnesschallenge.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitnesschallenge.R;
import com.example.fitnesschallenge.firebase.FirebaseAuthManager;
import com.example.fitnesschallenge.firebase.FirestoreManager;
import com.example.fitnesschallenge.models.Activity;
import com.example.fitnesschallenge.models.Participant;
import com.example.fitnesschallenge.utils.Constants;
import com.example.fitnesschallenge.utils.DateUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddActivityActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private AutoCompleteTextView actvSelectChallenge;
    private TextInputLayout tilAchievementValue;
    private TextInputEditText etActivityDate, etAchievementValue, etNotes;
    private TextView tvLivePointsPreview;
    private ProgressBar pbSubmit;
    private MaterialButton btnSubmit;

    private List<Participant> joinedParticipants = new ArrayList<>();
    private Participant selectedParticipant;
    private String preselectedChallengeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_activity);

        preselectedChallengeId = getIntent().getStringExtra(Constants.EXTRA_CHALLENGE_ID);

        initViews();
        setupToolbar();
        setupDatePicker();
        setupListeners();
        loadJoinedChallenges();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbarAddActivity);
        actvSelectChallenge = findViewById(R.id.actvSelectChallenge);
        tilAchievementValue = findViewById(R.id.tilAchievementValue);
        etActivityDate = findViewById(R.id.etActivityDate);
        etAchievementValue = findViewById(R.id.etAchievementValue);
        etNotes = findViewById(R.id.etNotes);
        tvLivePointsPreview = findViewById(R.id.tvLivePointsPreview);
        pbSubmit = findViewById(R.id.pbSubmitActivity);
        btnSubmit = findViewById(R.id.btnSubmitActivity);

        etActivityDate.setText(DateUtils.getTodayDate());
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupDatePicker() {
        etActivityDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePicker = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        String formatted = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        etActivityDate.setText(formatted);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePicker.show();
        });
    }

    private void setupListeners() {
        actvSelectChallenge.setOnItemClickListener((parent, view, position, id) -> {
            if (position < joinedParticipants.size()) {
                selectedParticipant = joinedParticipants.get(position);
                tilAchievementValue.setHint("Achievement Value (" + selectedParticipant.getUnit() + ")");
                updateLivePointsPreview();
            }
        });

        etAchievementValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                updateLivePointsPreview();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnSubmit.setOnClickListener(v -> performSubmitActivity());
    }

    private void updateLivePointsPreview() {
        String valStr = etAchievementValue.getText() != null ? etAchievementValue.getText().toString().trim() : "";
        double val = 0.0;
        try {
            if (!valStr.isEmpty()) val = Double.parseDouble(valStr);
        } catch (NumberFormatException ignored) {
        }

        String type = selectedParticipant != null ? selectedParticipant.getActivityType() : "Running";
        int pts = Constants.calculateActivityPoints(type, val);
        tvLivePointsPreview.setText("Points to earn: " + pts + " pts");
    }

    private void loadJoinedChallenges() {
        String uid = FirebaseAuthManager.getInstance().getCurrentUserId();
        if (uid == null) return;

        FirestoreManager.getInstance().getMyChallenges(uid, new FirestoreManager.FirestoreCallback<List<Participant>>() {
            @Override
            public void onSuccess(List<Participant> participants) {
                joinedParticipants = participants != null ? participants : new ArrayList<>();

                if (joinedParticipants.isEmpty()) {
                    Toast.makeText(AddActivityActivity.this, "You need to join a challenge first!", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }

                List<String> titles = new ArrayList<>();
                int preselectIndex = 0;

                for (int i = 0; i < joinedParticipants.size(); i++) {
                    Participant p = joinedParticipants.get(i);
                    titles.add(p.getChallengeTitle() + " (" + p.getActivityType() + ")");
                    if (preselectedChallengeId != null && preselectedChallengeId.equals(p.getChallengeId())) {
                        preselectIndex = i;
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(AddActivityActivity.this, android.R.layout.simple_dropdown_item_1line, titles);
                actvSelectChallenge.setAdapter(adapter);

                // Select default or preselected
                selectedParticipant = joinedParticipants.get(preselectIndex);
                actvSelectChallenge.setText(titles.get(preselectIndex), false);
                tilAchievementValue.setHint("Achievement Value (" + selectedParticipant.getUnit() + ")");
                updateLivePointsPreview();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(AddActivityActivity.this, "Error loading joined challenges", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performSubmitActivity() {
        if (selectedParticipant == null) {
            Toast.makeText(this, "Please select a challenge", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = etActivityDate.getText() != null ? etActivityDate.getText().toString().trim() : "";
        String valStr = etAchievementValue.getText() != null ? etAchievementValue.getText().toString().trim() : "";
        String notes = etNotes.getText() != null ? etNotes.getText().toString().trim() : "";

        if (TextUtils.isEmpty(valStr)) {
            etAchievementValue.setError("Achievement value is required");
            etAchievementValue.requestFocus();
            return;
        }

        double val;
        try {
            val = Double.parseDouble(valStr);
            if (val <= 0) {
                etAchievementValue.setError("Value must be greater than 0");
                return;
            }
        } catch (NumberFormatException e) {
            etAchievementValue.setError("Enter a valid number");
            return;
        }

        String uid = FirebaseAuthManager.getInstance().getCurrentUserId();
        if (uid == null) return;

        setLoading(true);

        Activity activity = new Activity(
                null,
                uid,
                selectedParticipant.getChallengeId(),
                selectedParticipant.getActivityType(),
                date,
                val,
                notes,
                0,
                System.currentTimeMillis()
        );

        FirestoreManager.getInstance().addActivity(activity, new FirestoreManager.FirestoreCallback<Integer>() {
            @Override
            public void onSuccess(Integer earnedPoints) {
                setLoading(false);
                Toast.makeText(AddActivityActivity.this, "Activity logged to Firestore! +" + earnedPoints + " pts earned!", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                setLoading(false);
                Toast.makeText(AddActivityActivity.this, "Failed to log activity: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        pbSubmit.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSubmit.setEnabled(!loading);
    }
}
