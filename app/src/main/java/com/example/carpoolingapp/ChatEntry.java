package com.example.carpoolingapp;

import java.io.Serializable;

public class ChatEntry implements Serializable {
    private final String driverName;
    private final String lastMessagePreview;

    public ChatEntry(String driverName, String lastMessagePreview) {
        this.driverName = driverName;
        this.lastMessagePreview = lastMessagePreview;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getLastMessagePreview() {
        return lastMessagePreview;
    }
}
