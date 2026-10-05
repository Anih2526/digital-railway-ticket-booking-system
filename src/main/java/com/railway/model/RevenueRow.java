package com.railway.model;

public class RevenueRow {
    private String journeyDate;
    private String route;
    private int bookings;
    private double revenue;

    public RevenueRow(String journeyDate, String route, int bookings, double revenue) {
        this.journeyDate = journeyDate;
        this.route = route;
        this.bookings = bookings;
        this.revenue = revenue;
    }

    public String getJourneyDate() { return journeyDate; }
    public String getRoute() { return route; }
    public int getBookings() { return bookings; }
    public double getRevenue() { return revenue; }
}
