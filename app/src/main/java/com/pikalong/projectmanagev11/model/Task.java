package com.pikalong.projectmanagev11.model;

import java.sql.Time;

public class Task {
    String id;
    String uId;
    String usId;
    String projectId;
    String leadName;
    String title, des, image, timestamp, files, imgFiles;
    int status;

    public Task(String id, String uId, String usId, String projectId, String leadName, String title, String des, String image, String timestamp, String files, String imgFiles, int status) {
        this.id = id;
        this.uId = uId;
        this.usId = usId;
        this.projectId = projectId;
        this.leadName = leadName;
        this.title = title;
        this.des = des;
        this.image = image;
        this.timestamp = timestamp;
        this.files = files;
        this.imgFiles = imgFiles;
        this.status = status;
    }

    public Task() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getUsId() {
        return usId;
    }

    public void setUsId(String usId) {
        this.usId = usId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getLeadName() {
        return leadName;
    }

    public void setLeadName(String leadName) {
        this.leadName = leadName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getFiles() {
        return files;
    }

    public void setFiles(String files) {
        this.files = files;
    }

    public String getImgFiles() {
        return imgFiles;
    }

    public void setImgFiles(String imgFiles) {
        this.imgFiles = imgFiles;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
