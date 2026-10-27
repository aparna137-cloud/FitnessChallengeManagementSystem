package com.example.fitnesschallenge.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitnesschallenge.R;
import com.example.fitnesschallenge.firebase.FirebaseAuthManager;
import com.example.fitnesschallenge.firebase.FirestoreManager;
import com.example.fitnesschallenge.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private ProgressBar pbLogin;
    private MaterialButton btnLogin;
    private TextView tvLoginTitle, tvLoginSubtitle, tvGoToRegister, tvDemoHint, tvForgotPassword;
    private MaterialButtonToggleGroup toggleGroupRole;

    private boolean isAdminPortalMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupListeners();
    }

    private void initViews() {
        tvLoginTitle = findViewById(R.id.tvLoginTitle);
        tvLoginSubtitle = findViewById(R.id.tvLoginSubtitle);
        toggleGroupRole = findViewById(R.id.toggleGroupRole);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        pbLogin = findViewById(R.id.pbLogin);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);
        tvDemoHint = findViewById(R.id.tvDemoHint);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> performLogin());

        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        if (tvForgotPassword != null) {
            tvForgotPassword.setOnClickListener(v -> showForgotPasswordDialog());
        }

        if (toggleGroupRole != null) {
            toggleGroupRole.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
                if (isChecked) {
                    if (checkedId == R.id.btnRoleAdmin) {
                        isAdminPortalMode = true;
                        if (tvLoginTitle != null) tvLoginTitle.setText("Admin Portal");
                        if (tvLoginSubtitle != null) tvLoginSubtitle.setText("Sign in with your administrator credentials");
                        if (btnLogin != null) btnLogin.setText("Sign In As Admin");
                        etEmail.setText("admin@fitness.com");
                        etPassword.setText("admin123");
                    } else {
                        isAdminPortalMode = false;
                        if (tvLoginTitle != null) tvLoginTitle.setText(R.string.title_login);
                        if (tvLoginSubtitle != null) tvLoginSubtitle.setText(R.string.subtitle_login);
                        if (btnLogin != null) btnLogin.setText(R.string.btn_login);
                        etEmail.setText("");
                        etPassword.setText("");
                    }
                    clearErrors();
                }
            });
        }

        // Tap demo credentials card to auto-fill admin credentials
        if (tvDemoHint != null) {
            tvDemoHint.setOnClickListener(v -> {
                if (toggleGroupRole != null) {
                    toggleGroupRole.check(R.id.btnRoleAdmin);
                } else {
                    etEmail.setText("admin@fitness.com");
                    etPassword.setText("admin123");
                }
                clearErrors();
                Toast.makeText(this, "Demo admin credentials filled!", Toast.LENGTH_SHORT).show();
            });
        }

        // Trigger login on soft keyboard 'Done' action
        etPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performLogin();
                return true;
            }
            return false;
        });

        // Clear error when user modifies input
        TextWatcher clearErrorWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearErrors();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        etEmail.addTextChangedListener(clearErrorWatcher);
        etPassword.addTextChangedListener(clearErrorWatcher);
    }

    private void showForgotPasswordDialog() {
        String currentEmail = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";

        TextInputLayout inputLayout = new TextInputLayout(this);
        inputLayout.setHint("Email Address");
        inputLayout.setPadding(40, 20, 40, 0);

        TextInputEditText input = new TextInputEditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        if (!TextUtils.isEmpty(currentEmail)) {
            input.setText(currentEmail);
        }
        inputLayout.addView(input);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Reset Password")
                .setMessage("Enter your registered account email to receive a password reset link.")
                .setView(inputLayout)
                .setPositiveButton("Send Reset Link", (dialog, which) -> {
                    String resetEmail = input.getText() != null ? input.getText().toString().trim() : "";
                    if (TextUtils.isEmpty(resetEmail) || !Patterns.EMAIL_ADDRESS.matcher(resetEmail).matches()) {
                        Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    FirebaseAuthManager.getInstance().sendPasswordResetEmail(resetEmail, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Password reset email sent to " + resetEmail, Toast.LENGTH_LONG).show();
                        } else {
                            String err = task.getException() != null ? task.getException().getMessage() : "Failed to send reset email";
                            Toast.makeText(this, "Error: " + err, Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void clearErrors() {
        if (tilEmail != null) tilEmail.setError(null);
        if (tilPassword != null) tilPassword.setError(null);
    }

    private void performLogin() {
        clearErrors();

        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Please enter a valid email address");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        setLoading(true);

        FirebaseAuthManager.getInstance().loginUser(email, password, task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().getUser() != null) {
                String uid = task.getResult().getUser().getUid();

                // Check user role in Firestore
                FirestoreManager.getInstance().getUser(uid, new FirestoreManager.FirestoreCallback<User>() {
                    @Override
                    public void onSuccess(User user) {
                        setLoading(false);

                        if (user.isSuspended()) {
                            FirebaseAuthManager.getInstance().logout();
                            Snackbar.make(findViewById(android.R.id.content), "Account Suspended: Contact administrator.", Snackbar.LENGTH_LONG).show();
                            return;
                        }

                        if (isAdminPortalMode) {
                            if (user.isAdmin()) {
                                Toast.makeText(LoginActivity.this, "Admin Login Successful! Welcome " + user.getName(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                                finishAffinity();
                            } else {
                                FirebaseAuthManager.getInstance().logout();
                                Snackbar.make(findViewById(android.R.id.content), "Access Denied: Account '" + email + "' does not have Admin privileges.", Snackbar.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Welcome " + user.getName() + "!", Toast.LENGTH_SHORT).show();
                            if (user.isAdmin()) {
                                startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                            } else {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            }
                            finishAffinity();
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // Auto-provision profile document in Firestore if missing
                        boolean isDefaultAdmin = "admin@fitness.com".equalsIgnoreCase(email) || isAdminPortalMode;
                        String role = isDefaultAdmin ? "admin" : "user";
                        String name = isDefaultAdmin ? "System Administrator" : "Fitness Member";

                        User newProfile = new User(
                                uid,
                                name,
                                email,
                                role,
                                25,
                                "Male",
                                175.0,
                                70.0,
                                "General Fitness",
                                System.currentTimeMillis()
                        );

                        FirestoreManager.getInstance().saveUser(newProfile, new FirestoreManager.SimpleCallback() {
                            @Override
                            public void onSuccess() {
                                setLoading(false);
                                Toast.makeText(LoginActivity.this, "Profile initialized! Welcome " + name, Toast.LENGTH_SHORT).show();
                                if (newProfile.isAdmin()) {
                                    startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                                } else {
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                }
                                finishAffinity();
                            }

                            @Override
                            public void onFailure(Exception ex) {
                                setLoading(false);
                                Snackbar.make(findViewById(android.R.id.content), "Failed to initialize profile: " + ex.getMessage(), Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            } else {
                setLoading(false);
                String errorMsg = task.getException() != null ? task.getException().getMessage() : "Authentication failed";
                Snackbar.make(findViewById(android.R.id.content), errorMsg, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        pbLogin.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!loading);
        btnLogin.setText(loading ? "Signing in…" : (isAdminPortalMode ? "Sign In As Admin" : getString(R.string.btn_login)));
        tilEmail.setEnabled(!loading);
        tilPassword.setEnabled(!loading);
    }
}