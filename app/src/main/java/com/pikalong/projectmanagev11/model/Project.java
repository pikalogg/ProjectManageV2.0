package com.pikalong.projectmanagev11.model;

import java.util.List;

public class Project {
    String id;
    String uId;
    String usId;
    String tasksId;
    String title, des, image, timestamp, files, imgFiles;
    int status;

    public Project(String id, String uId, String usId, String tasksId, String title, String des, String image, String timestamp, String files, String imgFiles, int status) {
        this.id = id;
        this.uId = uId;
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
        this.uId = project.uId;
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

    public String getUid() {
        return uId;
    }

    public void setUid(String uId) {
        this.uId = uId;
    }

    public String getUsId() {
        return usId;
    }

    public void setUsId(String usId) {
        this.usId = usId;
    }

    public String getTasksId() {
        return tasksId;
    }

    public void setTasksId(String tasksId) {
        this.tasksId = tasksId;
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
