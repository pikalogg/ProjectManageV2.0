package com.pikalong.projectmanagev11.model;

import java.sql.Timestamp;

public class ChatMessage {

    private String messageText;
    private String messageUser;
    private String messageTime;
    private  String projectId;

    public ChatMessage(String messageText, String messageUser, String projectId) {
        this.projectId = projectId;
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.messageTime = new Timestamp(System.currentTimeMillis()).toString().substring(0, 19);
    }

    public ChatMessage(){

    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}