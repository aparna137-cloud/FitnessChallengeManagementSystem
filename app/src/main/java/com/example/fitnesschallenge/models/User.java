package com.example.fitnesschallenge.models;

import java.io.Serializable;

public class User implements Serializable {
    private String uid;
    private String name;
    private String email;
    private String role; // "user" or "admin"
    private String status; // "active" or "suspended"
    private int age;
    private String gender;
    private double height; // in cm
    private double weight; // in kg
    private String fitnessGoal;
    private long createdAt;

    // Required empty constructor for Firestore
    public User() {
    }

    public User(String uid, String name, String email, String role, int age, String gender, double height, double weight, String fitnessGoal, long createdAt) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.role = role;
        this.status = "active";
        this.age = age;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.fitnessGoal = fitnessGoal;
        this.createdAt = createdAt;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name != null ? name : "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email != null ? email : "";
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role != null ? role : "user";
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(this.role);
    }

    public String getStatus() {
        return status != null ? status : "active";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isSuspended() {
        return "suspended".equalsIgnoreCase(this.status);
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender != null ? gender : "Other";
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getFitnessGoal() {
        return fitnessGoal != null ? fitnessGoal : "General Fitness";
    }

    public void setFitnessGoal(String fitnessGoal) {
        this.fitnessGoal = fitnessGoal;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    // Helper: Calculate Body Mass Index (BMI = weight_kg / (height_m ^ 2))
    public double calculateBMI() {
        if (height <= 0 || weight <= 0) return 0.0;
        double heightInMeters = height / 100.0;
        return weight / (heightInMeters * heightInMeters);
    }

    public String getBMICategory() {
        double bmi = calculateBMI();
        if (bmi <= 0) return "N/A";
        if (bmi < 18.5) return "Underweight";
        if (bmi < 24.9) return "Normal weight";
        if (bmi < 29.9) return "Overweight";
        return "Obese";
    }
}