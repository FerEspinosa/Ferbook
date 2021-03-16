package com.ferbook.models;

public class Message {

    private String id;
    private String senderId;
    private String receiverId;
    private String chatId;
    private String message;
    private long timestamp;
    private boolean viewed;

    public Message () { }

    public Message(String id, String senderId, String receiverId, String chatId, String message, long timestamp, boolean viewed) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.chatId = chatId;
        this.message = message;
        this.timestamp = timestamp;
        this.viewed = viewed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isViewed() {
        return viewed;
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }
}
