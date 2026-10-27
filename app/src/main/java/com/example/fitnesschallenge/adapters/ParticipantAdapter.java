package com.example.fitnesschallenge.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnesschallenge.R;
import com.example.fitnesschallenge.models.Participant;

import java.util.ArrayList;
import java.util.List;

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.ViewHolder> {

    private final Context context;
    private List<Participant> participantList = new ArrayList<>();

    public ParticipantAdapter(Context context) {
        this.context = context;
    }

    public void setParticipants(List<Participant> participants) {
        this.participantList = participants != null ? participants : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_participant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Participant p = participantList.get(position);

        holder.tvName.setText(p.getUserName() != null && !p.getUserName().isEmpty() ? p.getUserName() : "User ID: " + p.getUserId().substring(0, Math.min(8, p.getUserId().length())));

        String compStr = p.getCompletedValue() == (long) p.getCompletedValue() ?
                String.valueOf((long) p.getCompletedValue()) : String.format("%.1f", p.getCompletedValue());
        String targStr = p.getTargetValue() == (long) p.getTargetValue() ?
                String.valueOf((long) p.getTargetValue()) : String.format("%.1f", p.getTargetValue());

        holder.tvProgress.setText(String.format("Progress: %s / %s %s (%d%%)", compStr, targStr, p.getUnit(), p.getProgressPercentage()));
        holder.tvStatus.setText(p.getStatus());
        holder.tvPoints.setText(p.getTotalPoints() + " pts");
    }

    @Override
    public int getItemCount() {
        return participantList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvProgress, tvStatus, tvPoints;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvParticipantName);
            tvProgress = itemView.findViewById(R.id.tvParticipantProgress);
            tvStatus = itemView.findViewById(R.id.tvParticipantStatus);
            tvPoints = itemView.findViewById(R.id.tvParticipantPoints);
        }
    }
}
