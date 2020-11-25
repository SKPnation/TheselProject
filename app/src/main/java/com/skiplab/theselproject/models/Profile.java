package com.skiplab.theselproject.models;

public class Profile {

    String user_id, description, appt_duration;
    long appointments, instants, instant_cost, appointment_cost;

    public Profile() {
    }

    public Profile(String user_id, String description, String appt_duration, long appointments, long instants, long instant_cost, long appointment_cost) {
        this.user_id = user_id;
        this.description = description;
        this.appt_duration = appt_duration;
        this.appointments = appointments;
        this.instants = instants;
        this.instant_cost = instant_cost;
        this.appointment_cost = appointment_cost;
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

    public String getAppt_duration() {
        return appt_duration;
    }

    public void setAppt_duration(String appt_duration) {
        this.appt_duration = appt_duration;
    }

    public long getAppointments() {
        return appointments;
    }

    public void setAppointments(long appointments) {
        this.appointments = appointments;
    }

    public long getInstants() {
        return instants;
    }

    public void setInstants(long instants) {
        this.instants = instants;
    }

    public long getInstant_cost() {
        return instant_cost;
    }

    public void setInstant_cost(long instant_cost) {
        this.instant_cost = instant_cost;
    }

    public long getAppointment_cost() {
        return appointment_cost;
    }

    public void setAppointment_cost(long appointment_cost) {
        this.appointment_cost = appointment_cost;
    }
}
