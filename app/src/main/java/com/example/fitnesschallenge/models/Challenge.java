package com.example.fitnesschallenge.models;

import java.io.Serializable;

public class Challenge implements Serializable {
    private String id;
    private String title;
    private String description;
    private String activityType; // "Running", "Steps", "Cycling", "Workout", "Push-Ups"
    private double targetValue;
    private String unit; // "KM", "Steps", "Minutes", "Reps"
    private String startDate; // "YYYY-MM-DD"
    private String endDate;   // "YYYY-MM-DD"
    private int points;
    private String status; // "active", "completed", "deactivated"
    private String difficulty; // "Beginner", "Intermediate", "Advanced"
    private String rules;
    private String createdBy;
    private long createdAt;

    // Required empty constructor for Firestore
    public Challenge() {
    }

    public Challenge(String id, String title, String description, String activityType, double targetValue, String unit, String startDate, String endDate, int points, String status, String createdBy, long createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.activityType = activityType;
        this.targetValue = targetValue;
        this.unit = unit;
        this.startDate = startDate;
        this.endDate = endDate;
        this.points = points;
        this.status = status;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.difficulty = "Intermediate";
        this.rules = "Complete the required workout value within the challenge start and end dates.";
    }

    public String getId() {
        return id != null ? id : "";
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title != null ? title : "";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description != null ? description : "";
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getActivityType() {
        return activityType != null ? activityType : "Running";
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public double getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(double targetValue) {
        this.targetValue = targetValue;
    }

    public String getUnit() {
        return unit != null ? unit : "";
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getStartDate() {
        return startDate != null ? startDate : "";
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate != null ? endDate : "";
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getStatus() {
        return status != null ? status : "active";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isDeactivated() {
        return "deactivated".equalsIgnoreCase(this.status);
    }

    public String getDifficulty() {
        return difficulty != null ? difficulty : "Intermediate";
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getRules() {
        return rules != null ? rules : "";
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public String getCreatedBy() {
        return createdBy != null ? createdBy : "";
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getTargetFormatted() {
        if (targetValue == (long) targetValue) {
            return String.format("%d %s", (long) targetValue, getUnit());
        } else {
            return String.format("%.1f %s", targetValue, getUnit());
        }
    }
}