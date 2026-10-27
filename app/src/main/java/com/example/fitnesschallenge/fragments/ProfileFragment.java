package com.example.fitnesschallenge.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fitnesschallenge.R;
import com.example.fitnesschallenge.activities.LoginActivity;
import com.example.fitnesschallenge.firebase.FirebaseAuthManager;
import com.example.fitnesschallenge.firebase.FirestoreManager;
import com.example.fitnesschallenge.models.User;
import com.example.fitnesschallenge.utils.SampleDataHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileFragment extends Fragment {

    private TextView tvAvatar, tvName, tvEmail;
    private TextView tvAge, tvGender, tvHeight, tvWeight, tvBMI, tvGoal;
    private MaterialButton btnEditProfile, btnSeedDemoData, btnLogout;

    private User currentUser;

    private static final String[] GOALS = {"Weight Loss", "Muscle Gain", "Improve Stamina", "Endurance & Speed", "General Fitness"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initViews(view);
        setupListeners();
        return view;
    }

    private void initViews(View view) {
        tvAvatar = view.findViewById(R.id.tvProfileAvatar);
        tvName = view.findViewById(R.id.tvProfileName);
        tvEmail = view.findViewById(R.id.tvProfileEmail);
        tvAge = view.findViewById(R.id.tvProfileAge);
        tvGender = view.findViewById(R.id.tvProfileGender);
        tvHeight = view.findViewById(R.id.tvProfileHeight);
        tvWeight = view.findViewById(R.id.tvProfileWeight);
        tvBMI = view.findViewById(R.id.tvProfileBMI);
        tvGoal = view.findViewById(R.id.tvProfileGoal);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnSeedDemoData = view.findViewById(R.id.btnSeedDemoData);
        btnLogout = view.findViewById(R.id.btnLogout);
    }

    private void setupListeners() {
        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());
        btnSeedDemoData.setOnClickListener(v -> seedDemoData());
        btnLogout.setOnClickListener(v -> confirmLogout());
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserProfile();
    }

    private void loadUserProfile() {
        String uid = FirebaseAuthManager.getInstance().getCurrentUserId();
        if (uid == null) return;

        FirestoreManager.getInstance().getUser(uid, new FirestoreManager.FirestoreCallback<User>() {
            @Override
            public void onSuccess(User user) {
                if (!isAdded()) return;
                currentUser = user;
                populateProfileUI(user);
            }

            @Override
            public void onFailure(Exception e) {
            }
        });
    }

    private void populateProfileUI(User user) {
        tvName.setText(user.getName().isEmpty() ? "Fitness Member" : user.getName());
        tvEmail.setText(user.getEmail());

        String initials = "U";
        if (user.getName() != null && !user.getName().trim().isEmpty()) {
            String[] parts = user.getName().trim().split("\\s+");
            if (parts.length >= 2) {
                initials = ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
            } else {
                initials = ("" + parts[0].charAt(0)).toUpperCase();
            }
        }
        tvAvatar.setText(initials);

        tvAge.setText(user.getAge() + " yrs");
        tvGender.setText(user.getGender());
        tvHeight.setText(String.format("%.0f cm", user.getHeight()));
        tvWeight.setText(String.format("%.0f kg", user.getWeight()));
        tvBMI.setText(String.format("%.1f (%s)", user.calculateBMI(), user.getBMICategory()));
        tvGoal.setText(user.getFitnessGoal());
    }

    private void showEditProfileDialog() {
        if (currentUser == null) return;

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_profile, null);

        TextInputEditText etName = dialogView.findViewById(R.id.etEditName);
        TextInputEditText etAge = dialogView.findViewById(R.id.etEditAge);
        TextInputEditText etHeight = dialogView.findViewById(R.id.etEditHeight);
        TextInputEditText etWeight = dialogView.findViewById(R.id.etEditWeight);
        AutoCompleteTextView actvGoal = dialogView.findViewById(R.id.actvEditGoal);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancelEdit);
        MaterialButton btnSave = dialogView.findViewById(R.id.btnSaveEdit);

        etName.setText(currentUser.getName());
        etAge.setText(String.valueOf(currentUser.getAge()));
        etHeight.setText(String.valueOf(currentUser.getHeight()));
        etWeight.setText(String.valueOf(currentUser.getWeight()));

        ArrayAdapter<String> goalAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, GOALS);
        actvGoal.setAdapter(goalAdapter);
        actvGoal.setText(currentUser.getFitnessGoal(), false);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String name = etName.getText() != null ? etName.getText().toString().trim() : "";
            String ageStr = etAge.getText() != null ? etAge.getText().toString().trim() : "";
            String heightStr = etHeight.getText() != null ? etHeight.getText().toString().trim() : "";
            String weightStr = etWeight.getText() != null ? etWeight.getText().toString().trim() : "";
            String goal = actvGoal.getText().toString();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(ageStr) || TextUtils.isEmpty(heightStr) || TextUtils.isEmpty(weightStr)) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            currentUser.setName(name);
            currentUser.setAge(Integer.parseInt(ageStr));
            currentUser.setHeight(Double.parseDouble(heightStr));
            currentUser.setWeight(Double.parseDouble(weightStr));
            currentUser.setFitnessGoal(goal);

            btnSave.setEnabled(false);

            FirestoreManager.getInstance().updateUser(currentUser, new FirestoreManager.SimpleCallback() {
                @Override
                public void onSuccess() {
                    dialog.dismiss();
                    Toast.makeText(requireContext(), "Profile updated in Firestore!", Toast.LENGTH_SHORT).show();
                    populateProfileUI(currentUser);
                }

                @Override
                public void onFailure(Exception e) {
                    btnSave.setEnabled(true);
                    Toast.makeText(requireContext(), "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        dialog.show();
    }

    private void seedDemoData() {
        btnSeedDemoData.setEnabled(false);
        Toast.makeText(requireContext(), "Seeding sample challenges into Firestore...", Toast.LENGTH_SHORT).show();

        SampleDataHelper.seedSampleChallenges(FirestoreManager.getInstance(), new SampleDataHelper.OnSeedCompleteListener() {
            @Override
            public void onSuccess(String message) {
                if (isAdded()) {
                    btnSeedDemoData.setEnabled(true);
                    Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String error) {
                if (isAdded()) {
                    btnSeedDemoData.setEnabled(true);
                    Snackbar.make(requireView(), error, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void confirmLogout() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.confirm_logout_title)
                .setMessage(R.string.confirm_logout_msg)
                .setPositiveButton("Logout", (dialog, which) -> {
                    FirebaseAuthManager.getInstance().logout();
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
