package com.skiplab.theselproject.models;

public class Wallet {
    String uid;
    int balance;

    public Wallet() {
    }

    public Wallet(String uid, int balance) {
        this.uid = uid;
        this.balance = balance;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
