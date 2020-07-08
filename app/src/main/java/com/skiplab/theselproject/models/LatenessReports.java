package com.skiplab.theselproject.models;

public class LatenessReports {

    String counsellor_name, counsellor_id, cost, report_message, client_id, client_email, client_phone, client_name, timestamp;

    public LatenessReports() {
    }

    public LatenessReports(String counsellor_name, String counsellor_id, String cost, String report_message, String client_id, String client_email, String client_phone, String client_name, String timestamp) {
        this.counsellor_name = counsellor_name;
        this.counsellor_id = counsellor_id;
        this.cost = cost;
        this.report_message = report_message;
        this.client_id = client_id;
        this.client_email = client_email;
        this.client_phone = client_phone;
        this.client_name = client_name;
        this.timestamp = timestamp;
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

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getReport_message() {
        return report_message;
    }

    public void setReport_message(String report_message) {
        this.report_message = report_message;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getClient_email() {
        return client_email;
    }

    public void setClient_email(String client_email) {
        this.client_email = client_email;
    }

    public String getClient_phone() {
        return client_phone;
    }

    public void setClient_phone(String client_phone) {
        this.client_phone = client_phone;
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
}
