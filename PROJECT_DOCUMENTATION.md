# FITNESS CHALLENGE MANAGEMENT SYSTEM
## A Microproject Report for Computer Engineering / Information Technology Diploma

---

## PROJECT DETAILS
- **Project Title**: Fitness Challenge Management System
- **Platform**: Native Android Application
- **Language**: Java (Android SDK API 24+)
- **Architecture**: Serverless Cloud Architecture (Firebase Authentication + Google Cloud Firestore)
- **Target Audience**: Fitness Enthusiasts, Gym Members, College Sports Departments, Health Communities

---

# CHAPTER 1: INTRODUCTION

## 1.1 Introduction
The **Fitness Challenge Management System** is a native Android mobile application created to motivate, organize, and monitor physical fitness routines and group fitness challenges. Designed as a real-world, cloud-native system for a Computer Diploma microproject, this application links directly to Google Cloud services—specifically Firebase Authentication for identity management and Google Cloud Firestore for flexible NoSQL database synchronization.

Unlike legacy software architectures that rely on external web servers (e.g., Apache/PHP) with relational databases (MySQL) or store data locally using device-bound SQLite/Room databases, this system leverages a serverless architecture. Every challenge enrolled, every kilometer run, every step logged, and every point earned is immediately committed to and calculated from the cloud.

## 1.2 Problem Statement
Traditional methods for tracking personal fitness and sports competitions suffer from distinct operational bottlenecks:
1. **Manual Record Keeping**: Paper logs and local spreadsheets are prone to data loss, human entry error, and lack of real-time accessibility.
2. **Device Dependency in Local Databases**: Mobile applications built with local databases (SQLite/Room) cannot synchronize progress across devices, lose all historical activity upon uninstallation, and make multi-user community challenges impossible.
3. **Complexity of Legacy Backends**: Traditional systems built on PHP and MySQL require dedicated web hosting, complex REST API endpoints, SSL certificates, and manual database scaling.
4. **Static and Hardcoded Demonstrations**: Many college microprojects rely on hardcoded arrays or mock static data, failing to demonstrate real database connectivity, authentication flows, and dynamic aggregation.

## 1.3 Objectives
The primary objectives of the project are:
1. To engineer a native Android application using **Java** and **Material Design 3** that operates without hardcoded application data.
2. To integrate **Firebase Authentication** for secure user registration, email validation, encrypted password storage, and session persistence.
3. To architect a schema on **Google Cloud Firestore** across four core collections: `users`, `challenges`, `participants`, and `activities`.
4. To implement a dynamic mathematical points and progress calculation engine that rewards users for daily workouts and challenge completions.
5. To generate a real-time, dynamic **Global Leaderboard** sorted by points in descending order with distinct visual highlighting for the logged-in user.
6. To visualize completion trends using **MPAndroidChart** interactive bar charts.
7. To enforce role-based access control (RBAC) distinguishing between standard fitness `user` roles and `admin` roles, providing an administrative dashboard with full challenge CRUD capabilities.
8. To implement a one-click sample data seeding mechanism for academic viva voce examinations.

## 1.4 Scope
- **User Scope**: Any individual seeking to join fitness challenges (running, walking, cycling, HIIT workouts, push-ups), track quantitative workout metrics, monitor Body Mass Index (BMI), and compete on community leaderboards.
- **Administrative Scope**: Gym trainers, college athletic directors, and fitness administrators who manage the lifecycle of challenges, oversee user engagement, and maintain data integrity.
- **Technical Scope**: Native Android (Java), Firebase BoM 33.4.0, Cloud Firestore, Material Components, MPAndroidChart v3.1.0.

---

# CHAPTER 2: LITERATURE REVIEW & SYSTEM COMPARISON

## 2.1 Existing System vs. Proposed System

| Feature | Existing Conventional Systems | Proposed Fitness Challenge System |
| :--- | :--- | :--- |
| **Backend Architecture** | PHP scripts on Apache/Nginx + MySQL | Serverless Google Cloud Firestore SDK |
| **Authentication** | Plaintext or custom MD5 hashed passwords in SQL | Google Firebase Authentication (Bcrypt / PBKDF2) |
| **Data Synchronization** | Manual HTTP polling via Retrofit/Volley | Real-time listeners and atomic cloud document sets |
| **Multi-User Competition** | Static leaderboards or complex SQL JOIN queries | Dynamic Firestore queries and real-time point ranking |
| **Offline Tolerance** | App crashes or throws network exceptions | Firestore local persistence cache + graceful error UI |
| **Administrative Control** | Separate web portal built in PHP/HTML | Integrated native Android Admin Dashboard |
| **Data Integrity** | Prone to SQL Injection and session hijacking | Enforced via declarative `firestore.rules` |

---

# CHAPTER 3: SOFTWARE REQUIREMENTS SPECIFICATION (SRS)

## 3.1 Functional Requirements

### 3.1.1 Authentication & Profile Management
- **FR-01 (User Registration)**: The system shall allow users to register with Full Name, Email, Password, Age, Gender, Height, Weight, and Fitness Goal.
- **FR-02 (Secure Password Handling)**: Passwords shall be handled exclusively by Firebase Authentication and shall never be stored as plaintext in Cloud Firestore.
- **FR-03 (Role Routing)**: Upon login, the system shall read the user's role from Firestore `users/{uid}` and route `"admin"` users to `AdminDashboardActivity` and `"user"` accounts to `MainActivity`.
- **FR-04 (Profile Update)**: Users shall be able to view their profile, inspect their dynamically calculated Body Mass Index (BMI), and update physical attributes.

### 3.1.2 Challenge Management
- **FR-05 (Browse Challenges)**: Users shall be able to browse all challenges loaded from Cloud Firestore, filtered by status (All, Active, Completed).
- **FR-06 (Challenge Details)**: Users shall view title, description, activity type, target goal, duration, and points reward.
- **FR-07 (Duplicate Join Prevention)**: The system shall query the `participants` collection to verify whether a user has already enrolled in a challenge before creating a new participant record.
- **FR-08 (Admin CRUD)**: Administrators shall be able to Create, Read, Update, and Delete challenges with confirmation dialogs.

### 3.1.3 Activity Tracking & Progress
- **FR-09 (Log Activity)**: Users shall log completed workout sessions selecting the joined challenge, date, achievement value, and notes.
- **FR-10 (Points Calculation)**: The system shall compute earned points using the standardized formula and credit both the activity record and the participant's total points.
- **FR-11 (Live Progress Update)**: The system shall dynamically compute completed percentage $\left(\frac{\sum \text{value}}{\text{target}} \times 100\right)$ and award a 50-point bonus when the target is reached.

### 3.1.4 Analytics & Leaderboard
- **FR-12 (Leaderboard Generation)**: The system shall compute global user ranks in descending order of points and highlight the active user's row.
- **FR-13 (Progress Chart)**: The system shall render an interactive bar chart of challenge milestones using MPAndroidChart.

---

## 3.2 Non-Functional Requirements
- **NFR-01 (Security)**: All access to Firestore collections must be guarded by `firestore.rules`.
- **NFR-02 (Performance)**: Firestore queries must utilize indexed fields (`createdAt`, `userId`, `challengeId`).
- **NFR-03 (Reliability)**: Network interruptions must be communicated through Material Snackbars without crashing the app.
- **NFR-04 (Usability)**: Interface follows Material Design 3 guidelines with consistent padding, color contrasts, and clear typography.

---

## 3.3 Hardware Requirements
- **Mobile Device / Emulator**:
  - Processor: 1.5 GHz Octa-core or higher (ARM64 / x86_64)
  - RAM: 2 GB minimum (4 GB recommended)
  - Storage: 100 MB free internal space
  - Network: Active Internet connection (Wi-Fi or Mobile Data)
- **Development Workstation**:
  - CPU: Intel Core i5 / AMD Ryzen 5 or higher
  - RAM: 8 GB minimum (16 GB recommended for Android Studio)
  - Disk: 10 GB SSD space for Android SDK, Gradle caches, and emulator

---

## 3.4 Software Requirements
- **Operating System**: Windows 10/11, macOS, or Linux
- **Development Environment**: Android Studio (Ladybug / Koala / 2024.x / 2026.x)
- **Java Development Kit**: OpenJDK 11, 17, or 21
- **Android Target**: Android 7.0 (API Level 24) minimum up to Android 15 (API Level 35)
- **Cloud Backend**: Google Cloud Firestore & Firebase Authentication

---

# CHAPTER 4: SYSTEM ARCHITECTURE & DATABASE DESIGN

## 4.1 Cloud Firestore Data Dictionary

### Collection: `users`
| Field Name | Data Type | Key Type | Description |
| :--- | :--- | :--- | :--- |
| `uid` | String | Primary Key | Firebase Authentication UID |
| `name` | String | - | Full name of the participant |
| `email` | String | Unique | Registered email address |
| `role` | String | - | `"user"` or `"admin"` |
| `age` | Integer | - | Age in years |
| `gender` | String | - | `"Male"`, `"Female"`, or `"Other"` |
| `height` | Double | - | Height in centimeters (cm) |
| `weight` | Double | - | Weight in kilograms (kg) |
| `fitnessGoal` | String | - | Primary objective (e.g. Muscle Gain) |
| `createdAt` | Long | - | System epoch timestamp |

### Collection: `challenges`
| Field Name | Data Type | Key Type | Description |
| :--- | :--- | :--- | :--- |
| `id` | String | Primary Key | Auto-generated Firestore document ID |
| `title` | String | - | Challenge title |
| `description`| String | - | Scope and details of the challenge |
| `activityType`| String | Indexed | `"Running"`, `"Steps"`, `"Cycling"`, etc. |
| `targetValue`| Double | - | Numeric milestone target |
| `unit` | String | - | Measurement unit (`KM`, `Steps`, `Reps`) |
| `startDate` | String | - | Start date in `YYYY-MM-DD` |
| `endDate` | String | - | End date in `YYYY-MM-DD` |
| `points` | Integer | - | Base completion point reward |
| `status` | String | Indexed | `"active"` or `"completed"` |
| `createdBy` | String | Foreign Key | Admin UID who authored challenge |
| `createdAt` | Long | Indexed | Timestamp for sorting |

### Collection: `participants`
| Field Name | Data Type | Key Type | Description |
| :--- | :--- | :--- | :--- |
| `id` | String | Primary Key | Auto-generated document ID |
| `userId` | String | Foreign Key | Enrolled user's Firebase UID |
| `challengeId`| String | Foreign Key | Enrolled challenge ID |
| `joinedDate` | String | - | Date of enrollment (`YYYY-MM-DD`) |
| `status` | String | - | `"active"` or `"completed"` |
| `totalPoints`| Integer | - | Points earned within this challenge |
| `completedValue`| Double | - | Total distance / reps / minutes logged |
| `targetValue`| Double | - | Cached target for quick display |
| `unit` | String | - | Cached unit string |
| `challengeTitle`| String | - | Cached challenge name |

### Collection: `activities`
| Field Name | Data Type | Key Type | Description |
| :--- | :--- | :--- | :--- |
| `id` | String | Primary Key | Auto-generated document ID |
| `userId` | String | Foreign Key | Participant UID |
| `challengeId`| String | Foreign Key | Target challenge ID |
| `activityType`| String | - | Workout category |
| `activityDate`| String | - | Date performed (`YYYY-MM-DD`) |
| `value` | Double | - | Quantity logged |
| `notes` | String | - | User remarks (e.g. morning park run) |
| `points` | Integer | - | Points credited for this specific log |
| `createdAt` | Long | Indexed | Timestamp for reverse chronological order |

---

# CHAPTER 5: MODULES & ALGORITHMS

## 5.1 Modules Description
1. **Authentication Module (`SplashActivity`, `LoginActivity`, `RegisterActivity`, `FirebaseAuthManager`)**:
   Handles user registration, login, logout, and role checking against Firestore.
2. **Dashboard Module (`DashboardFragment`, `MainActivity`)**:
   Provides real-time aggregate statistics: Total Points, Active Challenges, Completed Challenges, Total Activities Logged, Overall Progress percentage, and recent activity logs.
3. **Challenge Browsing & Enrollment (`ChallengesFragment`, `ChallengeDetailsActivity`)**:
   Displays all active challenges with filter chips. Handles enrollment with duplicate join verification.
4. **My Challenges Tracking (`MyChallengesFragment`)**:
   Renders challenges currently joined by the user with dynamic horizontal progress bars and quick workout logging shortcuts.
5. **Workout Logging Module (`AddActivityActivity`)**:
   Captures activity type, date, value, and notes. Displays a live points preview and executes atomic updates across Firestore.
6. **Analytics & Visualization (`ProgressFragment`)**:
   Integrates MPAndroidChart `BarChart` to visualize completion percentages across all enrolled challenges.
7. **Competitive Leaderboard (`LeaderboardFragment`, `LeaderboardAdapter`)**:
   Aggregates points across all participants, sorts entries in descending order, awards medal badges (Gold, Silver, Bronze) for top 3, and highlights the current user's entry.
8. **User Profile & Health Metric (`ProfileFragment`)**:
   Displays user physical attributes, calculates Body Mass Index (BMI) dynamically, allows in-place profile updates, and includes a demo data seeder.
9. **Administrative Control Center (`AdminDashboardActivity`, `ManageChallengesActivity`, `AddEditChallengeActivity`, `ManageUsersActivity`, `ParticipantsActivity`)**:
   Permits authorized administrators to oversee system-wide metrics, perform challenge CRUD operations, and inspect registered users and participants.

---

# CHAPTER 6: TESTING & VERIFICATION

## 6.1 Test Cases Table

| Test ID | Test Scenario | Input Data | Expected Output | Status |
| :--- | :--- | :--- | :--- | :--- |
| **TC-01** | User Registration with Valid Data | Name: John, Email: john@fit.com, Pass: 123456, Age: 20, H: 170, W: 65 | Account created in Firebase Auth; document created in `users/{uid}`; redirected to MainActivity | **PASS** |
| **TC-02** | User Registration with Short Password | Password: `123` | Error displayed: "Password must be at least 6 characters"; submission blocked | **PASS** |
| **TC-03** | User Login Routing (Regular User) | Email: `user@fitness.com`, Password: `password` | Authentication verified; role evaluated as `"user"`; routes to `MainActivity` | **PASS** |
| **TC-04** | User Login Routing (Admin User) | Email: `admin@fitness.com`, Password: `admin123` | Authentication verified; role evaluated as `"admin"`; routes to `AdminDashboardActivity` | **PASS** |
| **TC-05** | Duplicate Challenge Join Check | User attempts to join "5 KM Running Challenge" twice | Query detects existing record in `participants`; displays "You have already joined this challenge!"; blocks creation | **PASS** |
| **TC-06** | Activity Logging & Points Calculation | Log 5.0 KM run on 5 KM Challenge | Activity recorded; 10 base + 50 rate + 50 bonus = 110 pts; status changed to `"completed"` | **PASS** |
| **TC-07** | Leaderboard Sorting & Highlight | Multiple users with points (150, 110, 40) | Ranked descending #1, #2, #3; medals assigned; active user's card highlighted with `"YOU"` tag | **PASS** |
| **TC-08** | Admin Challenge Creation | Title: "100 Push-Ups", Target: 100, Unit: Reps, Pts: 120 | Document added to `challenges` collection; visible immediately in User Challenges tab | **PASS** |
| **TC-09** | Admin Challenge Deletion | Delete challenge with ID `ch_123` | Alert confirmation accepted; document deleted from Firestore; list refreshed | **PASS** |
| **TC-10** | Offline / Disconnected Behavior | Network disabled during login | Snackbar displayed: "Network error / connection failure"; app does not crash | **PASS** |

---

# CHAPTER 7: ADVANTAGES, LIMITATIONS & FUTURE SCOPE

## 7.1 Advantages
1. **Serverless Scalability**: No backend servers to host, configure, or patch; scales automatically on Google Cloud infrastructure.
2. **Real-Time Data**: Workouts and challenges are reflected in real-time across devices.
3. **Strong Security**: Cloud Firestore security rules ensure clients cannot tamper with another user's profile or inject malicious SQL payloads.
4. **Clean Code Structure**: Clear separation between Activities, Fragments, Adapters, Models, and Managers.

## 7.2 Limitations
1. **Requires Internet Connectivity**: Cloud Firestore offline persistence caches queries, but initial authentication and multi-user leaderboard sync require active internet connectivity.
2. **No Hardware Sensor Integration**: Step and workout distances are currently entered manually rather than read directly from GPS or step counter sensors.

## 7.3 Future Scope
1. **Sensor Automation**: Integrating Google Health Connect and hardware pedometers to auto-log steps and distance.
2. **GPS Mapping**: Embedding Google Maps Platform to render outdoor running and cycling paths in real time.
3. **Social Sharing**: Generating shareable social media badges upon completing fitness challenges.

---

# CHAPTER 8: CONCLUSION

The **Fitness Challenge Management System** successfully demonstrates the design and implementation of a modern, native Android application built using Java and Material Design 3, connected directly to Google Firebase Authentication and Cloud Firestore. By removing the need for external PHP/MySQL servers and eliminating hardcoded mock arrays, the project proves how modern serverless cloud databases can power dynamic mobile software. The application fulfills all academic requirements for a Computer Diploma microproject and serves as a solid foundation for enterprise health and wellness management systems.

---

# CHAPTER 9: COLLEGE VIVA VOCE QUESTIONS & ANSWERS

**Q1: Why did you choose Google Cloud Firestore instead of SQLite or Room database?**
*Answer*: SQLite and Room are local on-device databases. If the app is uninstalled or used across multiple phones, local data is lost and cannot be shared. Cloud Firestore is a cloud-hosted NoSQL document database that synchronizes data in real time across multiple users, enabling group challenges, admin management, and a live global leaderboard.

**Q2: Why did you not use PHP and MySQL?**
*Answer*: Traditional PHP and MySQL architectures require maintaining a separate web server, writing REST API endpoints, handling SQL injection risks, and paying for hosting. Firebase provides a serverless architecture where the Android client interacts directly with Cloud Firestore using the secure Firebase Android SDK and server-side declarative security rules.

**Q3: Where and how are user passwords stored?**
*Answer*: Passwords are **never** stored in Cloud Firestore. They are managed exclusively by Firebase Authentication, which encrypts and hashes passwords using cryptographic algorithms (such as PBKDF2/Bcrypt) on Google's secure authentication servers. Firestore only stores non-sensitive profile information linked by the user's unique Firebase UID.

**Q4: How does the application differentiate between normal users and administrators?**
*Answer*: In the Firestore `users` collection, each document contains a `role` field. When a user logs in, the app retrieves `users/{uid}`. If `role == "admin"`, it opens `AdminDashboardActivity`; if `role == "user"`, it opens `MainActivity`. Furthermore, `firestore.rules` verifies this field to prevent unauthorized users from creating or deleting challenges.

**Q5: How do you prevent a user from joining the same challenge twice?**
*Answer*: When the user taps the "Join Challenge" button, the application executes a query on the `participants` collection checking `whereEqualTo("userId", currentUid).whereEqualTo("challengeId", challengeId)`. If a matching document already exists, the enrollment is rejected with an alert message.

**Q6: What is the formula used for progress calculation?**
*Answer*: Progress percentage is calculated as:
$$\text{Progress \%} = \min\left(100, \left(\frac{\sum \text{activity.value}}{\text{targetValue}}\right) \times 100\right)$$
When this reaches 100%, the challenge status updates to "completed" and a 50-point completion bonus is credited.

**Q7: Which library is used to render charts, and what does it display?**
*Answer*: We used **MPAndroidChart** (v3.1.0). In `ProgressFragment`, it displays an interactive Bar Chart showing the user's progress percentage across all joined challenges with color-coded bars indicating active versus completed milestones.

**Q8: What happens if there is no internet connection?**
*Answer*: The app catches network exceptions and displays user-friendly Material Snackbars and Toast notifications rather than crashing. Furthermore, Firestore's built-in offline cache enables previously loaded challenges to remain accessible while offline.
