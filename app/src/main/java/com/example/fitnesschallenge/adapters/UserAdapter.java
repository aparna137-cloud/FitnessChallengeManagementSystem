package com.example.fitnesschallenge.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnesschallenge.R;
import com.example.fitnesschallenge.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    private final Context context;
    private final OnUserClickListener listener;
    private List<User> userList = new ArrayList<>();

    public UserAdapter(Context context, OnUserClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setUsers(List<User> users) {
        this.userList = users != null ? users : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);

        holder.tvName.setText(user.getName().isEmpty() ? "Anonymous User" : user.getName());
        holder.tvEmail.setText(user.getEmail());

        if (user.isSuspended()) {
            holder.tvRoleBadge.setText("SUSPENDED");
            holder.tvRoleBadge.setTextColor(ContextCompat.getColor(context, R.color.status_error));
        } else {
            holder.tvRoleBadge.setText(user.getRole().toUpperCase(Locale.ROOT));
            holder.tvRoleBadge.setTextColor(ContextCompat.getColor(context, R.color.primary));
        }

        String metrics = String.format(Locale.getDefault(), "Age: %d • %.0f cm / %.0f kg • BMI: %.1f (%s)",
                user.getAge(), user.getHeight(), user.getWeight(), user.calculateBMI(), user.getBMICategory());
        holder.tvMetrics.setText(metrics);

        holder.tvGoal.setText("Goal: " + user.getFitnessGoal());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUserClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvRoleBadge, tvEmail, tvMetrics, tvGoal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvUserName);
            tvRoleBadge = itemView.findViewById(R.id.tvUserRoleBadge);
            tvEmail = itemView.findViewById(R.id.tvUserEmail);
            tvMetrics = itemView.findViewById(R.id.tvUserMetrics);
            tvGoal = itemView.findViewById(R.id.tvUserGoal);
        }
    }
}