package com.example.fitnesschallenge.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnesschallenge.R;
import com.example.fitnesschallenge.models.Activity;
import com.example.fitnesschallenge.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {

    private final Context context;
    private List<Activity> activityList = new ArrayList<>();

    public ActivityAdapter(Context context) {
        this.context = context;
    }

    public void setActivities(List<Activity> activities) {
        this.activityList = activities != null ? activities : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_activity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Activity act = activityList.get(position);

        holder.tvTypeTitle.setText(act.getActivityType() + " Workout");

        String dateFormatted = DateUtils.formatDisplayDate(act.getActivityDate());
        if (act.getNotes() != null && !act.getNotes().trim().isEmpty()) {
            holder.tvDateAndNotes.setText(dateFormatted + " • " + act.getNotes());
        } else {
            holder.tvDateAndNotes.setText(dateFormatted);
        }

        String valFormatted = act.getValue() == (long) act.getValue() ?
                String.valueOf((long) act.getValue()) : String.format("%.1f", act.getValue());
        holder.tvValue.setText("+" + valFormatted);

        holder.tvPoints.setText("+" + act.getPoints() + " pts");

        // Icon based on activity type
        String type = act.getActivityType() != null ? act.getActivityType().toLowerCase() : "";
        if (type.contains("running")) {
            holder.ivIcon.setImageResource(R.drawable.ic_running);
        } else if (type.contains("step")) {
            holder.ivIcon.setImageResource(R.drawable.ic_steps);
        } else if (type.contains("cycl")) {
            holder.ivIcon.setImageResource(R.drawable.ic_cycling);
        } else if (type.contains("push")) {
            holder.ivIcon.setImageResource(R.drawable.ic_pushups);
        } else {
            holder.ivIcon.setImageResource(R.drawable.ic_workout);
        }
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTypeTitle, tvDateAndNotes, tvValue, tvPoints;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivActivityIcon);
            tvTypeTitle = itemView.findViewById(R.id.tvActivityTypeTitle);
            tvDateAndNotes = itemView.findViewById(R.id.tvActivityDateAndNotes);
            tvValue = itemView.findViewById(R.id.tvActivityValue);
            tvPoints = itemView.findViewById(R.id.tvActivityPoints);
        }
    }
}
