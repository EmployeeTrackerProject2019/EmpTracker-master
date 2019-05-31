package com.employee.employeetracker.models;

public class Employee {
    private String userId;
    private String userName;
    private Long checkInTimeStamp;
    private String checkInPhoto;
    private Long checkOutTimeStamp;
    private String checkOutPhoto;
    private String typeOfShift;
    private String dutyPost;
    private String location;

    private String fullName, email, phone, image, history;
    private String lastName;
    private String firstName;
    private long lastSeen;
    private long getCheckInCount;
    private long getCheckOutCount;

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

    public Employee() {
    }

    public Employee(String userId, String userName, Long checkInTimeStamp, String checkInPhoto,
                    Long checkOutTimeStamp, String checkOutPhoto, String typeOfShift, String dutyPost, String location) {
        this.userId = userId;
        this.userName = userName;
        this.checkInTimeStamp = checkInTimeStamp;
        this.checkInPhoto = checkInPhoto;
        this.checkOutTimeStamp = checkOutTimeStamp;
        this.checkOutPhoto = checkOutPhoto;
        this.typeOfShift = typeOfShift;
        this.dutyPost = dutyPost;
        this.location = location;
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

    public Long getCheckInTimeStamp() {
        return checkInTimeStamp;
    }

    public void setCheckInTimeStamp(Long checkInTimeStamp) {
        this.checkInTimeStamp = checkInTimeStamp;
    }

    public String getCheckInPhoto() {
        return checkInPhoto;
    }

    public void setCheckInPhoto(String checkInPhoto) {
        this.checkInPhoto = checkInPhoto;
    }

    public Long getCheckOutTimeStamp() {
        return checkOutTimeStamp;
    }

    public void setCheckOutTimeStamp(Long checkOutTimeStamp) {
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


}
