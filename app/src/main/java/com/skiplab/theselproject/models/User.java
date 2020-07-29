package com.skiplab.theselproject.models;

public class User {
    String uid, username, email, phone, age, profile_photo, bio, isStaff, address, accountNumber, bank, dayTime, nightTime, category1,  category2, category3, onlineStatus, selectedCategory, messaging_token;
    long cost, posts;

    public User() {
    }

    public User(String uid, String username, String email, String phone, String age, String profile_photo, String bio, String isStaff, String address, String accountNumber, String bank, String dayTime, String nightTime, String category1, String category2, String category3, String onlineStatus, String selectedCategory, String messaging_token, long cost, long posts) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.age = age;
        this.profile_photo = profile_photo;
        this.bio = bio;
        this.isStaff = isStaff;
        this.address = address;
        this.accountNumber = accountNumber;
        this.bank = bank;
        this.dayTime = dayTime;
        this.nightTime = nightTime;
        this.category1 = category1;
        this.category2 = category2;
        this.category3 = category3;
        this.onlineStatus = onlineStatus;
        this.selectedCategory = selectedCategory;
        this.messaging_token = messaging_token;
        this.cost = cost;
        this.posts = posts;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getIsStaff() {
        return isStaff;
    }

    public void setIsStaff(String isStaff) {
        this.isStaff = isStaff;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getDayTime() {
        return dayTime;
    }

    public void setDayTime(String dayTime) {
        this.dayTime = dayTime;
    }

    public String getNightTime() {
        return nightTime;
    }

    public void setNightTime(String nightTime) {
        this.nightTime = nightTime;
    }

    public String getCategory1() {
        return category1;
    }

    public void setCategory1(String category1) {
        this.category1 = category1;
    }

    public String getCategory2() {
        return category2;
    }

    public void setCategory2(String category2) {
        this.category2 = category2;
    }

    public String getCategory3() {
        return category3;
    }

    public void setCategory3(String category3) {
        this.category3 = category3;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(String selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public String getMessaging_token() {
        return messaging_token;
    }

    public void setMessaging_token(String messaging_token) {
        this.messaging_token = messaging_token;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public long getPosts() {
        return posts;
    }

    public void setPosts(long posts) {
        this.posts = posts;
    }
}
