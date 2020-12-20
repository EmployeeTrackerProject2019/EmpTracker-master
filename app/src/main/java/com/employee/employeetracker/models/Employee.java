package com.employee.employeetracker.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Employee implements Serializable {
    @SerializedName("email")
    String email;
    @SerializedName("phone")
    String phone;
    @SerializedName("image")
    String image;
    @SerializedName("history")
    String history;
    @SerializedName("userId")
    private String userId;
    @SerializedName("userName")
    private String userName;
    @SerializedName("checkInTimeStamp")
    private String checkInTimeStamp;
    @SerializedName("checkInPhoto")
    private String checkInPhoto;
    @SerializedName("checkOutTimeStamp")
    private String checkOutTimeStamp;
    @SerializedName("checkOutPhoto")
    private String checkOutPhoto;
    @SerializedName("typeOfShift")
    private String typeOfShift;
    @SerializedName("dutyPost")
    private String dutyPost;
    @SerializedName("location")
    private String location;
    @SerializedName("date")
    private String date;
    @SerializedName("fullName")
    private String fullName;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastSeen")
    private long lastSeen;
    @SerializedName("getCheckInCount")
    private long getCheckInCount;
    @SerializedName("getCheckOutCount")
    private long getCheckOutCount;


    public Employee() {
    }

    public Employee(String name, String duty, String shift) {
        this.fullName = name;
        this.dutyPost = duty;
        this.typeOfShift = shift;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCheckInTimeStamp() {
        return checkInTimeStamp;
    }

    public void setCheckInTimeStamp(String checkInTimeStamp) {
        this.checkInTimeStamp = checkInTimeStamp;
    }

    public String getCheckInPhoto() {
        return checkInPhoto;
    }

    public void setCheckInPhoto(String checkInPhoto) {
        this.checkInPhoto = checkInPhoto;
    }

    public String getCheckOutTimeStamp() {
        return checkOutTimeStamp;
    }

    public void setCheckOutTimeStamp(String checkOutTimeStamp) {
        this.checkOutTimeStamp = checkOutTimeStamp;
    }

    public String getCheckOutPhoto() {
        return checkOutPhoto;
    }

    public void setCheckOutPhoto(String checkOutPhoto) {
        this.checkOutPhoto = checkOutPhoto;
    }

    public String getTypeOfShift() {
        return typeOfShift;
    }

    public void setTypeOfShift(String typeOfShift) {
        this.typeOfShift = typeOfShift;
    }

    public String getDutyPost() {
        return dutyPost;
    }

    public void setDutyPost(String dutyPost) {
        this.dutyPost = dutyPost;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public long getGetCheckInCount() {
        return getCheckInCount;
    }

    public void setGetCheckInCount(long getCheckInCount) {
        this.getCheckInCount = getCheckInCount;
    }

    public long getGetCheckOutCount() {
        return getCheckOutCount;
    }

    public void setGetCheckOutCount(long getCheckOutCount) {
        this.getCheckOutCount = getCheckOutCount;
    }
}
