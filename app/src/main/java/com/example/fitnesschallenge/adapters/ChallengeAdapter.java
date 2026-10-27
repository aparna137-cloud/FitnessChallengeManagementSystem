package com.example.fitnesschallenge.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnesschallenge.R;
import com.example.fitnesschallenge.models.Challenge;
import com.example.fitnesschallenge.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.ChallengeViewHolder> {

    public interface OnChallengeClickListener {
        void onChallengeClick(Challenge challenge);
        void onEditClick(Challenge challenge);
        void onDeleteClick(Challenge challenge);
    }

    private final Context context;
    private List<Challenge> challengeList = new ArrayList<>();
    private final boolean isAdminMode;
    private final OnChallengeClickListener listener;

    public ChallengeAdapter(Context context, boolean isAdminMode, OnChallengeClickListener listener) {
        this.context = context;
        this.isAdminMode = isAdminMode;
        this.listener = listener;
    }

    public void setChallenges(List<Challenge> challenges) {
        this.challengeList = challenges != null ? challenges : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChallengeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_challenge, parent, false);
        return new ChallengeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengeViewHolder holder, int position) {
        Challenge challenge = challengeList.get(position);

        holder.tvTitle.setText(challenge.getTitle());
        holder.tvDescription.setText(challenge.getDescription());
        holder.tvActivityType.setText(challenge.getActivityType());
        holder.tvPoints.setText(challenge.getPoints() + " pts");
        holder.tvTarget.setText(challenge.getTargetFormatted());
        holder.tvEndDate.setText(DateUtils.formatDisplayDate(challenge.getEndDate()));

        if (isAdminMode) {
            holder.layoutAdminActions.setVisibility(View.VISIBLE);
            holder.btnEdit.setOnClickListener(v -> {
                if (listener != null) listener.onEditClick(challenge);
            });
            holder.btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDeleteClick(challenge);
            });
        } else {
            holder.layoutAdminActions.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onChallengeClick(challenge);
        });
    }

    @Override
    public int getItemCount() {
        return challengeList.size();
    }

    static class ChallengeViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvActivityType, tvPoints, tvTarget, tvEndDate;
        LinearLayout layoutAdminActions;
        ImageButton btnEdit, btnDelete;

        public ChallengeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvChallengeTitle);
            tvDescription = itemView.findViewById(R.id.tvChallengeDescription);
            tvActivityType = itemView.findViewById(R.id.tvActivityTypeBadge);
            tvPoints = itemView.findViewById(R.id.tvPointsBadge);
            tvTarget = itemView.findViewById(R.id.tvTargetValue);
            tvEndDate = itemView.findViewById(R.id.tvEndDate);
            layoutAdminActions = itemView.findViewById(R.id.layoutAdminActions);
            btnEdit = itemView.findViewById(R.id.btnEditChallenge);
            btnDelete = itemView.findViewById(R.id.btnDeleteChallenge);
        }
    }
}
