package com.skiplab.theselproject.models;

public class Deposits {
    String uid, referenceNumber, timestamp;
    int amount;

    public Deposits() {
    }

    public Deposits(String uid, String referenceNumber, String timestamp, int amount) {
        this.uid = uid;
        this.referenceNumber = referenceNumber;
        this.timestamp = timestamp;
        this.amount = amount;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
