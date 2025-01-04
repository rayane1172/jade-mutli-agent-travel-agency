package org.example.agents.Entity;

import java.util.Date;

public class Flight {
    private String flightNumber;
    private String from; // Departure location
    private String to;   // Destination location
    private String date;   // Date of the flight
    private int totalSeats; // Total number of available seats
    private String airlineName;
    private double price;

    public Flight(String flightNumber, String from, String to, String date, int totalSeats,String airlineName,double price) {
        this.flightNumber = flightNumber;
        this.from = from;
        this.to = to;
        this.date = date;
        this.totalSeats = totalSeats;
        this.airlineName = airlineName;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getAirlineName() {
        return airlineName;
    }

    public void setAirlineName(String airlineName) {
        this.airlineName = airlineName;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getDate() {
        return date;
    }
    public boolean hasAvailableSeats() {
        return this.totalSeats > 0; // Check if there are available seats
    }

    // Method to decrease available seats by the number of bookings
    public boolean decreaseSeats(int numberOfSeats) {
        if (numberOfSeats <= totalSeats) {
            totalSeats -= numberOfSeats;
            return true; // Seats successfully decreased
        } else {
            return false; // Not enough seats available
        }
    }
    @Override
    public String toString() {
        return "Flight " + flightNumber + " from " + from + " to " + to + " on " + date + ", Airline name : "+airlineName+ ", price: "+price;
    }

    // Check if the flight matches the request based on from, to, and date
    public boolean matches(String from, String to, String date) {
        return this.from.equals(from) && this.to.equals(to) && this.date.equals(date);
    }
}
