package com.railway.model;

public class RevenueReport {
    private double totalRevenue;
    private int successfulTransactions;
    private int confirmedBookings;
    private int cancelledBookings;
    private double averageBookingAmount;

    public RevenueReport(double totalRevenue, int successfulTransactions,
                         int confirmedBookings, int cancelledBookings,
                         double averageBookingAmount) {
        this.totalRevenue = totalRevenue;
        this.successfulTransactions = successfulTransactions;
        this.confirmedBookings = confirmedBookings;
        this.cancelledBookings = cancelledBookings;
        this.averageBookingAmount = averageBookingAmount;
    }

    public double getTotalRevenue() { return totalRevenue; }
    public int getSuccessfulTransactions() { return successfulTransactions; }
    public int getConfirmedBookings() { return confirmedBookings; }
    public int getCancelledBookings() { return cancelledBookings; }
    public double getAverageBookingAmount() { return averageBookingAmount; }
}
