package com.employee.employeetracker.models;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Report implements Serializable {
    @SerializedName("userId")
    private String userId;
    @SerializedName("fullName")
    private String fullName;
    @SerializedName("image")
    private String image;
    @SerializedName("timeStamp")
    private Long timeStamp;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("comments")
    private String comments;

    public Report() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

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

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
