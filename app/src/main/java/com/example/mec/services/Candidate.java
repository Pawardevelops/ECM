package com.example.mec.services;

public class Candidate {
    public String uid; // Add this field for UID
    public String firstName, lastName, email, registrationNo, slogan, imageUrl;
    public String department, course, section, semester, status = "Pending";
    public String userType;
    public int voteCount;

    public Candidate(String uid, String firstName, String lastName, String email, String registrationNo, String slogan, String imageUrl,
                     String department, String course, String section, String semester, String userType, String status,int votes) {
        this.uid = uid; // Initialize UID
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.registrationNo = registrationNo;
        this.slogan = slogan;
        this.imageUrl = imageUrl;
        this.department = department;
        this.course = course;
        this.section = section;
        this.semester = semester;
        this.userType = userType;
        this.status = status;
        this.voteCount = votes;

    }

    public Candidate(String uid, String firstName, String lastName, String email, String registrationNo, String slogan, String imageUrl,
                     String department, String course, String section, String semester, String userType) {
        this.uid = uid; // Initialize UID
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.registrationNo = registrationNo;
        this.slogan = slogan;
        this.imageUrl = imageUrl;
        this.department = department;
        this.course = course;
        this.section = section;
        this.semester = semester;
        this.userType = userType;
    }

    public Candidate() {
        // Default constructor required for calls to DataSnapshot.getValue(Candidate.class)
    }




    public Candidate(String uid, String firstName, String lastName, String email, String registrationNo, String slogan, String imageUrl, String department, String course, String section, String semester, String userType, int votes) {
        this.voteCount = votes;
        this.userType = userType;
        this.status = status;
        this.semester = semester;
        this.section = section;
        this.course = course;
        this.department = department;
        this.imageUrl = imageUrl;
        this.slogan = slogan;
        this.registrationNo = registrationNo;
        this.email = email;
        this.lastName = lastName;
        this.firstName = firstName;
        this.uid = uid;

    }

    // Getter and Setter for UID
    public String getUid() {
        return uid;
    }

    public int getVotes() {
        return voteCount;
    }

    public void setVotes(int votes) {
        this.voteCount = votes;
    }


    public void setUid(String uid) {
        this.uid = uid;
    }

    // Other getters and setters
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public String getSlogan() {
        return slogan;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDepartment() {
        return department;
    }

    public String getCourse() {
        return course;
    }

    public String getSection() {
        return section;
    }

    public String getSemester() {
        return semester;
    }

    public String getUserType() {
        return userType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
