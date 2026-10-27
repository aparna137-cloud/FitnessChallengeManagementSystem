package com.example.fitnesschallenge.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnesschallenge.R;
import com.example.fitnesschallenge.firebase.FirestoreManager;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private final Context context;
    private List<FirestoreManager.LeaderboardEntry> entryList = new ArrayList<>();
    private final String currentUserId;

    public LeaderboardAdapter(Context context, String currentUserId) {
        this.context = context;
        this.currentUserId = currentUserId;
    }

    public void setEntries(List<FirestoreManager.LeaderboardEntry> entries) {
        this.entryList = entries != null ? entries : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_leaderboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FirestoreManager.LeaderboardEntry entry = entryList.get(position);

        holder.tvRank.setText("#" + entry.rank);
        holder.tvName.setText(entry.userName);

        String initials = "U";
        if (entry.userName != null && !entry.userName.trim().isEmpty()) {
            String[] parts = entry.userName.trim().split("\\s+");
            if (parts.length >= 2) {
                initials = ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
            } else {
                initials = ("" + parts[0].charAt(0)).toUpperCase();
            }
        }
        holder.tvAvatar.setText(initials);

        holder.tvSubtext.setText(entry.completedChallenges + " Challenges completed • " + entry.goal);
        holder.tvPoints.setText(entry.totalPoints + " pts");

        // Top 3 medals styling
        if (entry.rank == 1) {
            holder.ivRankBadge.setVisibility(View.VISIBLE);
            holder.ivRankBadge.setImageResource(R.drawable.bg_badge_gold);
            holder.tvRank.setTextColor(Color.WHITE);
        } else if (entry.rank == 2) {
            holder.ivRankBadge.setVisibility(View.VISIBLE);
            holder.ivRankBadge.setImageResource(R.drawable.bg_badge_silver);
            holder.tvRank.setTextColor(Color.WHITE);
        } else if (entry.rank == 3) {
            holder.ivRankBadge.setVisibility(View.VISIBLE);
            holder.ivRankBadge.setImageResource(R.drawable.bg_badge_bronze);
            holder.tvRank.setTextColor(Color.WHITE);
        } else {
            holder.ivRankBadge.setVisibility(View.GONE);
            holder.tvRank.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
        }

        // Highlight current logged-in user's row
        boolean isCurrentUser = currentUserId != null && currentUserId.equals(entry.userId);
        if (isCurrentUser) {
            holder.tvYouTag.setVisibility(View.VISIBLE);
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.highlight_user_bg));
            holder.cardView.setStrokeColor(ContextCompat.getColor(context, R.color.highlight_user_border));
            holder.cardView.setStrokeWidth(3);
        } else {
            holder.tvYouTag.setVisibility(View.GONE);
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_background));
            holder.cardView.setStrokeColor(ContextCompat.getColor(context, R.color.divider));
            holder.cardView.setStrokeWidth(1);
        }
    }

    @Override
    public int getItemCount() {
        return entryList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        ImageView ivRankBadge;
        TextView tvRank, tvAvatar, tvName, tvYouTag, tvSubtext, tvPoints;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardLeaderboard);
            ivRankBadge = itemView.findViewById(R.id.ivRankBadge);
            tvRank = itemView.findViewById(R.id.tvRankNumber);
            tvAvatar = itemView.findViewById(R.id.tvUserAvatar);
            tvName = itemView.findViewById(R.id.tvLeaderboardName);
            tvYouTag = itemView.findViewById(R.id.tvYouTag);
            tvSubtext = itemView.findViewById(R.id.tvLeaderboardSubtext);
            tvPoints = itemView.findViewById(R.id.tvLeaderboardPoints);
        }
    }
}
