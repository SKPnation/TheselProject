package com.skiplab.theselproject.models;

public class Appointment {
    private String counsellor_id, client_id;
    private String appointment_id;
    private String timestamp;
    private String booked_date;
    private String booked_time;
    private String slot;
    private boolean absent;
    private int num_messages;

    public Appointment() {
    }

    public Appointment(String counsellor_id, String client_id, String appointment_id, String timestamp, String booked_date, String booked_time, String slot, boolean absent, int num_messages) {
        this.counsellor_id = counsellor_id;
        this.client_id = client_id;
        this.appointment_id = appointment_id;
        this.timestamp = timestamp;
        this.booked_date = booked_date;
        this.booked_time = booked_time;
        this.slot = slot;
        this.absent = absent;
        this.num_messages = num_messages;
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

    public String getAppointment_id() {
        return appointment_id;
    }

    public void setAppointment_id(String appointment_id) {
        this.appointment_id = appointment_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getBooked_date() {
        return booked_date;
    }

    public void setBooked_date(String booked_date) {
        this.booked_date = booked_date;
    }

    public String getBooked_time() {
        return booked_time;
    }

    public void setBooked_time(String booked_time) {
        this.booked_time = booked_time;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public boolean isAbsent() {
        return absent;
    }

    public void setAbsent(boolean absent) {
        this.absent = absent;
    }

    public int getNum_messages() {
        return num_messages;
    }

    public void setNum_messages(int num_messages) {
        this.num_messages = num_messages;
    }
}
