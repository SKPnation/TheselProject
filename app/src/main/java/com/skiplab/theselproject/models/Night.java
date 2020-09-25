package com.skiplab.theselproject.models;

public class Night {
    String nine_pm, ten_pm, eleven_pm;
    String slot;
    String uid;

    public Night() {
    }

    public Night(String nine_pm, String ten_pm, String eleven_pm, String slot, String uid) {
        this.nine_pm = nine_pm;
        this.ten_pm = ten_pm;
        this.eleven_pm = eleven_pm;
        this.slot = slot;
        this.uid = uid;
    }

    public String getNine_pm() {
        return nine_pm;
    }

    public void setNine_pm(String nine_pm) {
        this.nine_pm = nine_pm;
    }

    public String getTen_pm() {
        return ten_pm;
    }

    public void setTen_pm(String ten_pm) {
        this.ten_pm = ten_pm;
    }

    public String getEleven_pm() {
        return eleven_pm;
    }

    public void setEleven_pm(String eleven_pm) {
        this.eleven_pm = eleven_pm;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
