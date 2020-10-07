package com.skiplab.theselproject.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Appointment {
    @ServerTimestamp
    private Date timestamp;
    private String counsellor_id, client_id;
    private String appointment_id;
    private long booked_date;
    private String start_time;
    private String end_time;
    private String slot;
    private String timeType;
    private boolean absent;
    private boolean open;
    private long num_messages;

    public Appointment() {
    }

    public Appointment(Date timestamp, String counsellor_id, String client_id, String appointment_id, long booked_date, String start_time, String end_time, String slot, String timeType, boolean absent, boolean open, long num_messages) {
        this.timestamp = timestamp;
        this.counsellor_id = counsellor_id;
        this.client_id = client_id;
        this.appointment_id = appointment_id;
        this.booked_date = booked_date;
        this.start_time = start_time;
        this.end_time = end_time;
        this.slot = slot;
        this.timeType = timeType;
        this.absent = absent;
        this.open = open;
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

    public long getBooked_date() {
        return booked_date;
    }

    public void setBooked_date(long booked_date) {
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

    public String getTimeType() {
        return timeType;
    }

    public void setTimeType(String timeType) {
        this.timeType = timeType;
    }

    public boolean isAbsent() {
        return absent;
    }

    public void setAbsent(boolean absent) {
        this.absent = absent;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public long getNum_messages() {
        return num_messages;
    }

    public void setNum_messages(long num_messages) {
        this.num_messages = num_messages;
    }
}
