package com.skiplab.theselproject.models;

public class Afternoon {
    String twelve_pm, one_pm, two_pm, three_pm, four_pm;
    String slot;
    String uid;

    public Afternoon() {
    }

    public Afternoon(String twelve_pm, String one_pm, String two_pm, String three_pm, String four_pm, String slot, String uid) {
        this.twelve_pm = twelve_pm;
        this.one_pm = one_pm;
        this.two_pm = two_pm;
        this.three_pm = three_pm;
        this.four_pm = four_pm;
        this.slot = slot;
        this.uid = uid;
    }

    public String getTwelve_pm() {
        return twelve_pm;
    }

    public void setTwelve_pm(String twelve_pm) {
        this.twelve_pm = twelve_pm;
    }

    public String getOne_pm() {
        return one_pm;
    }

    public void setOne_pm(String one_pm) {
        this.one_pm = one_pm;
    }

    public String getTwo_pm() {
        return two_pm;
    }

    public void setTwo_pm(String two_pm) {
        this.two_pm = two_pm;
    }

    public String getThree_pm() {
        return three_pm;
    }

    public void setThree_pm(String three_pm) {
        this.three_pm = three_pm;
    }

    public String getFour_pm() {
        return four_pm;
    }

    public void setFour_pm(String four_pm) {
        this.four_pm = four_pm;
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
