package com.example.fitnesschallenge.firebase;

import com.example.fitnesschallenge.models.Activity;
import com.example.fitnesschallenge.models.Challenge;
import com.example.fitnesschallenge.models.Participant;
import com.example.fitnesschallenge.models.User;
import com.example.fitnesschallenge.utils.Constants;
import com.example.fitnesschallenge.utils.DateUtils;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.util.Log;

public class FirestoreManager {
    private static FirestoreManager instance;
    private final FirebaseFirestore db;

    public interface FirestoreCallback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    // Helper class for User dashboard live statistics
    public static class UserStats implements Serializable {
        public int totalPoints = 0;
        public int activeChallenges = 0;
        public int completedChallenges = 0;
        public int totalActivities = 0;
        public int overallProgressPct = 0;
    }

    // Helper class for Leaderboard rows
    public static class LeaderboardEntry implements Serializable {
        public String userId;
        public String userName;
        public String email;
        public int totalPoints;
        public int rank;
        public int completedChallenges;
        public String goal;

        public LeaderboardEntry(String userId, String userName, String email, int totalPoints, int completedChallenges, String goal) {
            this.userId = userId;
            this.userName = userName;
            this.email = email;
            this.totalPoints = totalPoints;
            this.completedChallenges = completedChallenges;
            this.goal = goal;
        }
    }

    // Helper class for Admin dashboard live statistics
    public static class AdminStats implements Serializable {
        public int totalUsers = 0;
        public int activeUsers = 0;
        public int suspendedUsers = 0;
        public int totalChallenges = 0;
        public int activeChallenges = 0;
        public int completedChallenges = 0;
        public int deactivatedChallenges = 0;
        public int totalParticipants = 0;
        public int totalActivities = 0;
        public int overallCompletionRatePct = 0;
    }

    private FirestoreManager() {
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized FirestoreManager getInstance() {
        if (instance == null) {
            instance = new FirestoreManager();
        }
        return instance;
    }

    // ==========================================
    // USER OPERATIONS
    // ==========================================

    public void saveUser(User user, SimpleCallback callback) {
        db.collection(Constants.COLLECTION_USERS)
                .document(user.getUid())
                .set(user)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    public void getUser(String uid, FirestoreCallback<User> callback) {
        db.collection(Constants.COLLECTION_USERS)
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            user.setUid(documentSnapshot.getId());
                            callback.onSuccess(user);
                            return;
                        }
                    }
                    callback.onFailure(new Exception("User record not found"));
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void updateUser(User user, SimpleCallback callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", user.getName());
        updates.put("age", user.getAge());
        updates.put("gender", user.getGender());
        updates.put("height", user.getHeight());
        updates.put("weight", user.getWeight());
        updates.put("fitnessGoal", user.getFitnessGoal());
        if (user.getStatus() != null) {
            updates.put("status", user.getStatus());
        }

        db.collection(Constants.COLLECTION_USERS)
                .document(user.getUid())
                .update(updates)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    public void setUserSuspendedStatus(String uid, boolean suspended, SimpleCallback callback) {
        String statusStr = suspended ? "suspended" : "active";
        db.collection(Constants.COLLECTION_USERS)
                .document(uid)
                .update("status", statusStr)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    public void getAllUsers(FirestoreCallback<List<User>> callback) {
        db.collection(Constants.COLLECTION_USERS)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        User u = doc.toObject(User.class);
                        u.setUid(doc.getId());
                        list.add(u);
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(callback::onFailure);
    }

    // ==========================================
    // CHALLENGE OPERATIONS
    // ==========================================

    public void getChallenges(FirestoreCallback<List<Challenge>> callback) {
        db.collection(Constants.COLLECTION_CHALLENGES)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Challenge> challenges = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Challenge c = doc.toObject(Challenge.class);
                        c.setId(doc.getId());
                        challenges.add(c);
                    }
                    callback.onSuccess(challenges);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void getChallenge(String challengeId, FirestoreCallback<Challenge> callback) {
        db.collection(Constants.COLLECTION_CHALLENGES)
                .document(challengeId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Challenge c = doc.toObject(Challenge.class);
                        if (c != null) {
                            c.setId(doc.getId());
                            callback.onSuccess(c);
                            return;
                        }
                    }
                    callback.onFailure(new Exception("Challenge not found"));
                })
                .addOnFailureListener(callback::onFailure);
    }

    public Task<DocumentReference> createChallengeTask(Challenge challenge) {
        challenge.setCreatedAt(System.currentTimeMillis());
        return db.collection(Constants.COLLECTION_CHALLENGES).add(challenge);
    }

    public void createChallenge(Challenge challenge, FirestoreCallback<String> callback) {
        challenge.setCreatedAt(System.currentTimeMillis());
        db.collection(Constants.COLLECTION_CHALLENGES)
                .add(challenge)
                .addOnSuccessListener(docRef -> callback.onSuccess(docRef.getId()))
                .addOnFailureListener(callback::onFailure);
    }

    public void updateChallenge(Challenge challenge, SimpleCallback callback) {
        db.collection(Constants.COLLECTION_CHALLENGES)
                .document(challenge.getId())
                .set(challenge)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    public void deactivateChallenge(String challengeId, SimpleCallback callback) {
        db.collection(Constants.COLLECTION_CHALLENGES)
                .document(challengeId)
                .update("status", "deactivated")
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    public void deleteChallenge(String challengeId, SimpleCallback callback) {
        db.collection(Constants.COLLECTION_CHALLENGES)
                .document(challengeId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    // ==========================================
    // PARTICIPANT OPERATIONS
    // ==========================================

    public void isUserJoined(String userId, String challengeId, FirestoreCallback<Boolean> callback) {
        db.collection(Constants.COLLECTION_PARTICIPANTS)
                .whereEqualTo("userId", userId)
                .whereEqualTo("challengeId", challengeId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> callback.onSuccess(!queryDocumentSnapshots.isEmpty()))
                .addOnFailureListener(callback::onFailure);
    }

    public void joinChallenge(String userId, String challengeId, FirestoreCallback<String> callback) {
        // Query first to prevent double join
        isUserJoined(userId, challengeId, new FirestoreCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean joined) {
                if (joined) {
                    callback.onFailure(new Exception("User has already joined this challenge"));
                    return;
                }

                // Retrieve Challenge to populate cached fields
                getChallenge(challengeId, new FirestoreCallback<Challenge>() {
                    @Override
                    public void onSuccess(Challenge challenge) {
                        Participant participant = new Participant(
                                null,
                                userId,
                                challengeId,
                                DateUtils.getTodayDate(),
                                Constants.STATUS_ACTIVE,
                                0
                        );
                        participant.setChallengeTitle(challenge.getTitle());
                        participant.setActivityType(challenge.getActivityType());
                        participant.setTargetValue(challenge.getTargetValue());
                        participant.setUnit(challenge.getUnit());
                        participant.setCompletedValue(0.0);

                        db.collection(Constants.COLLECTION_PARTICIPANTS)
                                .add(participant)
                                .addOnSuccessListener(docRef -> callback.onSuccess(docRef.getId()))
                                .addOnFailureListener(callback::onFailure);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        callback.onFailure(e);
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public void removeUserFromChallenge(String participantId, SimpleCallback callback) {
        db.collection(Constants.COLLECTION_PARTICIPANTS)
                .document(participantId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    public void getMyChallenges(String userId, FirestoreCallback<List<Participant>> callback) {
        db.collection(Constants.COLLECTION_PARTICIPANTS)
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Participant> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Participant p = doc.toObject(Participant.class);
                        p.setId(doc.getId());
                        list.add(p);
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void getChallengeParticipants(String challengeId, FirestoreCallback<List<Participant>> callback) {
        db.collection(Constants.COLLECTION_PARTICIPANTS)
                .whereEqualTo("challengeId", challengeId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Participant> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Participant p = doc.toObject(Participant.class);
                        p.setId(doc.getId());
                        list.add(p);
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void getAllParticipants(FirestoreCallback<List<Participant>> callback) {
        db.collection(Constants.COLLECTION_PARTICIPANTS)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Participant> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Participant p = doc.toObject(Participant.class);
                        p.setId(doc.getId());
                        list.add(p);
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(callback::onFailure);
    }

    // ==========================================
    // ACTIVITY OPERATIONS & PROGRESS
    // ==========================================

    public void addActivity(Activity activity, FirestoreCallback<Integer> callback) {
        activity.setCreatedAt(System.currentTimeMillis());

        // 1. Calculate points
        int points = Constants.calculateActivityPoints(activity.getActivityType(), activity.getValue());
        activity.setPoints(points);

        // 2. Save activity document
        db.collection(Constants.COLLECTION_ACTIVITIES)
                .add(activity)
                .addOnSuccessListener(docRef -> {
                    // 3. Update participant progress and total points
                    updateParticipantAfterActivity(activity.getUserId(), activity.getChallengeId(), activity.getValue(), points, (updatedPoints, completedBonus) -> {
                        callback.onSuccess(points + (completedBonus ? Constants.COMPLETION_BONUS_POINTS : 0));
                    });
                })
                .addOnFailureListener(callback::onFailure);
    }

    interface OnParticipantUpdateListener {
        void onDone(int newTotalPoints, boolean completedBonus);
    }

    private void updateParticipantAfterActivity(String userId, String challengeId, double addedValue, int addedPoints, OnParticipantUpdateListener listener) {
        db.collection(Constants.COLLECTION_PARTICIPANTS)
                .whereEqualTo("userId", userId)
                .whereEqualTo("challengeId", challengeId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                        Participant p = doc.toObject(Participant.class);
                        if (p != null) {
                            double newCompleted = p.getCompletedValue() + addedValue;
                            int newPoints = p.getTotalPoints() + addedPoints;
                            boolean newlyCompleted = false;

                            String status = p.getStatus();
                            if (p.getTargetValue() > 0 && newCompleted >= p.getTargetValue() && !Constants.STATUS_COMPLETED.equalsIgnoreCase(status)) {
                                status = Constants.STATUS_COMPLETED;
                                newPoints += Constants.COMPLETION_BONUS_POINTS;
                                newlyCompleted = true;
                            }

                            Map<String, Object> updates = new HashMap<>();
                            updates.put("completedValue", newCompleted);
                            updates.put("totalPoints", newPoints);
                            updates.put("status", status);

                            boolean finalNewlyCompleted = newlyCompleted;
                            int finalNewPoints = newPoints;
                            doc.getReference().update(updates)
                                    .addOnSuccessListener(aVoid -> listener.onDone(finalNewPoints, finalNewlyCompleted))
                                    .addOnFailureListener(e -> listener.onDone(finalNewPoints, false));
                            return;
                        }
                    }
                    listener.onDone(addedPoints, false);
                })
                .addOnFailureListener(e -> listener.onDone(addedPoints, false));
    }

    public void getUserActivities(String userId, FirestoreCallback<List<Activity>> callback) {
        db.collection(Constants.COLLECTION_ACTIVITIES)
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Activity> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Activity a = doc.toObject(Activity.class);
                        a.setId(doc.getId());
                        list.add(a);
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void getUserActivitiesForChallenge(String userId, String challengeId, FirestoreCallback<List<Activity>> callback) {
        db.collection(Constants.COLLECTION_ACTIVITIES)
                .whereEqualTo("userId", userId)
                .whereEqualTo("challengeId", challengeId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Activity> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Activity a = doc.toObject(Activity.class);
                        a.setId(doc.getId());
                        list.add(a);
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(callback::onFailure);
    }

    // ==========================================
    // AGGREGATION & LIVE STATISTICS (DIPLOMA VIVA)
    // ==========================================

    public void getUserStatistics(String userId, FirestoreCallback<UserStats> callback) {
        UserStats stats = new UserStats();

        db.collection(Constants.COLLECTION_PARTICIPANTS)
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(participantsSnapshot -> {
                    double totalProgressSum = 0;
                    int challengeCount = 0;

                    for (DocumentSnapshot doc : participantsSnapshot.getDocuments()) {
                        Participant p = doc.toObject(Participant.class);
                        if (p != null) {
                            stats.totalPoints += p.getTotalPoints();
                            if (Constants.STATUS_COMPLETED.equalsIgnoreCase(p.getStatus())) {
                                stats.completedChallenges++;
                            } else {
                                stats.activeChallenges++;
                            }
                            totalProgressSum += p.getProgressPercentage();
                            challengeCount++;
                        }
                    }

                    if (challengeCount > 0) {
                        stats.overallProgressPct = (int) Math.round(totalProgressSum / challengeCount);
                    }

                    db.collection(Constants.COLLECTION_ACTIVITIES)
                            .whereEqualTo("userId", userId)
                            .get()
                            .addOnSuccessListener(activitiesSnapshot -> {
                                stats.totalActivities = activitiesSnapshot.size();
                                callback.onSuccess(stats);
                            })
                            .addOnFailureListener(e -> callback.onSuccess(stats));
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void getLeaderboard(FirestoreCallback<List<LeaderboardEntry>> callback) {
        getAllUsers(new FirestoreCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                Map<String, User> userMap = new HashMap<>();
                for (User u : users) {
                    userMap.put(u.getUid(), u);
                }

                db.collection(Constants.COLLECTION_PARTICIPANTS)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            Map<String, LeaderboardEntry> entryMap = new HashMap<>();

                            for (User u : users) {
                                entryMap.put(u.getUid(), new LeaderboardEntry(
                                        u.getUid(),
                                        u.getName().isEmpty() ? "Fitness Member" : u.getName(),
                                        u.getEmail(),
                                        0,
                                        0,
                                        u.getFitnessGoal()
                                ));
                            }

                            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                Participant p = doc.toObject(Participant.class);
                                if (p != null && p.getUserId() != null) {
                                    LeaderboardEntry entry = entryMap.get(p.getUserId());
                                    if (entry == null) {
                                        User u = userMap.get(p.getUserId());
                                        String name = (u != null && !u.getName().isEmpty()) ? u.getName() : "Member";
                                        String email = (u != null) ? u.getEmail() : "";
                                        String goal = (u != null) ? u.getFitnessGoal() : "General";
                                        entry = new LeaderboardEntry(p.getUserId(), name, email, 0, 0, goal);
                                        entryMap.put(p.getUserId(), entry);
                                    }
                                    entry.totalPoints += p.getTotalPoints();
                                    if (Constants.STATUS_COMPLETED.equalsIgnoreCase(p.getStatus())) {
                                        entry.completedChallenges++;
                                    }
                                }
                            }

                            List<LeaderboardEntry> list = new ArrayList<>(entryMap.values());
                            Collections.sort(list, (a, b) -> Integer.compare(b.totalPoints, a.totalPoints));

                            for (int i = 0; i < list.size(); i++) {
                                list.get(i).rank = i + 1;
                            }

                            callback.onSuccess(list);
                        })
                        .addOnFailureListener(callback::onFailure);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public void getAdminStatistics(FirestoreCallback<AdminStats> callback) {
        AdminStats stats = new AdminStats();

        // 1. Users count & active/suspended
        db.collection(Constants.COLLECTION_USERS)
                .get()
                .addOnSuccessListener(usersSnap -> {
                    stats.totalUsers = usersSnap.size();
                    for (DocumentSnapshot doc : usersSnap.getDocuments()) {
                        String st = doc.getString("status");
                        if ("suspended".equalsIgnoreCase(st)) {
                            stats.suspendedUsers++;
                        } else {
                            stats.activeUsers++;
                        }
                    }

                    // 2. Challenges count & active/completed/deactivated
                    db.collection(Constants.COLLECTION_CHALLENGES)
                            .get()
                            .addOnSuccessListener(challengesSnap -> {
                                stats.totalChallenges = challengesSnap.size();
                                for (DocumentSnapshot doc : challengesSnap.getDocuments()) {
                                    String st = doc.getString("status");
                                    if ("deactivated".equalsIgnoreCase(st)) {
                                        stats.deactivatedChallenges++;
                                    } else if (Constants.STATUS_COMPLETED.equalsIgnoreCase(st)) {
                                        stats.completedChallenges++;
                                    } else {
                                        stats.activeChallenges++;
                                    }
                                }

                                // 3. Participants count & overall completion rate
                                db.collection(Constants.COLLECTION_PARTICIPANTS)
                                        .get()
                                        .addOnSuccessListener(partSnap -> {
                                            stats.totalParticipants = partSnap.size();
                                            int completedPart = 0;
                                            for (DocumentSnapshot doc : partSnap.getDocuments()) {
                                                String st = doc.getString("status");
                                                if (Constants.STATUS_COMPLETED.equalsIgnoreCase(st)) {
                                                    completedPart++;
                                                }
                                            }
                                            if (stats.totalParticipants > 0) {
                                                stats.overallCompletionRatePct = (int) Math.round(((double) completedPart / stats.totalParticipants) * 100);
                                            }

                                            // 4. Activities count
                                            db.collection(Constants.COLLECTION_ACTIVITIES)
                                                    .get()
                                                    .addOnSuccessListener(actSnap -> {
                                                        stats.totalActivities = actSnap.size();
                                                        callback.onSuccess(stats);
                                                    })
                                                    .addOnFailureListener(e -> callback.onSuccess(stats));
                                        })
                                        .addOnFailureListener(e -> callback.onSuccess(stats));
                            })
                            .addOnFailureListener(callback::onFailure);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void testFirestoreConnection() {
        Map<String, Object> testData = new HashMap<>();
        testData.put("message", "Firebase is connected!");
        testData.put("createdAt", System.currentTimeMillis());

        db.collection("test")
                .add(testData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("FIREBASE_TEST", "SUCCESS: Firestore is connected!");
                })
                .addOnFailureListener(e -> {
                    Log.e("FIREBASE_TEST", "ERROR: " + e.getMessage());
                });
    }
}