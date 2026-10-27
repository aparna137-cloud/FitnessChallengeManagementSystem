package com.example.fitnesschallenge.utils;

public class Constants {
    // Firestore Collections
    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_CHALLENGES = "challenges";
    public static final String COLLECTION_PARTICIPANTS = "participants";
    public static final String COLLECTION_ACTIVITIES = "activities";

    // User Roles
    public static final String ROLE_USER = "user";
    public static final String ROLE_ADMIN = "admin";

    // Challenge / Participant Status
    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_COMPLETED = "completed";

    // Intent Extra Keys
    public static final String EXTRA_CHALLENGE = "extra_challenge";
    public static final String EXTRA_CHALLENGE_ID = "extra_challenge_id";
    public static final String EXTRA_USER_ID = "extra_user_id";

    // Points System Constants
    public static final int BASE_POINTS = 10;
    public static final int COMPLETION_BONUS_POINTS = 50;

    /**
     * Calculate points for an activity based on type and value.
     * Diploma Viva Formula:
     * - Base points: 10
     * - Rate per unit:
     *     * Running / Cycling: 10 pts per 1.0 KM
     *     * Steps: 5 pts per 1,000 steps
     *     * Workout: 1 pt per minute
     *     * Push-Ups: 1 pt per 5 reps
     */
    public static int calculateActivityPoints(String activityType, double value) {
        int earned = BASE_POINTS;
        if (value <= 0) return earned;

        if (activityType == null) activityType = "Running";

        switch (activityType.toLowerCase()) {
            case "running":
            case "cycling":
                earned += (int) (value * 10);
                break;
            case "steps":
                earned += (int) ((value / 1000.0) * 5);
                break;
            case "workout":
                earned += (int) (value * 1);
                break;
            case "push-ups":
            case "pushups":
                earned += (int) (value / 5.0);
                break;
            default:
                earned += (int) (value * 2);
                break;
        }
        return Math.max(earned, BASE_POINTS);
    }
}