package com.skiplab.theselproject.models;

public class Early {
    String five_am, six_am;
    String slot;
    String uid;

    public Early() {
    }

    public Early(String five_am, String six_am, String slot, String uid) {
        this.five_am = five_am;
        this.six_am = six_am;
        this.slot = slot;
        this.uid = uid;
    }

    public String getFive_am() {
        return five_am;
    }

    public void setFive_am(String five_am) {
        this.five_am = five_am;
    }

    public String getSix_am() {
        return six_am;
    }

    public void setSix_am(String six_am) {
        this.six_am = six_am;
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
