package org.example.agents.Entity;

public class Flight {

    private String flightId;
    private String from;
    private String to;
    private String departureDate;
    private double initialPrice;
    private double price;
    private String airline;

    public Flight(String flightId, String from, String to, String departureDate, double initialPrice, double price, String airline) {
        this.flightId = flightId;
        this.from = from;
        this.to = to;
        this.departureDate = departureDate;
        this.initialPrice = initialPrice;
        this.price = price;
        this.airline = airline;
    }

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
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

    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public double getInitialPrice() {
        return initialPrice;
    }

    public void setInitialPrice(double initialPrice) {
        this.initialPrice = initialPrice;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "flightId='" + flightId + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", departureDate='" + departureDate + '\'' +
                ", initialPrice=" + initialPrice +
                ", price=" + price +
                ", airline='" + airline + '\'' +
                '}';
    }
}
