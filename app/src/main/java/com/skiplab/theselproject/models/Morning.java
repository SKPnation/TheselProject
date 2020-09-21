package com.skiplab.theselproject.models;

public class Morning {
    String seven_am, eight_am, nine_am, ten_am, eleven_am;
    String slot;
    String uid;

    public Morning() {
    }

    public Morning(String seven_am, String eight_am, String nine_am, String ten_am, String eleven_am, String slot, String uid) {
        this.seven_am = seven_am;
        this.eight_am = eight_am;
        this.nine_am = nine_am;
        this.ten_am = ten_am;
        this.eleven_am = eleven_am;
        this.slot = slot;
        this.uid = uid;
    }

    public String getSeven_am() {
        return seven_am;
    }

    public void setSeven_am(String seven_am) {
        this.seven_am = seven_am;
    }

    public String getEight_am() {
        return eight_am;
    }

    public void setEight_am(String eight_am) {
        this.eight_am = eight_am;
    }

    public String getNine_am() {
        return nine_am;
    }

    public void setNine_am(String nine_am) {
        this.nine_am = nine_am;
    }

    public String getTen_am() {
        return ten_am;
    }

    public void setTen_am(String ten_am) {
        this.ten_am = ten_am;
    }

    public String getEleven_am() {
        return eleven_am;
    }

    public void setEleven_am(String eleven_am) {
        this.eleven_am = eleven_am;
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
