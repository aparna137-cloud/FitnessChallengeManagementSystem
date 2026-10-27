# MICROPROJECT REPORT
## FITNESS CHALLENGE MANAGEMENT SYSTEM
### A Serverless Native Android Application using Java, Material Design 3, & Google Cloud Firestore

---

**Academic Year**: 2025–2026  
**Program**: Computer Engineering / Information Technology Diploma  
**Course**: Mobile Application Development (Android)  
**Project Platform**: Native Android (Java SDK, API 24+)  
**Cloud Infrastructure**: Google Firebase Authentication & Google Cloud Firestore  

---

## EXECUTIVE SUMMARY / ABSTRACT

The **Fitness Challenge Management System** is a real-world, cloud-native Android mobile application engineered using **Java**, **XML Layouts**, and **Material Design 3**, connected directly to **Firebase Authentication** and **Google Cloud Firestore**.

Developed specifically as a Computer Diploma microproject, this application eliminates traditional middle-tier server architectures (such as PHP/Apache) and local-only databases (like SQLite/Room). All user profiles, fitness challenges, participant enrollments, and workout activity logs are synchronized in real time with Cloud Firestore.

Key technical features include:
1. **Serverless Architecture**: Direct cloud database synchronization via the official Firebase Android SDK.
2. **Dynamic Mathematical Engine**: Real-time points calculation based on workout activity types, value rates, and milestone completion bonuses.
3. **Interactive Visual Analytics**: Graphical progress tracking using **MPAndroidChart** bar charts.
4. **Role-Based Access Control (RBAC)**: Distinct user capabilities separating regular fitness enthusiasts (`user`) from administrators (`admin`).
5. **Real-time Global Leaderboard**: Automated rank aggregation with medal badges (#1 Gold, #2 Silver, #3 Bronze) and active user highlighting.
6. **One-Click Demo Data Seeder**: Built-in sample challenge generator for college viva voce demonstrations.

---

## TABLE OF CONTENTS
1. [CHAPTER 1: INTRODUCTION & OBJECTIVES](#chapter-1-introduction--objectives)
2. [CHAPTER 2: LITERATURE REVIEW & SYSTEM COMPARISON](#chapter-2-literature-review--system-comparison)
3. [CHAPTER 3: SOFTWARE REQUIREMENTS SPECIFICATION (SRS)](#chapter-3-software-requirements-specification-srs)
4. [CHAPTER 4: SYSTEM ARCHITECTURE & DATABASE DESIGN](#chapter-4-system-architecture--database-design)
5. [CHAPTER 5: MODULES & BUSINESS LOGIC](#chapter-5-modules--business-logic)
6. [CHAPTER 6: SOFTWARE TESTING & TEST CASES](#chapter-6-software-testing--test-cases)
7. [CHAPTER 7: ADVANTAGES, LIMITATIONS & FUTURE SCOPE](#chapter-7-advantages-limitations--future-scope)
8. [CHAPTER 8: CONCLUSION](#chapter-8-conclusion)
9. [CHAPTER 9: COLLEGE VIVA VOCE QUESTIONS & ANSWERS](#chapter-9-college-viva-voce-questions--answers)

---

## CHAPTER 1: INTRODUCTION & OBJECTIVES

### 1.1 Introduction
In recent years, mobile applications have transformed health and fitness management. However, many college microprojects rely on hardcoded static arrays or local SQLite databases that lose data upon uninstallation and cannot support multi-user competition.

The **Fitness Challenge Management System** addresses these limitations by leveraging a modern **Serverless Cloud Architecture**. Connected to Google Cloud infrastructure, the application allows users to join competitive fitness challenges (running, walking, cycling, HIIT workouts, push-ups), log workouts, track personal Body Mass Index (BMI), and compete on a live global leaderboard.

### 1.2 Problem Statement
Traditional fitness applications suffer from three main bottlenecks:
1. **Local Storage Limitations**: SQLite/Room databases isolate user data on a single phone, preventing multi-user leaderboards or cross-device access.
2. **Complexity of Conventional Web Backends**: Hosting custom PHP/MySQL web servers requires server maintenance, SSL certificate setups, and manual REST API development.
3. **Hardcoded Data Demonstrations**: Academic projects using static mock data fail to demonstrate true database connectivity and dynamic cloud aggregation.

### 1.3 Objectives
1. **Cloud-Native Native Android App**: Build a native Java application using Material Design 3 guidelines without hardcoded data.
2. **Firebase Authentication Integration**: Implement secure user registration, email validation, and encrypted session management.
3. **Cloud Firestore Schema**: Design and maintain 4 core Firestore collections: `users`, `challenges`, `participants`, and `activities`.
4. **Dynamic Points Logic**: Automatically compute points and completion percentages based on activity type and distance/reps.
5. **Global Leaderboard & Analytics**: Aggregate points across all participants dynamically and plot visual bar charts via MPAndroidChart.
6. **Role-Based Admin Dashboard**: Provide authorized administrators with full CRUD control over challenges and user management.

---

## CHAPTER 2: LITERATURE REVIEW & SYSTEM COMPARISON

| Metric / Feature | Conventional PHP/MySQL System | Local SQLite/Room System | Proposed Cloud Firestore System |
| :--- | :--- | :--- | :--- |
| **Backend** | External Apache/PHP Web Server | None (On-Device Local) | Google Cloud Firestore (Serverless) |
| **Authentication** | Custom PHP Session / MySQL | None | Firebase Authentication (Encrypted) |
| **Real-time Sync** | Manual HTTP Polling | Impossible | Real-time WebSocket listeners |
| **Multi-User Competition**| Complex SQL JOIN Queries | Not Supported | Dynamic Firestore Query Aggregation |
| **Offline Resilience** | Network Exception / Crash | Local-Only | Firestore Offline Persistence Cache |
| **Security** | Vulnerable to SQL Injection | Unencrypted Local DB | Declarative `firestore.rules` |

---

## CHAPTER 3: SOFTWARE REQUIREMENTS SPECIFICATION (SRS)

### 3.1 Functional Requirements
- **FR-01 (User Authentication)**: Secure email/password registration, login, logout, and session persistence handled via Firebase Auth.
- **FR-02 (Profile & BMI Tracking)**: Calculate Body Mass Index ($\text{BMI} = \frac{\text{weight}}{\text{height}^2}$) and health classification dynamically.
- **FR-03 (Challenge Browsing & Enrollment)**: Browse active/completed challenges with filter chips and duplicate join validation.
- **FR-04 (Workout Logging)**: Capture workout activity type, date, quantity logged, and remarks with instant points calculation.
- **FR-05 (Leaderboard Ranking)**: Sort users by total points in descending order with top 3 medal highlights (#1 Gold, #2 Silver, #3 Bronze).
- **FR-06 (Admin Dashboard)**: Enable authorized admins (`role == "admin"`) to create, update, and delete challenges.

### 3.2 Non-Functional Requirements
- **NFR-01 (Security)**: All database access governed strictly by declarative `firestore.rules`.
- **NFR-02 (Performance)**: Firestore queries optimized using composite indexes (`createdAt`, `userId`, `challengeId`).
- **NFR-03 (UI/UX Usability)**: Compliant with Material Design 3 guidelines featuring light and dark theme adaptability.

### 3.3 System Specifications
- **Operating System**: Android 7.0 (API 24) minimum up to Android 15 (API 35).
- **Language & IDE**: Java (JDK 11/17) & Android Studio (Ladybug / Koala / 2026.x).
- **Dependencies**: Firebase BoM 33.4.0, Material Components, MPAndroidChart v3.1.0, SwipeRefreshLayout.

---

## CHAPTER 4: SYSTEM ARCHITECTURE & DATABASE DESIGN

### 4.1 System Architecture Diagram
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

### 4.2 Cloud Firestore Data Dictionary

1. **`users` Collection**:
   - `uid` (String, PK): Firebase Auth UID
   - `name` (String): Full Name
   - `email` (String): Registered email address
   - `role` (String): `"user"` or `"admin"`
   - `age` (Integer), `height` (Double, cm), `weight` (Double, kg)
   - `fitnessGoal` (String), `createdAt` (Long, timestamp)

2. **`challenges` Collection**:
   - `id` (String, PK): Auto-generated document ID
   - `title` (String), `description` (String), `activityType` (String)
   - `targetValue` (Double), `unit` (String), `points` (Integer)
   - `startDate` (String), `endDate` (String), `status` (String: `"active"`/`"completed"`)

3. **`participants` Collection**:
   - `id` (String, PK), `userId` (FK), `challengeId` (FK)
   - `completedValue` (Double), `totalPoints` (Integer), `status` (String)

4. **`activities` Collection**:
   - `id` (String, PK), `userId` (FK), `challengeId` (FK)
   - `activityDate` (String), `value` (Double), `notes` (String), `points` (Integer)

---

## CHAPTER 5: MODULES & BUSINESS LOGIC

### 5.1 Business Logic & Mathematical Formulas

1. **Earned Points Formula**:
   $$\text{Points} = \text{Base Points (10)} + (\text{Workout Value} \times \text{Rate Multiplier}) + \text{Completion Bonus (50)}$$
   - *Running / Cycling*: `10 pts` per 1.0 KM
   - *Steps*: `5 pts` per 1,000 steps
   - *Workout*: `1 pt` per 1 minute
   - *Push-Ups*: `1 pt` per 5 reps
   - *Completion Bonus*: `50 pts` credited automatically when `completedValue >= targetValue` for the first time.

2. **Challenge Progress Percentage**:
   $$\text{Progress \%} = \min\left(100, \left(\frac{\sum \text{activity.value}}{\text{targetValue}}\right) \times 100\right)$$

3. **Body Mass Index (BMI)**:
   $$\text{BMI} = \frac{\text{Weight (kg)}}{(\text{Height (m)})^2} = \frac{\text{Weight (kg)}}{(\text{Height (cm)} / 100)^2}$$

---

## CHAPTER 6: SOFTWARE TESTING & TEST CASES

| Test ID | Test Scenario | Input Data | Expected Output | Status |
| :--- | :--- | :--- | :--- | :--- |
| **TC-01** | Valid User Registration | Name: John, Email: john@fit.com, Pass: 123456 | Account created in Firebase Auth & Firestore `users/{uid}`; redirected to MainActivity | **PASS** |
| **TC-02** | Short Password Validation | Password: `123` | Error displayed: "Password must be at least 6 characters"; submission blocked | **PASS** |
| **TC-03** | User Role Routing (User) | Login with `user@fitness.com` (`role == "user"`) | Authenticated; routed to `MainActivity` | **PASS** |
| **TC-04** | User Role Routing (Admin) | Login with `admin@fitness.com` (`role == "admin"`) | Authenticated; routed to `AdminDashboardActivity` | **PASS** |
| **TC-05** | Duplicate Challenge Join Check | Tap "Join Challenge" twice on same challenge | Existing record detected in `participants`; duplicate enrollment blocked | **PASS** |
| **TC-06** | Workout Logging & Points | Log 5.0 KM run on 5 KM Challenge | Activity added; 10 base + 50 rate + 50 bonus = 110 pts awarded | **PASS** |
| **TC-07** | Leaderboard Rank Aggregation | Multiple users with points (350, 110, 40) | Ranked descending #1, #2, #3 with Gold/Silver medal icons; "YOU" card highlighted | **PASS** |
| **TC-08** | Viva Data Seeder | Tap "Seed Demo Data (Viva)" | Uploads 5 pre-configured challenges directly to Firestore | **PASS** |

---

## CHAPTER 7: ADVANTAGES, LIMITATIONS & FUTURE SCOPE

### 7.1 Advantages
1. **Serverless Infrastructure**: Zero web server hosting costs; scales automatically on Google Cloud infrastructure.
2. **Real-time NoSQL Sync**: Workouts, enrollments, and leaderboard points update across devices instantly.
3. **Material Design 3 Polish**: Modern UI supporting light and dark themes seamlessly.

### 7.2 Limitations
1. Initial user authentication and live multi-user leaderboard sync require active internet access.
2. Workouts are currently entered manually rather than fetched automatically from GPS or wearable step sensors.

### 7.3 Future Scope
1. Integrate Google Health Connect to auto-sync steps from smartwatch devices.
2. Add Google Maps Platform to plot real-time outdoor running and cycling paths.

---

## CHAPTER 8: CONCLUSION

The **Fitness Challenge Management System** successfully demonstrates a native, cloud-connected Android application built using Java, Material Design 3, Firebase Authentication, and Google Cloud Firestore. By eliminating external PHP/MySQL hosting and local-only SQLite limitations, the project delivers a scalable, real-time solution suitable for college diploma examinations, viva voce defense, and real-world fitness communities.

---

## CHAPTER 9: COLLEGE VIVA VOCE QUESTIONS & ANSWERS

**Q1: Why choose Cloud Firestore over SQLite or Room?**
*Answer*: SQLite and Room are local databases restricted to a single phone. Uninstalling the app deletes all data, and multi-user competition is impossible. Cloud Firestore is a cloud NoSQL database that synchronizes data in real-time across all registered users.

**Q2: How are user passwords secured?**
*Answer*: Passwords are **never** stored in Cloud Firestore. They are handled exclusively by Firebase Authentication, which hashes and encrypts passwords using cryptographic algorithms on Google's secure servers.

**Q3: How does the application differentiate between regular users and administrators?**
*Answer*: Each document in the `users` collection contains a `role` field. Upon login, the app checks `users/{uid}`: if `role == "admin"`, it launches `AdminDashboardActivity`; if `role == "user"`, it launches `MainActivity`.

**Q4: How do you prevent a user from joining the same challenge twice?**
*Answer*: Before enrolling, the app executes a Firestore query on `participants` checking `whereEqualTo("userId", currentUid).whereEqualTo("challengeId", challengeId)`. If a match exists, duplicate enrollment is blocked.
