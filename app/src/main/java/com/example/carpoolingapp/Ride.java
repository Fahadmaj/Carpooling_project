package com.example.carpoolingapp;

public class Ride {
    public String driverName;
    public String destination;
    public String price;
    public String seats;
    public String status;
    public boolean isExpanded;

    public Ride(String driverName, String destination, String price, String seats) {
        this.driverName = driverName;
        this.destination = destination;
        this.price = price;
        this.seats = seats;
        this.isExpanded = false;
        this.status = "none";
    }
}

