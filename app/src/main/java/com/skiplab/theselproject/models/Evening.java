package com.skiplab.theselproject.models;

public class Evening {
    String five_pm, six_pm, seven_pm, eight_pm;
    String slot;
    String uid;

    public Evening() {
    }

    public Evening(String five_pm, String six_pm, String seven_pm, String eight_pm, String slot, String uid) {
        this.five_pm = five_pm;
        this.six_pm = six_pm;
        this.seven_pm = seven_pm;
        this.eight_pm = eight_pm;
        this.slot = slot;
        this.uid = uid;
    }

    public String getFive_pm() {
        return five_pm;
    }

    public void setFive_pm(String five_pm) {
        this.five_pm = five_pm;
    }

    public String getSix_pm() {
        return six_pm;
    }

    public void setSix_pm(String six_pm) {
        this.six_pm = six_pm;
    }

    public String getSeven_pm() {
        return seven_pm;
    }

    public void setSeven_pm(String seven_pm) {
        this.seven_pm = seven_pm;
    }

    public String getEight_pm() {
        return eight_pm;
    }

    public void setEight_pm(String eight_pm) {
        this.eight_pm = eight_pm;
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
