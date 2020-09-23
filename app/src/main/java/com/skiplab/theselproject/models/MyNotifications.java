package com.skiplab.theselproject.models;

import com.google.firebase.firestore.FieldValue;

public class MyNotifications {
    private String uid, title, content;
    private String timestamp;
    private boolean read;

    public MyNotifications() {
    }

    public MyNotifications(String uid, String title, String content, String timestamp, boolean read) {
        this.uid = uid;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.read = read;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
