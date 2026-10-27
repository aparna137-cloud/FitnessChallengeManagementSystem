package com.example.fitnesschallenge.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnesschallenge.R;
import com.example.fitnesschallenge.models.Participant;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ParticipantChallengeAdapter extends RecyclerView.Adapter<ParticipantChallengeAdapter.ViewHolder> {

    public interface OnParticipantActionListener {
        void onChallengeClick(Participant participant);
        void onLogActivityClick(Participant participant);
    }

    private final Context context;
    private List<Participant> participantList = new ArrayList<>();
    private final OnParticipantActionListener listener;

    public ParticipantChallengeAdapter(Context context, OnParticipantActionListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setParticipants(List<Participant> participants) {
        this.participantList = participants != null ? participants : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_challenge, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Participant p = participantList.get(position);

        holder.tvTitle.setText(p.getChallengeTitle().isEmpty() ? "Challenge" : p.getChallengeTitle());
        holder.tvActivityType.setText(p.getActivityType().isEmpty() ? "Fitness" : p.getActivityType());
        holder.tvStatus.setText(p.getStatus());

        String targetStr = p.getTargetValue() == (long) p.getTargetValue() ?
                String.valueOf((long) p.getTargetValue()) : String.format("%.1f", p.getTargetValue());
        String completedStr = p.getCompletedValue() == (long) p.getCompletedValue() ?
                String.valueOf((long) p.getCompletedValue()) : String.format("%.1f", p.getCompletedValue());

        holder.tvProgressFraction.setText(completedStr + " / " + targetStr + " " + p.getUnit());

        int pct = p.getProgressPercentage();
        holder.tvProgressPercent.setText(pct + "%");
        holder.progressBar.setProgress(pct);

        holder.tvPointsEarned.setText("Earned: " + p.getTotalPoints() + " pts");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onChallengeClick(p);
        });

        holder.btnLogActivity.setOnClickListener(v -> {
            if (listener != null) listener.onLogActivityClick(p);
        });
    }

    @Override
    public int getItemCount() {
        return participantList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvActivityType, tvStatus, tvProgressFraction, tvProgressPercent, tvPointsEarned;
        ProgressBar progressBar;
        MaterialButton btnLogActivity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvMyChallengeTitle);
            tvActivityType = itemView.findViewById(R.id.tvMyActivityTypeBadge);
            tvStatus = itemView.findViewById(R.id.tvMyStatusBadge);
            tvProgressFraction = itemView.findViewById(R.id.tvMyProgressFraction);
            tvProgressPercent = itemView.findViewById(R.id.tvMyProgressPercent);
            tvPointsEarned = itemView.findViewById(R.id.tvMyTotalPointsEarned);
            progressBar = itemView.findViewById(R.id.progressBarMyChallenge);
            btnLogActivity = itemView.findViewById(R.id.btnLogActivityQuick);
        }
    }
}
