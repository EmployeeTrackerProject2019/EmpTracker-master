package com.employee.employeetracker.models;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Users implements Serializable {
    @SerializedName("userId")
    private String userId;
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("fullName")
    private String fullName;
    @SerializedName("email")
    private String email;
    @SerializedName("pass")
    private String pass;
    @SerializedName("occupation")
    private String occupation;
    @SerializedName("image")
    private String image;
    @SerializedName("phone")
    private String phone;
    @SerializedName("latitude")
    private double latitude;
    @SerializedName("longitude")
    private double longitude;
    @SerializedName("about")
    private String about;
    @SerializedName("reference")
    private String reference;
    @SerializedName("timeStamp")
    private long timeStamp;
    @SerializedName("dutyPost")
    private String dutyPost;
    @SerializedName("typeOfShift")
    private String typeOfShift;

    public Users() {

    }

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

    public Users(String userId, String firstName, String lastName, String fullName, String email,
                 String pass,
                 String phone, String about, String image) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.email = email;
        this.pass = pass;
        this.image = image;
        this.phone = phone;

    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
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
