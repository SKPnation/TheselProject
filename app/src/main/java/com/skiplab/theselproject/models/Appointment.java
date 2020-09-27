package com.skiplab.theselproject.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Appointment {
    @ServerTimestamp
    private Date timestamp;
    private String counsellor_id, client_id;
    private String appointment_id;
    private String booked_date;
    private String start_time;
    private String end_time;
    private String slot;
    private boolean absent;
    private int num_messages;

    public Appointment() {
    }

    public Appointment(Date timestamp, String counsellor_id, String client_id, String appointment_id, String booked_date, String start_time, String end_time, String slot, boolean absent, int num_messages) {
        this.timestamp = timestamp;
        this.counsellor_id = counsellor_id;
        this.client_id = client_id;
        this.appointment_id = appointment_id;
        this.booked_date = booked_date;
        this.start_time = start_time;
        this.end_time = end_time;
        this.slot = slot;
        this.absent = absent;
        this.num_messages = num_messages;
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

    public String getAppointment_id() {
        return appointment_id;
    }

    public void setAppointment_id(String appointment_id) {
        this.appointment_id = appointment_id;
    }

    public String getBooked_date() {
        return booked_date;
    }

    public void setBooked_date(String booked_date) {
        this.booked_date = booked_date;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
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
