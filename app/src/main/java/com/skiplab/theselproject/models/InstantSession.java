package com.skiplab.theselproject.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class InstantSession {

    private String counsellor_id, client_id;
    private String session_id;
    private String timestamp;
    private long num_messages;
    private long expiryDate;
    private String expiryDay;

    public InstantSession() {
    }

    public InstantSession(String counsellor_id, String client_id, String session_id, String timestamp, long num_messages, long expiryDate, String expiryDay) {
        this.counsellor_id = counsellor_id;
        this.client_id = client_id;
        this.session_id = session_id;
        this.timestamp = timestamp;
        this.num_messages = num_messages;
        this.expiryDate = expiryDate;
        this.expiryDay = expiryDay;
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

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public long getNum_messages() {
        return num_messages;
    }

    public void setNum_messages(long num_messages) {
        this.num_messages = num_messages;
    }

    public long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(long expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getExpiryDay() {
        return expiryDay;
    }

    public void setExpiryDay(String expiryDay) {
        this.expiryDay = expiryDay;
    }
}
