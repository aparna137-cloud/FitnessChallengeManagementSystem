# Fitness Challenge Management System (Android Native Java)
**Computer Diploma College Microproject**

---

## 1. Project Title & Abstract

### Project Title
**Fitness Challenge Management System**

### Abstract
The **Fitness Challenge Management System** is a real native Android application developed using **Java**, **XML Layouts**, and **Material Design 3**, integrated directly with **Firebase Authentication** and **Google Cloud Firestore**. Designed specifically as a Computer Engineering / Information Technology Diploma microproject, this application eliminates traditional middle-tier server architectures (such as PHP and MySQL) and local-only databases (like SQLite/Room). All user accounts, fitness challenges, participant enrollments, and workout activity logs are synchronized in real-time with Cloud Firestore.

The application incorporates a dynamic points calculation algorithm, multi-metric workout tracking, real-time leaderboard rankings, visual progress analytics using **MPAndroidChart**, and a role-based administrative control dashboard.

---

## 2. Problem Statement & Objectives

### Problem Statement
Traditional fitness and gym logging applications often rely on either purely offline local storage (which loses data on app reinstall and cannot support multi-user competition) or cumbersome web servers (PHP/MySQL) requiring expensive backend hosting, maintenance, and manual REST API development. Students and gym enthusiasts need a cloud-native, reliable, serverless mobile solution where workout logs, competitive challenge tracking, and leaderboards update dynamically and securely.

### Objectives
1. **Serverless Architecture**: Connect directly from Android to Firebase Authentication and Cloud Firestore using the official Firebase Android SDK.
2. **Zero Hardcoded Data**: Store and retrieve 100% of data (users, challenges, participants, activities, leaderboard, and statistics) from Cloud Firestore.
3. **Role-Based Access Control**: Separate privileges between regular fitness `user` accounts and authorized `admin` accounts.
4. **Dynamic Progress Tracking**: Calculate workout completion percentages and points dynamically from logged activities.
5. **Interactive Data Visualization**: Display visual analytics using MPAndroidChart.
6. **Academic Suitability**: Deliver clean, well-commented Java code and comprehensive documentation suitable for diploma examination and viva voce defense.

---

## 3. Technology Stack

| Component | Technology / Library | Description |
| :--- | :--- | :--- |
| **Operating System** | Android (API 24+) | Android 7.0 Nougat up to Android 15 / 16 |
| **Development IDE** | Android Studio | Ladybug / Koala / 2026.x compatible |
| **Programming Language** | Java (JDK 11 / 17 / 21) | Pure Java implementation (no Kotlin) |
| **User Interface** | XML Layouts & Material Design 3 | MaterialCardView, TextInputLayout, BottomNavigationView |
| **Authentication** | Firebase Authentication | Secure email & password auth |
| **Primary Database** | Google Cloud Firestore | NoSQL document-oriented cloud database |
| **Chart Visualization** | MPAndroidChart (v3.1.0) | Interactive bar charts for challenge completion |
| **List Management** | AndroidX RecyclerView | Optimized list rendering with ViewHolders |
| **Build System** | Gradle (Kotlin DSL / Groovy) | Gradle 9.6 with Android Gradle Plugin 9.4 |

> [!NOTE]
> This application does **NOT** use PHP, MySQL, or SQLite/Room as primary storage. All operational data flows directly to Google Cloud Firestore.

---

## 4. System Architecture

```
+-----------------------------------------------------------+
|                   Android Client (Java)                   |
|  [Activities & Fragments] -> [Adapters] -> [ViewModels]   |
+-----------------------------+-----------------------------+
                              |
       +----------------------+----------------------+
       |                                             |
       v                                             v
+-------------------------------+     +-------------------------------+
|     Firebase Authentication    |     |      Cloud Firestore SDK      |
|  • Email/Password Sign Up     |     |  • Realtime NoSQL Sync        |
|  • Session Management & UID   |     |  • Dynamic Aggregations       |
|  • Secure Password Handling   |     |  • Offline Persistence Cache  |
+---------------+---------------+     +---------------+---------------+
                |                                     |
                +------------------+------------------+
                                   |
                                   v
+-----------------------------------------------------------+
|                Google Cloud Infrastructure                |
|                                                           |
|  Collections:                                             |
|  ├── users/{uid}               (Profile, Role, Metrics)  |
|  ├── challenges/{challengeId}  (Targets, Units, Points)   |
|  ├── participants/{id}         (Join status, Progress)    |
|  └── activities/{activityId}   (Workouts, Values, Points) |
+-----------------------------------------------------------+
```

---

## 5. Cloud Firestore Database Structure

### 1. `users` Collection
- **Document ID**: Firebase Authentication `uid`
- **Fields**:
  - `uid` (String): Unique user identifier from Firebase Auth
  - `name` (String): Full name of the user
  - `email` (String): Registered email address
  - `role` (String): `"user"` or `"admin"`
  - `age` (Integer): User age in years
  - `gender` (String): `"Male"`, `"Female"`, or `"Other"`
  - `height` (Double): Height in centimeters
  - `weight` (Double): Weight in kilograms
  - `fitnessGoal` (String): e.g., `"Muscle Gain"`, `"Weight Loss"`
  - `createdAt` (Long): Timestamp in milliseconds

*Example Document:*
```json
users/{uid}: {
  "uid": "aB3k9Fj019d...",
  "name": "John Doe",
  "email": "user@fitness.com",
  "role": "user",
  "age": 20,
  "gender": "Male",
  "height": 170.0,
  "weight": 65.0,
  "fitnessGoal": "Muscle Gain",
  "createdAt": 1727173200000
}
```

### 2. `challenges` Collection
- **Document ID**: Auto-generated by Firestore
- **Fields**:
  - `id` (String): Document ID
  - `title` (String): Challenge title
  - `description` (String): Detailed challenge briefing
  - `activityType` (String): `"Running"`, `"Steps"`, `"Cycling"`, `"Workout"`, `"Push-Ups"`
  - `targetValue` (Double): Target number (e.g., `5.0`, `10000.0`, `30.0`)
  - `unit` (String): `"KM"`, `"Steps"`, `"Minutes"`, `"Reps"`
  - `startDate` (String): Format `"YYYY-MM-DD"`
  - `endDate` (String): Format `"YYYY-MM-DD"`
  - `points` (Integer): Base points reward for completion
  - `status` (String): `"active"` or `"completed"`
  - `createdBy` (String): Admin UID
  - `createdAt` (Long): Creation timestamp

### 3. `participants` Collection
- **Document ID**: Auto-generated by Firestore
- **Fields**:
  - `id` (String): Document ID
  - `userId` (String): Firebase Auth UID of participant
  - `challengeId` (String): Target challenge document ID
  - `joinedDate` (String): Date joined `"YYYY-MM-DD"`
  - `status` (String): `"active"` or `"completed"`
  - `totalPoints` (Integer): Points accumulated in this challenge
  - `completedValue` (Double): Sum of logged workout values
  - `targetValue` (Double): Challenge goal target
  - `unit` (String): Measurement unit
  - `challengeTitle` (String): Challenge name

> **Duplicate Join Prevention**: When a user taps *Join Challenge*, the app runs a Firestore query `whereEqualTo("userId", currentUid).whereEqualTo("challengeId", challengeId)`. If a match is found, duplicate enrollment is strictly blocked.

### 4. `activities` Collection
- **Document ID**: Auto-generated by Firestore
- **Fields**:
  - `id` (String): Document ID
  - `userId` (String): Participant UID
  - `challengeId` (String): Target challenge ID
  - `activityType` (String): `"Running"`, `"Steps"`, etc.
  - `activityDate` (String): Date logged `"YYYY-MM-DD"`
  - `value` (Double): Quantity completed (e.g. `5.0` KM)
  - `notes` (String): Remarks or workout notes
  - `points` (Integer): Points awarded for this log
  - `createdAt` (Long): Timestamp

---

## 6. Mathematical Formulas & Business Logic (Diploma Viva)

### Points Calculation Formula
$$\text{Earned Points} = \text{Base Points} + (\text{Workout Value} \times \text{Rate}) + \text{Completion Bonus}$$
- **Base Points**: `10 pts` per logged activity
- **Rate Multipliers**:
  - Running / Cycling: `10 pts` per 1.0 KM
  - Steps: `5 pts` per 1,000 steps
  - Workout: `1 pt` per 1 minute
  - Push-Ups: `1 pt` per 5 reps
- **Completion Bonus**: `50 pts` automatically awarded when `completedValue >= targetValue` for the first time!

### Challenge Progress Formula
$$\text{Progress Percentage} = \min\left(100, \left(\frac{\sum \text{activity.value}}{\text{targetValue}}\right) \times 100\right)$$

### Body Mass Index (BMI) Formula
$$\text{BMI} = \frac{\text{Weight (kg)}}{(\text{Height (m)})^2} = \frac{\text{Weight (kg)}}{(\text{Height (cm)} / 100)^2}$$

---

## 7. Step-by-Step Firebase Project Setup

### Step 1: Create a Project in Firebase Console
1. Open [Firebase Console](https://console.firebase.google.com/).
2. Click **Add project**.
3. Name your project (e.g., `fitness-challenge-project`).
4. Disable or enable Google Analytics (either is fine), then click **Create Project**.

### Step 2: Register Android Application
1. In the project overview, click the **Android** icon.
2. Enter the exact Android package name:
   ```
   com.example.fitnesschallenge
   ```
3. Enter App nickname: `Fitness Challenge`.
4. Click **Register app**.

### Step 3: Download and Add `google-services.json`
1. Download the `google-services.json` file provided by Firebase.
2. Open the project root folder on your computer:
   ```
   FitnessChallengeManagementSystem/app/
   ```
3. Copy/overwrite the `google-services.json` file inside the `app/` folder.

### Step 4: Enable Email/Password Authentication
1. In the Firebase Console left menu, navigate to **Build** -> **Authentication**.
2. Click **Get Started**.
3. Under the **Sign-in method** tab, select **Email/Password**.
4. Toggle **Enable** to ON and click **Save**.

### Step 5: Create Cloud Firestore Database
1. In the Firebase Console left menu, navigate to **Build** -> **Firestore Database**.
2. Click **Create database**.
3. Choose a location close to you (e.g., `asia-south1` Mumbai).
4. Select **Start in test mode** (or production mode) and click **Create**.

### Step 6: Apply Firestore Security Rules
1. In Firestore Database, click the **Rules** tab.
2. Copy and paste the contents of `firestore.rules` (provided in the repository):
   ```javascript
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       function isAuthenticated() { return request.auth != null; }
       function isOwner(userId) { return isAuthenticated() && request.auth.uid == userId; }
       function isAdmin() {
         return isAuthenticated() &&
           exists(/databases/$(database)/documents/users/$(request.auth.uid)) &&
           get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == 'admin';
       }

       match /users/{userId} {
         allow read: if isAuthenticated();
         allow create: if isAuthenticated() && request.auth.uid == userId;
         allow update: if isOwner(userId) || isAdmin();
         allow delete: if isAdmin();
       }

       match /challenges/{challengeId} {
         allow read: if isAuthenticated();
         allow create, update, delete: if isAdmin();
       }

       match /participants/{participantId} {
         allow read: if isAuthenticated();
         allow create: if isAuthenticated() && request.resource.data.userId == request.auth.uid;
         allow update: if isAuthenticated() && (resource.data.userId == request.auth.uid || isAdmin());
         allow delete: if isAdmin();
       }

       match /activities/{activityId} {
         allow read: if isAuthenticated();
         allow create: if isAuthenticated() && request.resource.data.userId == request.auth.uid;
         allow update, delete: if isAuthenticated() && (resource.data.userId == request.auth.uid || isAdmin());
       }
     }
   }
   ```
3. Click **Publish**.

---

## 8. How to Create the Admin Account

To keep the application secure and prevent hardcoding passwords into source code:
1. In the Android App, tap **Register** to create an account with email:
   - Email: `admin@fitness.com`
   - Password: `admin123` (or your chosen password)
   - Name: `System Administrator`
2. Go to [Firebase Console](https://console.firebase.google.com/) -> **Firestore Database** -> `users` collection.
3. Locate the document corresponding to the admin user.
4. Edit the field `role` and change its value from `"user"` to:
   ```json
   "role": "admin"
   ```
5. Click **Update**.
6. When you log in with `admin@fitness.com`, the app automatically navigates directly to the **Admin Dashboard**!

---

## 9. One-Click Sample Data Seeder (For College Viva)

The application includes an integrated **Sample Data Seeder** utility designed specifically for college project viva demonstrations.
- **Where to find it**:
  1. On the **Admin Dashboard**, click the **"Seed Sample Challenges (Viva)"** button.
  2. Or from the User **Profile** tab, tap **"Seed Demo Data (Viva)"**.
- **What it does**:
  It automatically uploads 5 pre-configured fitness challenges directly into Cloud Firestore:
  1. *5 KM Running Challenge* (Running, 5.0 KM, 100 pts)
  2. *10,000 Steps Daily Challenge* (Steps, 10000 Steps, 80 pts)
  3. *30 Minute Workout* (Workout, 30 Minutes, 75 pts)
  4. *7 Day Cycling Quest* (Cycling, 35.0 KM, 150 pts)
  5. *100 Push-Ups Challenge* (Push-Ups, 100 Reps, 120 pts)

This ensures you can demonstrate a live database in front of examiners within seconds without manual typing!

---

## 10. Application Screens & Navigation Flow

### User Flow
```
Splash Screen
  ├── [If Not Logged In] -> Login Activity -> Register Activity
  └── [If Logged In]
        ├── role == 'user' -> MainActivity (Bottom Navigation)
        │     ├── [Tab 1] Home Dashboard (Live stats, Recent workouts, Quick actions)
        │     ├── [Tab 2] Challenges (Filter Active/Completed, Tap -> ChallengeDetailsActivity -> Join)
        │     ├── [Tab 3] My Challenges (Live progress bars, Quick Log Activity)
        │     ├── [Tab 4] Progress (MPAndroidChart BarChart completion analytics)
        │     ├── [Tab 5] Leaderboard (Real-time ranked by points, Highlighted "YOU" card)
        │     └── [Tab 6] Profile (BMI calculation, Edit profile dialog, Seed data, Logout)
        │
        └── role == 'admin' -> AdminDashboardActivity
              ├── Manage Challenges (View, Create FAB, Edit, Delete with alert)
              ├── Manage Users (Inspect registered users and physical metrics)
              ├── View Participants (Track participants per challenge)
              └── Seed Sample Data & Logout
```

---

## 11. How to Build & Run in Android Studio

1. Open **Android Studio**.
2. Select **Open** and select the folder `FitnessChallengeManagementSystem`.
3. Wait for Gradle Sync to complete.
4. Ensure your local `google-services.json` is placed in `FitnessChallengeManagementSystem/app/`.
5. Connect an Android device (via USB with USB Debugging enabled) or start an Android Virtual Device (AVD).
6. Click **Run 'app'** (Green play button or `Shift + F10`).

---

## 12. Future Enhancements

1. **GPS Route Tracking**: Integrate Google Maps Platform to plot real-time outdoor running and cycling paths.
2. **Push Notifications**: Firebase Cloud Messaging (FCM) to remind users of upcoming challenge deadlines.
3. **Wearable Synchronization**: Integrate with Google Health Connect to auto-sync steps from fitness bands.
4. **Social Sharing**: Share earned certificates and challenge completion badges to social media.
