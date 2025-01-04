package org.example.agents.Entity;

import java.io.Serializable;

public class VolRequest implements Serializable {

    private String id; // Identifier for the flight
    private String from; // Departure city
    private String to; // Arrival city
//    private double initialPrice; // Initial price of the ticket
    private double minimumPrice; // Minimum selling price of the ticket
    private String departureDate; // Departure date of the flight
    private int numTickets; // Number of tickets requested
    private int[] passengerAges; // Ages of passengers (for discount calculation)

    public VolRequest() {
    }

    public VolRequest(String id, String from, String to, double minimumPrice, String departureDate, int numTickets, int[] passengerAges) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.minimumPrice = minimumPrice;
        this.departureDate = departureDate;
        this.numTickets = numTickets;
        this.passengerAges = passengerAges;
    }

    @Override
    public String toString() {
        return "VolRequest{" +
                "id='" + id + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", minimumPrice=" + minimumPrice +
                ", departureDate='" + departureDate + '\'' +
                ", numTickets=" + numTickets +
                ", passengerAges=" + (passengerAges != null ? java.util.Arrays.toString(passengerAges) : "null") +
                '}';
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }


    public double getMinimumPrice() {
        return minimumPrice;
    }

    public void setMinimumPrice(double minimumPrice) {
        this.minimumPrice = minimumPrice;
    }


    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public int getNumTickets() {
        return numTickets;
    }

    public void setNumTickets(int numTickets) {
        this.numTickets = numTickets;
    }

    public int[] getPassengerAges() {
        return passengerAges;
    }

    public void setPassengerAges(int[] passengerAges) {
        this.passengerAges = passengerAges;
    }
}