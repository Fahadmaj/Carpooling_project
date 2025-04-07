// Ride.java
package com.example.carpoolingapp;

public class Ride {
    public String driverName;
    public String destination;
    public String price;
    public String seats;
    public String status;
    public boolean isExpanded;
    public String date;
    public String time;

    public Ride(String driverName, String destination, String price, String seats, String date, String time) {
        this.driverName = driverName;
        this.destination = destination;
        this.price = price;
        this.seats = seats;
        this.isExpanded = false;
        this.status = "none";
        this.date = date;
        this.time = time;
    }
}