package org.example.agents.Entity;

import java.io.Serializable;
import java.util.Date;

public class VolRequest implements Serializable {

    private String from;
    private String to;
    private String departureDate;
    private String returnDate;


    public VolRequest() {
    }
    public VolRequest(String from, String to, String departureDate, String returnDate){
        this.from = from;
        this.to = to;
        this.departureDate = departureDate;
        this.returnDate = returnDate;
    }

    @Override
    public String toString() {
        return "VolRequest{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", departureDate=" + departureDate +
                ", returnDate=" + returnDate +
                '}';
    }

    public String getReturnDate() {
        return returnDate;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }
}
