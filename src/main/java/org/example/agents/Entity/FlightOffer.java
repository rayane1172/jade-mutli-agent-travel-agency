package org.example.agents.Entity;
// FlightOffer class to store flight details
class FlightOffer {
    private String flightId;
    private String airline;
    private double price;

    // Getters and setters
    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "FlightOffer{" +
                "flightId='" + flightId + '\'' +
                ", airline='" + airline + '\'' +
                ", price=" + price +
                '}';
    }
}