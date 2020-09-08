package com.skiplab.theselproject.models;

public class Profile {

    String user_id, description;
    long num_of_consultations, num_of_payments;
    double total;

    public Profile() {
    }

    public Profile(String user_id, String description, long num_of_consultations, long num_of_payments, double total) {
        this.user_id = user_id;
        this.description = description;
        this.num_of_consultations = num_of_consultations;
        this.num_of_payments = num_of_payments;
        this.total = total;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getNum_of_consultations() {
        return num_of_consultations;
    }

    public void setNum_of_consultations(long num_of_consultations) {
        this.num_of_consultations = num_of_consultations;
    }

    public long getNum_of_payments() {
        return num_of_payments;
    }

    public void setNum_of_payments(long num_of_payments) {
        this.num_of_payments = num_of_payments;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
