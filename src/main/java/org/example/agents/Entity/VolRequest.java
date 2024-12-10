package org.example.agents.Entity;

import java.io.Serializable;

public class VolRequest implements Serializable {

    private String from;
    private String to;
    private String departureDate;
    private String returnDate;
    private double budgetMin;
    private double budgetMax;

    public VolRequest(String from, String to, String departureDate, String returnDate, double budgetMin, double budgetMax) {
        this.from = from;
        this.to = to;
        this.departureDate = departureDate;
        this.returnDate = returnDate;
        this.budgetMin = budgetMin;
        this.budgetMax = budgetMax;
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

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public double getBudgetMin() {
        return budgetMin;
    }

    public void setBudgetMin(double budgetMin) {
        this.budgetMin = budgetMin;
    }

    public double getBudgetMax() {
        return budgetMax;
    }

    public void setBudgetMax(double budgetMax) {
        this.budgetMax = budgetMax;
    }

    @Override
    public String toString() {
        return "VolRequest{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", departureDate='" + departureDate + '\'' +
                ", returnDate='" + returnDate + '\'' +
                ", budgetMin=" + budgetMin +
                ", budgetMax=" + budgetMax +
                '}';
    }
}
