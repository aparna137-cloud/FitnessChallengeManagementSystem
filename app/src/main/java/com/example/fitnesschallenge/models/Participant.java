package com.example.fitnesschallenge.models;

import java.io.Serializable;

public class Participant implements Serializable {
    private String id;
    private String userId;
    private String challengeId;
    private String joinedDate;
    private String status; // "active" or "completed"
    private int totalPoints;
    
    // Cached / joined helper fields for convenient display in UI
    private String challengeTitle;
    private String activityType;
    private double targetValue;
    private String unit;
    private double completedValue;
    private String userName;

    // Required empty constructor for Firestore
    public Participant() {
    }

    public Participant(String id, String userId, String challengeId, String joinedDate, String status, int totalPoints) {
        this.id = id;
        this.userId = userId;
        this.challengeId = challengeId;
        this.joinedDate = joinedDate;
        this.status = status;
        this.totalPoints = totalPoints;
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

    public String getJoinedDate() {
        return joinedDate != null ? joinedDate : "";
    }

    public void setJoinedDate(String joinedDate) {
        this.joinedDate = joinedDate;
    }

    public String getStatus() {
        return status != null ? status : "active";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public String getChallengeTitle() {
        return challengeTitle != null ? challengeTitle : "";
    }

    public void setChallengeTitle(String challengeTitle) {
        this.challengeTitle = challengeTitle;
    }

    public String getActivityType() {
        return activityType != null ? activityType : "";
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

    public double getCompletedValue() {
        return completedValue;
    }

    public void setCompletedValue(double completedValue) {
        this.completedValue = completedValue;
    }

    public String getUserName() {
        return userName != null ? userName : "";
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getProgressPercentage() {
        if (targetValue <= 0) return 0;
        int pct = (int) Math.round((completedValue / targetValue) * 100);
        return Math.min(pct, 100);
    }
}