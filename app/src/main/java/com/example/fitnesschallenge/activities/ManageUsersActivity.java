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

import com.example.fitnesschallenge.R;
import com.example.fitnesschallenge.adapters.UserAdapter;
import com.example.fitnesschallenge.firebase.FirebaseAuthManager;
import com.example.fitnesschallenge.firebase.FirestoreManager;
import com.example.fitnesschallenge.models.User;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class ManageUsersActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextInputEditText etSearchUser;
    private RecyclerView rvUsers;
    private ProgressBar pbUsers;
    private TextView tvEmptyUsers;
    private UserAdapter adapter;

    private List<User> allUsers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupSearch();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbarManageUsers);
        etSearchUser = findViewById(R.id.etSearchUser);
        rvUsers = findViewById(R.id.rvManageUsers);
        pbUsers = findViewById(R.id.pbManageUsers);
        tvEmptyUsers = findViewById(R.id.tvEmptyManageUsers);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new UserAdapter(this, user -> {
            Intent intent = new Intent(ManageUsersActivity.this, UserDetailsActivity.class);
            intent.putExtra(UserDetailsActivity.EXTRA_USER, user);
            startActivity(intent);
        });

        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setAdapter(adapter);
    }

    private void setupSearch() {
        if (etSearchUser != null) {
            etSearchUser.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterUsers(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        verifyAdminAndLoadUsers();
    }

    private void verifyAdminAndLoadUsers() {
        String currentUid = FirebaseAuthManager.getInstance().getCurrentUserId();
        if (currentUid == null) {
            finish();
            return;
        }

        FirestoreManager.getInstance().getUser(currentUid, new FirestoreManager.FirestoreCallback<User>() {
            @Override
            public void onSuccess(User admin) {
                if (!admin.isAdmin()) {
                    Toast.makeText(ManageUsersActivity.this, "Unauthorized access", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                loadUsers();
            }

            @Override
            public void onFailure(Exception e) {
                finish();
            }
        });
    }

    private void loadUsers() {
        pbUsers.setVisibility(View.VISIBLE);
        tvEmptyUsers.setVisibility(View.GONE);

        FirestoreManager.getInstance().getAllUsers(new FirestoreManager.FirestoreCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                pbUsers.setVisibility(View.GONE);
                allUsers = users != null ? users : new ArrayList<>();
                String query = etSearchUser != null && etSearchUser.getText() != null ? etSearchUser.getText().toString() : "";
                filterUsers(query);
            }

            @Override
            public void onFailure(Exception e) {
                pbUsers.setVisibility(View.GONE);
                tvEmptyUsers.setText("Error loading users: " + e.getMessage());
                tvEmptyUsers.setVisibility(View.VISIBLE);
            }
        });
    }

    private void filterUsers(String query) {
        String lowerQuery = query.toLowerCase().trim();
        List<User> filtered = new ArrayList<>();

        for (User u : allUsers) {
            boolean matchesName = u.getName().toLowerCase().contains(lowerQuery);
            boolean matchesEmail = u.getEmail().toLowerCase().contains(lowerQuery);

            if (lowerQuery.isEmpty() || matchesName || matchesEmail) {
                filtered.add(u);
            }
        }

        adapter.setUsers(filtered);

        if (filtered.isEmpty()) {
            tvEmptyUsers.setText("No users found matching query.");
            tvEmptyUsers.setVisibility(View.VISIBLE);
        } else {
            tvEmptyUsers.setVisibility(View.GONE);
        }
    }
}