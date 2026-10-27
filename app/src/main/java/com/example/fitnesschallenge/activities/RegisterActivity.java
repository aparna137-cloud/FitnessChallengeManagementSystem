package com.example.fitnesschallenge.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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
import com.example.fitnesschallenge.models.User;
import com.example.fitnesschallenge.utils.Constants;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPassword, etAge, etHeight, etWeight;
    private AutoCompleteTextView actvGender, actvGoal;
    private ProgressBar pbRegister;
    private MaterialButton btnRegister;
    private TextView tvGoToLogin;

    private static final String[] GENDERS = {"Male", "Female", "Other"};
    private static final String[] GOALS = {"Weight Loss", "Muscle Gain", "Improve Stamina", "Endurance & Speed", "General Fitness"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupDropdowns();
        setupListeners();
    }

    private void initViews() {
        etName = findViewById(R.id.etRegName);
        etEmail = findViewById(R.id.etRegEmail);
        etPassword = findViewById(R.id.etRegPassword);
        etAge = findViewById(R.id.etRegAge);
        etHeight = findViewById(R.id.etRegHeight);
        etWeight = findViewById(R.id.etRegWeight);
        actvGender = findViewById(R.id.actvRegGender);
        actvGoal = findViewById(R.id.actvRegGoal);
        pbRegister = findViewById(R.id.pbRegister);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);
    }

    private void setupDropdowns() {
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, GENDERS);
        actvGender.setAdapter(genderAdapter);
        actvGender.setText(GENDERS[0], false);

        ArrayAdapter<String> goalAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, GOALS);
        actvGoal.setAdapter(goalAdapter);
        actvGoal.setText(GOALS[4], false);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> performRegistration());
        tvGoToLogin.setOnClickListener(v -> finish());
    }

    private void performRegistration() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        String ageStr = etAge.getText() != null ? etAge.getText().toString().trim() : "";
        String heightStr = etHeight.getText() != null ? etHeight.getText().toString().trim() : "";
        String weightStr = etWeight.getText() != null ? etWeight.getText().toString().trim() : "";
        String gender = actvGender.getText().toString();
        String goal = actvGoal.getText().toString();

        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            etName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Valid email is required");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(ageStr)) {
            etAge.setError("Age is required");
            etAge.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(heightStr)) {
            etHeight.setError("Height is required");
            etHeight.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(weightStr)) {
            etWeight.setError("Weight is required");
            etWeight.requestFocus();
            return;
        }

        int age = Integer.parseInt(ageStr);
        double height = Double.parseDouble(heightStr);
        double weight = Double.parseDouble(weightStr);

        setLoading(true);

        FirebaseAuthManager.getInstance().registerUser(email, password, task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().getUser() != null) {
                String uid = task.getResult().getUser().getUid();

                // Build User document
                User newUser = new User(
                        uid,
                        name,
                        email,
                        Constants.ROLE_USER,
                        age,
                        gender,
                        height,
                        weight,
                        goal,
                        System.currentTimeMillis()
                );

                FirestoreManager.getInstance().saveUser(newUser, new FirestoreManager.SimpleCallback() {
                    @Override
                    public void onSuccess() {
                        setLoading(false);
                        Toast.makeText(RegisterActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finishAffinity();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        setLoading(false);
                        Snackbar.make(findViewById(android.R.id.content), "Saved auth but failed profile: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });

            } else {
                setLoading(false);
                String error = task.getException() != null ? task.getException().getMessage() : "Registration failed";
                Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        pbRegister.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!loading);
    }
}
