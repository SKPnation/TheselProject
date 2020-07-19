package com.skiplab.theselproject.models;

public class Requests {
    String counsellor_name, counsellor_id, client_id, client_name, timestamp, request_time, status, plan, timer;

    public Requests() {
    }

    public Requests(String counsellor_name, String counsellor_id, String client_id, String client_name, String timestamp, String request_time, String status, String plan, String timer) {
        this.counsellor_name = counsellor_name;
        this.counsellor_id = counsellor_id;
        this.client_id = client_id;
        this.client_name = client_name;
        this.timestamp = timestamp;
        this.request_time = request_time;
        this.status = status;
        this.plan = plan;
        this.timer = timer;
    }

    public String getCounsellor_name() {
        return counsellor_name;
    }

    public void setCounsellor_name(String counsellor_name) {
        this.counsellor_name = counsellor_name;
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

    public String getClient_name() {
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getRequest_time() {
        return request_time;
    }

    public void setRequest_time(String request_time) {
        this.request_time = request_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }
}
