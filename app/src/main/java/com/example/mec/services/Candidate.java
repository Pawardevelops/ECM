package com.example.mec.services;

public class Candidate {
    public String firstName, lastName, email, registrationNo, slogan, imageUrl;
    public String department, course, section, semester, status="Pending";
    public String userType;

    public Candidate(String firstName, String lastName, String email, String registrationNo, String slogan, String imageUrl,
                     String department, String course, String section, String semester, String userType, String status) {
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
        this.status=status;
    }
    public Candidate(String firstName, String lastName, String email, String registrationNo, String slogan, String imageUrl,
                     String department, String course, String section, String semester, String userType) {
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

    }

    public String getFirstName() {
        return firstName;
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
