package com.employee.employeetracker.models;


public class Users {
    private String userId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String occupation;
    private String image;
    private String phone;
    private double latitude;
    private double longitude;
    private String about;
    private String reference;
    private long timeStamp;
    private String dutyPost;
    private String typeOfShift;


    public Users(String userId, String firstName, String lastName, String fullName, String email,
                 String phone, String about, String image) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.about = about;
        this.image = image;
    }

    public String getDutyPost() {
        return dutyPost;
    }

    public void setDutyPost(String dutyPost) {
        this.dutyPost = dutyPost;
    }

    public String getTypeOfShift() {
        return typeOfShift;
    }

    public void setTypeOfShift(String typeOfShift) {
        this.typeOfShift = typeOfShift;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

}
