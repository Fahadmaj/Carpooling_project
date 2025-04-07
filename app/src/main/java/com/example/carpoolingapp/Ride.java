package com.example.carpoolingapp;

public class Ride {
    public String driverName;
    public String destination;
    public String status; // "none", "requested", "accepted", or "declined"

    public Ride(String driverName, String destination) {
        this.driverName = driverName;
        this.destination = destination;
        this.status = "none"; // default status
    }
}
