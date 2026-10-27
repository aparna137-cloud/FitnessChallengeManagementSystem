package com.example.fitnesschallenge.models;

import java.io.Serializable;

public class Activity implements Serializable {
    private String id;
    private String userId;
    private String challengeId;
    private String activityType;
    private String activityDate; // "YYYY-MM-DD"
    private double value;
    private String notes;
    private int points;
    private long createdAt;

    // Required empty constructor for Firestore
    public Activity() {
    }

    public Activity(String id, String userId, String challengeId, String activityType, String activityDate, double value, String notes, int points, long createdAt) {
        this.id = id;
        this.userId = userId;
        this.challengeId = challengeId;
        this.activityType = activityType;
        this.activityDate = activityDate;
        this.value = value;
        this.notes = notes;
        this.points = points;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id != null ? id : "";
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId != null ? userId : "";
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getChallengeId() {
        return challengeId != null ? challengeId : "";
    }

    public void setChallengeId(String challengeId) {
        this.challengeId = challengeId;
    }

    public String getActivityType() {
        return activityType != null ? activityType : "Running";
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getActivityDate() {
        return activityDate != null ? activityDate : "";
    }

    public void setActivityDate(String activityDate) {
        this.activityDate = activityDate;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getNotes() {
        return notes != null ? notes : "";
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}