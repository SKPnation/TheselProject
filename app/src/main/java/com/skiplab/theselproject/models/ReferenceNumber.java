package com.skiplab.theselproject.models;

public class ReferenceNumber {
    String uid, referenceNumber, timestamp, uName, uDp;
    Long amount;

    public ReferenceNumber() {
    }

    public ReferenceNumber(String uid, String referenceNumber, String timestamp, String uName, String uDp, Long amount) {
        this.uid = uid;
        this.referenceNumber = referenceNumber;
        this.timestamp = timestamp;
        this.uName = uName;
        this.uDp = uDp;
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

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getuDp() {
        return uDp;
    }

    public void setuDp(String uDp) {
        this.uDp = uDp;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}
