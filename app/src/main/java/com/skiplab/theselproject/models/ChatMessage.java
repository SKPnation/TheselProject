package com.skiplab.theselproject.models;

public class ChatMessage {
    private String message;
    private String sender_id, receiver_id;
    private String chatroom_id;
    private String timestamp;
    private String type;

    public ChatMessage() {
    }

    public ChatMessage(String message, String sender_id, String receiver_id, String chatroom_id, String timestamp, String type) {
        this.message = message;
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.chatroom_id = chatroom_id;
        this.timestamp = timestamp;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
