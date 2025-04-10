package com.example.carpoolingapp;

import java.io.Serializable;

public class Trip implements Serializable {
    private final String destination;
    private final double price;
    private final String driver;
    private final String date;
    private final String time;
    private final String tripId;

    public Trip(String destination, double price, String driver, String date, String time, String tripId) {
        this.destination = destination;
        this.price = price;
        this.driver = driver;
        this.date = date;
        this.time = time;
        this.tripId = tripId;
    }

    public String getDestination() {
        return destination;
    }

    public double getPrice() {
        return price;
    }

    public String getDriver() {
        return driver;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getTripId() {
        return tripId;
    }
}
