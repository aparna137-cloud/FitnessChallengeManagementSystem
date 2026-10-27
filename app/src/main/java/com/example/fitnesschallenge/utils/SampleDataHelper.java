package com.example.fitnesschallenge.utils;

import com.example.fitnesschallenge.firebase.FirestoreManager;
import com.example.fitnesschallenge.models.Challenge;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.List;

public class SampleDataHelper {

    public interface OnSeedCompleteListener {
        void onSuccess(String message);
        void onError(String error);
    }

    /**
     * Seeds initial 5 rich challenges into Firestore for College Viva demonstration.
     */
    public static void seedSampleChallenges(FirestoreManager firestoreManager, OnSeedCompleteListener listener) {
        List<Challenge> challenges = new ArrayList<>();
        long now = System.currentTimeMillis();

        challenges.add(new Challenge(
                null,
                "5 KM Running Challenge",
                "Complete 5 KM running to boost your cardiovascular endurance and stamina.",
                "Running",
                5.0,
                "KM",
                DateUtils.getTodayDate(),
                DateUtils.getDateDaysFromNow(14),
                100,
                Constants.STATUS_ACTIVE,
                "admin",
                now
        ));

        challenges.add(new Challenge(
                null,
                "10,000 Steps Daily Challenge",
                "Hit 10,000 steps daily to maintain an active lifestyle and stay energized.",
                "Steps",
                10000.0,
                "Steps",
                DateUtils.getTodayDate(),
                DateUtils.getDateDaysFromNow(7),
                80,
                Constants.STATUS_ACTIVE,
                "admin",
                now + 1
        ));

        challenges.add(new Challenge(
                null,
                "30 Minute Workout",
                "High-intensity functional bodyweight training session for full-body strength.",
                "Workout",
                30.0,
                "Minutes",
                DateUtils.getTodayDate(),
                DateUtils.getDateDaysFromNow(10),
                75,
                Constants.STATUS_ACTIVE,
                "admin",
                now + 2
        ));

        challenges.add(new Challenge(
                null,
                "7 Day Cycling Quest",
                "Pedal 35 KM throughout the week to strengthen legs and burn calories.",
                "Cycling",
                35.0,
                "KM",
                DateUtils.getTodayDate(),
                DateUtils.getDateDaysFromNow(21),
                150,
                Constants.STATUS_ACTIVE,
                "admin",
                now + 3
        ));

        challenges.add(new Challenge(
                null,
                "100 Push-Ups Challenge",
                "Test your upper body endurance by knocking out 100 total push-ups.",
                "Push-Ups",
                100.0,
                "Reps",
                DateUtils.getTodayDate(),
                DateUtils.getDateDaysFromNow(14),
                120,
                Constants.STATUS_ACTIVE,
                "admin",
                now + 4
        ));

        List<Task<?>> tasks = new ArrayList<>();
        for (Challenge challenge : challenges) {
            tasks.add(firestoreManager.createChallengeTask(challenge));
        }

        Tasks.whenAllSuccess(tasks)
                .addOnSuccessListener(results -> listener.onSuccess("Successfully seeded " + challenges.size() + " sample challenges into Firestore!"))
                .addOnFailureListener(e -> listener.onError("Failed to seed sample challenges: " + e.getMessage()));
    }
}