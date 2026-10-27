# VIVA PRESENTATION SLIDES OUTLINE
## FITNESS CHALLENGE MANAGEMENT SYSTEM
### Microproject Presentation for Computer Diploma / Engineering Examination

---

## SLIDE 1: TITLE SLIDE
* **Title**: Fitness Challenge Management System
* **Subtitle**: A Serverless Native Android Application using Java, Material Design 3, & Google Cloud Firestore
* **Presenter Name**: [Your Name]
* **Roll No / Enrollment No**: [Your Roll Number]
* **Branch / Department**: Computer Engineering / Information Technology
* **College / Polytechnic Name**: [Your College Name]
* **Academic Year**: 2025–2026

---

## SLIDE 2: INTRODUCTION & PROBLEM STATEMENT
* **Background**: Fitness tracking applications encourage healthy lifestyles and community competition.
* **Problem Statement**:
  1. *SQLite/Room Databases*: Local storage isolates data on one phone; uninstallation deletes records and multi-user leaderboards are impossible.
  2. *PHP/MySQL Servers*: Require expensive hosting, server management, and REST API development.
* **Proposed Solution**:
  A **Cloud-Native, Serverless Android Application** connected directly to **Google Cloud Firestore** and **Firebase Authentication** using the official Firebase Android SDK.

---

## SLIDE 3: PROJECT OBJECTIVES & KEY FEATURES
* **Key Objectives**:
  * Zero hardcoded static arrays—100% cloud database synchronization.
  * Role-Based Access Control (RBAC): Separate **User** and **Admin** portals.
  * Gamified workout points engine with completion bonuses.
  * Interactive chart analytics using **MPAndroidChart**.
* **Key Features**:
  * Email/Password Auth with Password Reset.
  * Multi-metric workout tracking (Running, Steps, Cycling, HIIT, Push-Ups).
  * Real-time Global Leaderboard with Gold (#1), Silver (#2), Bronze (#3) badges.
  * Material Design 3 UI with native Light & Dark theme support.

---

## SLIDE 4: TECHNOLOGY STACK & ARCHITECTURE
* **Programming Language**: Java (JDK 11 / 17)
* **User Interface**: XML Layouts with Material Design 3 Components
* **Cloud Database**: Google Cloud Firestore (NoSQL Document Store)
* **Authentication**: Firebase Authentication
* **Charting Library**: MPAndroidChart (v3.1.0)
* **Architecture Diagram**:
  ```
  Android Client (Java) -> Firebase Auth & Cloud Firestore SDK -> Google Cloud
  ```

---

## SLIDE 5: CLOUD FIRESTORE DATABASE STRUCTURE
* **`users` Collection**: Stores profile, age, height, weight, BMI category, and role (`"user"` / `"admin"`).
* **`challenges` Collection**: Stores challenge target, activity type, dates, points reward, and status (`"active"`, `"completed"`, `"deactivated"`).
* **`participants` Collection**: Tracks enrollment, completed values, and earned points per user.
* **`activities` Collection**: Logs individual workouts with timestamps and calculated points.

---

## SLIDE 6: MATHEMATICAL FORMULAS & BUSINESS LOGIC
1. **Earned Points Calculation**:
   $$\text{Points} = \text{Base Points (10)} + (\text{Workout Value} \times \text{Rate Multiplier}) + \text{Completion Bonus (50)}$$
   * *Running/Cycling*: 10 pts per 1.0 KM
   * *Steps*: 5 pts per 1,000 steps
   * *Workout*: 1 pt per 1 min
   * *Push-Ups*: 1 pt per 5 reps
2. **Body Mass Index (BMI)**:
   $$\text{BMI} = \frac{\text{Weight (kg)}}{(\text{Height (cm)} / 100)^2}$$

---

## SLIDE 7: ADMIN MANAGEMENT & SECURITY
* **Role-Based Security**: Strict on-device authorization checks (`user.isAdmin()`) and server-side [`firestore.rules`](file:///C:/Users/DELL/Desktop/FitnessChallengeManagementSystem/firestore.rules).
* **Admin Dashboard Features**:
  * System Statistics: Total Users, Active Users, Total Challenges, Completion Rate %.
  * **User Management**: Real-time user search, user suspension toggle, and challenge assignment.
  * **Challenge Management**: Create, edit, and soft-deactivate challenges.
  * **User Progress Inspector**: Monitor user progress by status (`Completed`, `In Progress`, `Not Started`).

---

## SLIDE 8: COURSE OUTCOMES (CO) MAPPING
* **CO1 (Android Features)**: Activity Lifecycle, Intent routing, App Manifest permissions.
* **CO2 (Environment Setup)**: Android Studio, Gradle DSL, Version Catalog, Firebase SDK.
* **CO3 (UI Design)**: Material Design 3, Light & Dark themes, card elevation, micro-animations.
* **CO4 (UI Components)**: RecyclerView, Toolbars, TextInputOutlinedBox, Chips, MPAndroidChart.
* **CO5 (Database)**: Cloud Firestore NoSQL CRUD, Firebase Authentication.
* **CO6 (Deployment)**: Compiled Debug APK (`app-debug.apk`), deployed to physical device/AVD emulator.

---

## SLIDE 9: LIVE DEMONSTRATION FLOW
1. **User Sign-In**: Open app $\rightarrow$ Select **User Login** $\rightarrow$ View Dashboard, Active Challenges, & Leaderboard.
2. **Workout Activity Logging**: Log a 5.0 KM run $\rightarrow$ Demonstrate live points preview and dynamic leaderboard update.
3. **Admin Sign-In**: Select **Admin Login** $\rightarrow$ Open **Admin Dashboard** $\rightarrow$ Demonstrate Live Stats.
4. **User & Challenge Management**: Search user by name/email $\rightarrow$ Inspect user details & BMI $\rightarrow$ Soft-deactivate a challenge $\rightarrow$ Show **"Seed Sample Challenges (Viva)"** button.

---

## SLIDE 10: CONCLUSION & FUTURE SCOPE
* **Conclusion**:
  A fully functional, cloud-native Android app built without hardcoded arrays or web servers, fulfilling all diploma course outcomes.
* **Future Scope**:
  1. Integration with Google Health Connect for smartwatch step sync.
  2. Integration with Google Maps API for outdoor GPS route plotting.
* **Thank You!**
  * *Open for Questions & Viva Defense.*
