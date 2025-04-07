package com.example.carpoolingapp;



import java.util.Date;

public class Message {
    private String sender;
    private String text;
    private Date timestamp;

    public Message() {
    }

    public Message(String sender, String text, Date timestamp) {
        this.sender = sender;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}