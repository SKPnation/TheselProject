package com.skiplab.theselproject.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ChatRoom {

    private String creator_name;
    private String counsellor_id, creator_id;
    private String creator_dp;
    private String chatroom_id;
    private String timestamp;
    private int num_messages;


    public ChatRoom() {
    }

    public ChatRoom(String creator_name, String counsellor_id, String creator_id, String creator_dp, String chatroom_id, String timestamp, int num_messages) {
        this.creator_name = creator_name;
        this.counsellor_id = counsellor_id;
        this.creator_id = creator_id;
        this.creator_dp = creator_dp;
        this.chatroom_id = chatroom_id;
        this.timestamp = timestamp;
        this.num_messages = num_messages;
    }

    public String getCreator_name() {
        return creator_name;
    }

    public void setCreator_name(String creator_name) {
        this.creator_name = creator_name;
    }

    public String getCounsellor_id() {
        return counsellor_id;
    }

    public void setCounsellor_id(String counsellor_id) {
        this.counsellor_id = counsellor_id;
    }

    public String getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(String creator_id) {
        this.creator_id = creator_id;
    }

    public String getCreator_dp() {
        return creator_dp;
    }

    public void setCreator_dp(String creator_dp) {
        this.creator_dp = creator_dp;
    }

    public String getChatroom_id() {
        return chatroom_id;
    }

    public void setChatroom_id(String chatroom_id) {
        this.chatroom_id = chatroom_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getNum_messages() {
        return num_messages;
    }

    public void setNum_messages(int num_messages) {
        this.num_messages = num_messages;
    }
}
