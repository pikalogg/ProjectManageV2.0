package com.pikalong.projectmanagev11.model;

import java.sql.Time;

public class Task {
    String uid;
    String usId;
    String projectId;
    String title, des, image, timestamp, files, imgFiles;
    int status;

    public Task(String uid, String usId, String projectId, String title, String des, String image, String timestamp, String files, String imgFiles, int status) {
        this.uid = uid;
        this.usId = usId;
        this.projectId = projectId;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public String getUsId() {
        return usId;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getTitle() {
        return title;
    }

    public String getDes() {
        return des;
    }

    public String getImage() {
        return image;
    }

    public int getStatus() {
        return status;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUsId(String usId) {
        this.usId = usId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
