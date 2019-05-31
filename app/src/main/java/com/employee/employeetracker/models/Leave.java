package com.employee.employeetracker.models;


public class Leave {

    private String userId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String image;
    private String phone;
    private String startDate;
    private String endDate;
    private String leaveMsg, leaveResponse;
    private Long timeStamp;

    public Leave() {
    }

    public Leave(String userId, String fullName, String image, String startDate, String endDate,
                 String leaveMsg, String leaveResponse, Long timestamp) {
        this.userId = userId;
        this.fullName = fullName;
        this.image = image;
        this.startDate = startDate;
        this.endDate = endDate;
        this.leaveMsg = leaveMsg;
        this.leaveResponse = leaveResponse;
        this.timeStamp = timestamp;
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getLeaveMsg() {
        return leaveMsg;
    }

    public void setLeaveMsg(String leaveMsg) {
        this.leaveMsg = leaveMsg;
    }

    public String getLeaveResponse() {
        return leaveResponse;
    }

    public void setLeaveResponse(String leaveResponse) {
        this.leaveResponse = leaveResponse;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
