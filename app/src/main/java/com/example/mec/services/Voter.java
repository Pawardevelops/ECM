package com.example.mec.services;

public class Voter {
    public String firstName;
    public String lastName;
    public String email;
    public String registrationNo;
    public String imageUrl;
    public String department;
    public String section;
    public String course;
    public String role;
    public String semester; // Add semester field



    public Voter() {
        // Default constructor required for calls to DataSnapshot.getValue(Voter.class)
    }

    public Voter(String firstName, String lastName, String email, String registrationNo, String imageUrl, String department, String section, String course, String semester, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.registrationNo = registrationNo;
        this.imageUrl = imageUrl;
        this.department = department;
        this.section = section;
        this.course = course;
        this.semester = semester; // Initialize semester
        this.role = "voter";
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getSection() {
        return section;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getRole(String role){
        return role;
    }
    public void setRole(String role){
        this.role = role;
    }
}

