package com.skiplab.theselproject.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class MyNotifications {
    @ServerTimestamp
    private Date timestamp;
    private String counsellor_id, client_id;
    private String title, category, content;
    private String expiry_date, appointment_time;
    private boolean read;

    public MyNotifications() {
    }

    public MyNotifications(Date timestamp, String counsellor_id, String client_id, String title, String category, String content, String expiry_date, String appointment_time, boolean read) {
        this.timestamp = timestamp;
        this.counsellor_id = counsellor_id;
        this.client_id = client_id;
        this.title = title;
        this.category = category;
        this.content = content;
        this.expiry_date = expiry_date;
        this.appointment_time = appointment_time;
        this.read = read;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getCounsellor_id() {
        return counsellor_id;
    }

    public void setCounsellor_id(String counsellor_id) {
        this.counsellor_id = counsellor_id;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(String expiry_date) {
        this.expiry_date = expiry_date;
    }

    public String getAppointment_time() {
        return appointment_time;
    }

    public void setAppointment_time(String appointment_time) {
        this.appointment_time = appointment_time;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}