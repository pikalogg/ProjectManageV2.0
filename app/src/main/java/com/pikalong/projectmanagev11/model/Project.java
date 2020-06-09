package com.pikalong.projectmanagev11.model;

import java.util.List;

public class Project {
    String id;
    String uid;
    String usId;
    String tasksId;
    String title, des, image, timestamp, files, imgFiles;
    int status;

    public Project(String id, String uid, String usId, String tasksId, String title, String des, String image, String timestamp, String files, String imgFiles, int status) {
        this.id = id;
        this.uid = uid;
        this.usId = usId;
        this.tasksId = tasksId;
        this.title = title;
        this.des = des;
        this.image = image;
        this.timestamp = timestamp;
        this.files = files;
        this.imgFiles = imgFiles;
        this.status = status;
    }

    public Project(Project project) {
        this.id = project.id;
        this.uid = project.uid;
        this.usId = project.usId;
        this.tasksId = project.tasksId;
        this.title = project.title;
        this.des = project.des;
        this.image = project.image;
        this.timestamp = project.timestamp;
        this.files = project.files;
        this.imgFiles = project.imgFiles;
        this.status = project.status;
    }

    public Project() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTasksId() {
        return tasksId;
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

    public void setTasksId(String tasksId) {
        this.tasksId = tasksId;
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
