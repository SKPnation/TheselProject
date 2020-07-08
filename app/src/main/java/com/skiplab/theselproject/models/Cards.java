package com.skiplab.theselproject.models;

public class Cards {
    String uid, cardName1, cardNumber1, cardName2, cardNumber2, cardName3, cardNumber3;

    public Cards() {
    }

    public Cards(String uid, String cardName1, String cardNumber1, String cardName2, String cardNumber2, String cardName3, String cardNumber3) {
        this.uid = uid;
        this.cardName1 = cardName1;
        this.cardNumber1 = cardNumber1;
        this.cardName2 = cardName2;
        this.cardNumber2 = cardNumber2;
        this.cardName3 = cardName3;
        this.cardNumber3 = cardNumber3;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCardName1() {
        return cardName1;
    }

    public void setCardName1(String cardName1) {
        this.cardName1 = cardName1;
    }

    public String getCardNumber1() {
        return cardNumber1;
    }

    public void setCardNumber1(String cardNumber1) {
        this.cardNumber1 = cardNumber1;
    }

    public String getCardName2() {
        return cardName2;
    }

    public void setCardName2(String cardName2) {
        this.cardName2 = cardName2;
    }

    public String getCardNumber2() {
        return cardNumber2;
    }

    public void setCardNumber2(String cardNumber2) {
        this.cardNumber2 = cardNumber2;
    }

    public String getCardName3() {
        return cardName3;
    }

    public void setCardName3(String cardName3) {
        this.cardName3 = cardName3;
    }

    public String getCardNumber3() {
        return cardNumber3;
    }

    public void setCardNumber3(String cardNumber3) {
        this.cardNumber3 = cardNumber3;
    }
}
