package com.example.fitnesschallenge.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitnesschallenge.R;
import com.example.fitnesschallenge.firebase.FirebaseAuthManager;
import com.example.fitnesschallenge.firebase.FirestoreManager;
import com.example.fitnesschallenge.models.Challenge;
import com.example.fitnesschallenge.utils.Constants;
import com.example.fitnesschallenge.utils.DateUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class AddEditChallengeActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextInputEditText etTitle, etDescription, etTargetValue, etUnit, etStartDate, etEndDate, etPoints;
    private AutoCompleteTextView actvActivityType, actvStatus;
    private ProgressBar pbSave;
    private MaterialButton btnSave;

    private Challenge editingChallenge;
    private boolean isEditMode = false;

    private static final String[] ACTIVITY_TYPES = {"Running", "Steps", "Cycling", "Workout", "Push-Ups"};
    private static final String[] STATUSES = {Constants.STATUS_ACTIVE, Constants.STATUS_COMPLETED};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_challenge);

        editingChallenge = (Challenge) getIntent().getSerializableExtra(Constants.EXTRA_CHALLENGE);
        isEditMode = (editingChallenge != null);

        initViews();
        setupToolbar();
        setupDropdowns();
        setupDatePickers();
        setupListeners();

        if (isEditMode) {
            populateFieldsForEdit();
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbarAddEditChallenge);
        etTitle = findViewById(R.id.etChallengeTitle);
        etDescription = findViewById(R.id.etChallengeDescription);
        actvActivityType = findViewById(R.id.actvActivityType);
        actvStatus = findViewById(R.id.actvChallengeStatus);
        etTargetValue = findViewById(R.id.etTargetValue);
        etUnit = findViewById(R.id.etUnit);
        etStartDate = findViewById(R.id.etStartDate);
        etEndDate = findViewById(R.id.etEndDate);
        etPoints = findViewById(R.id.etPoints);
        pbSave = findViewById(R.id.pbSaveChallenge);
        btnSave = findViewById(R.id.btnSaveChallenge);

        etStartDate.setText(DateUtils.getTodayDate());
        etEndDate.setText(DateUtils.getDateDaysFromNow(14));
    }

    private void setupToolbar() {
        toolbar.setTitle(isEditMode ? "Edit Challenge" : "Create Challenge");
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupDropdowns() {
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, ACTIVITY_TYPES);
        actvActivityType.setAdapter(typeAdapter);
        actvActivityType.setText(ACTIVITY_TYPES[0], false);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, STATUSES);
        actvStatus.setAdapter(statusAdapter);
        actvStatus.setText(STATUSES[0], false);

        // Auto-fill suggested unit when activity type changes
        actvActivityType.setOnItemClickListener((parent, view, position, id) -> {
            String selected = ACTIVITY_TYPES[position];
            if ("Running".equalsIgnoreCase(selected) || "Cycling".equalsIgnoreCase(selected)) {
                etUnit.setText("KM");
            } else if ("Steps".equalsIgnoreCase(selected)) {
                etUnit.setText("Steps");
            } else if ("Workout".equalsIgnoreCase(selected)) {
                etUnit.setText("Minutes");
            } else if ("Push-Ups".equalsIgnoreCase(selected)) {
                etUnit.setText("Reps");
            }
        });
        etUnit.setText("KM");
    }

    private void setupDatePickers() {
        etStartDate.setOnClickListener(v -> showDatePicker(etStartDate));
        etEndDate.setOnClickListener(v -> showDatePicker(etEndDate));
    }

    private void showDatePicker(TextInputEditText targetField) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String formatted = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    targetField.setText(formatted);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void populateFieldsForEdit() {
        etTitle.setText(editingChallenge.getTitle());
        etDescription.setText(editingChallenge.getDescription());
        actvActivityType.setText(editingChallenge.getActivityType(), false);
        actvStatus.setText(editingChallenge.getStatus(), false);

        String val = editingChallenge.getTargetValue() == (long) editingChallenge.getTargetValue() ?
                String.valueOf((long) editingChallenge.getTargetValue()) : String.valueOf(editingChallenge.getTargetValue());
        etTargetValue.setText(val);
        etUnit.setText(editingChallenge.getUnit());
        etStartDate.setText(editingChallenge.getStartDate());
        etEndDate.setText(editingChallenge.getEndDate());
        etPoints.setText(String.valueOf(editingChallenge.getPoints()));
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> performSaveChallenge());
    }

    private void performSaveChallenge() {
        String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        String desc = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";
        String type = actvActivityType.getText().toString().trim();
        String status = actvStatus.getText().toString().trim();
        String targetValStr = etTargetValue.getText() != null ? etTargetValue.getText().toString().trim() : "";
        String unit = etUnit.getText() != null ? etUnit.getText().toString().trim() : "";
        String start = etStartDate.getText() != null ? etStartDate.getText().toString().trim() : "";
        String end = etEndDate.getText() != null ? etEndDate.getText().toString().trim() : "";
        String pointsStr = etPoints.getText() != null ? etPoints.getText().toString().trim() : "";

        if (TextUtils.isEmpty(title)) {
            etTitle.setError("Title is required");
            etTitle.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(desc)) {
            etDescription.setError("Description is required");
            etDescription.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(targetValStr)) {
            etTargetValue.setError("Target value is required");
            etTargetValue.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(unit)) {
            etUnit.setError("Unit is required");
            etUnit.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(pointsStr)) {
            etPoints.setError("Points is required");
            etPoints.requestFocus();
            return;
        }

        double targetVal;
        int points;
        try {
            targetVal = Double.parseDouble(targetValStr);
            points = Integer.parseInt(pointsStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numeric values", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        String adminUid = FirebaseAuthManager.getInstance().getCurrentUserId();
        if (adminUid == null) adminUid = "admin";

        if (isEditMode) {
            editingChallenge.setTitle(title);
            editingChallenge.setDescription(desc);
            editingChallenge.setActivityType(type);
            editingChallenge.setStatus(status);
            editingChallenge.setTargetValue(targetVal);
            editingChallenge.setUnit(unit);
            editingChallenge.setStartDate(start);
            editingChallenge.setEndDate(end);
            editingChallenge.setPoints(points);

            FirestoreManager.getInstance().updateChallenge(editingChallenge, new FirestoreManager.SimpleCallback() {
                @Override
                public void onSuccess() {
                    setLoading(false);
                    Toast.makeText(AddEditChallengeActivity.this, "Challenge updated in Firestore!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailure(Exception e) {
                    setLoading(false);
                    Toast.makeText(AddEditChallengeActivity.this, "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Challenge newChallenge = new Challenge(
                    null,
                    title,
                    desc,
                    type,
                    targetVal,
                    unit,
                    start,
                    end,
                    points,
                    status,
                    adminUid,
                    System.currentTimeMillis()
            );

            FirestoreManager.getInstance().createChallenge(newChallenge, new FirestoreManager.FirestoreCallback<String>() {
                @Override
                public void onSuccess(String docId) {
                    setLoading(false);
                    Toast.makeText(AddEditChallengeActivity.this, "Challenge created in Firestore!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailure(Exception e) {
                    setLoading(false);
                    Toast.makeText(AddEditChallengeActivity.this, "Creation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void setLoading(boolean loading) {
        pbSave.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!loading);
    }
}
