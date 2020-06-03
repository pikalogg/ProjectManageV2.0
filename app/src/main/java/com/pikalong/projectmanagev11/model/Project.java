package com.pikalong.projectmanagev11.model;

import java.util.List;

public class Project {
    String uid;
    String usId;
    String tasksId;
    String title, des, image, timestamp, file;
    int status;

    public Project(String uid, String usId, String tasksId, String title, String des, String image, String timestamp, String file, int status) {
        this.uid = uid;
        this.usId = usId;
        this.tasksId = tasksId;
        this.title = title;
        this.des = des;
        this.image = image;
        this.timestamp = timestamp;
        this.file = file;
        this.status = status;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
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
