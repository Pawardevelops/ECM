package com.example.mec.services;

public class Elections {
    private String title;
    private String description;
    private String date;
    private String time;
    private String section;
    private String department;

    // Default constructor required for calls to DataSnapshot.getValue(Election.class)
    public Elections() {}

    public Elections(String title, String description, String date, String time, String section, String department) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.section = section;
        this.department = department;
    }

    // Getters and setters (optional for Firebase Realtime Database)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
