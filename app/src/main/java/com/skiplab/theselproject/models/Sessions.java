package com.skiplab.theselproject.models;

public class Sessions {
    String counsellor_id, counsellor_name, client_id;
    long completed, requests;

    public Sessions() {
    }

    public Sessions(String counsellor_id, String counsellor_name, String client_id, long completed, long requests) {
        this.counsellor_id = counsellor_id;
        this.counsellor_name = counsellor_name;
        this.client_id = client_id;
        this.completed = completed;
        this.requests = requests;
    }

    public String getCounsellor_id() {
        return counsellor_id;
    }

    public void setCounsellor_id(String counsellor_id) {
        this.counsellor_id = counsellor_id;
    }

    public String getCounsellor_name() {
        return counsellor_name;
    }

    public void setCounsellor_name(String counsellor_name) {
        this.counsellor_name = counsellor_name;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public long getCompleted() {
        return completed;
    }

    public void setCompleted(long completed) {
        this.completed = completed;
    }

    public long getRequests() {
        return requests;
    }

    public void setRequests(long requests) {
        this.requests = requests;
    }
}
