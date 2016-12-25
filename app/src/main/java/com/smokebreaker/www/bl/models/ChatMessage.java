package com.smokebreaker.www.bl.models;

import com.google.firebase.database.Exclude;

public class ChatMessage{
    private User from;
    private String uid;
    private String message;
    private long timestamp;

    public ChatMessage() {
    }

    //region g&s

    @Exclude
    public User getFrom() {
        return from;
    }

    @Exclude
    public void setFrom(User from) {
        this.from = from;
        this.uid = from.getUid();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    //endregion
}
