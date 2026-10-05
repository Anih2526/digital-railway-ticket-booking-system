package com.railway.model;

public class AdminSummary {

    private int totalBookings;
    private int confirmedBookings;
    private int totalUsers;
    private int totalTrains;
    private int totalStations;
    private int totalRoutes;
    private double totalRevenue;
    private int availableSeats;

    public AdminSummary(
            int totalBookings,
            int confirmedBookings,
            int totalUsers,
            int totalTrains,
            int totalStations,
            int totalRoutes,
            double totalRevenue,
            int availableSeats) {
        this.totalBookings = totalBookings;
        this.confirmedBookings = confirmedBookings;
        this.totalUsers = totalUsers;
        this.totalTrains = totalTrains;
        this.totalStations = totalStations;
        this.totalRoutes = totalRoutes;
        this.totalRevenue = totalRevenue;
        this.availableSeats = availableSeats;
    }

    public int getTotalBookings() { return totalBookings; }
    public int getConfirmedBookings() { return confirmedBookings; }
    public int getTotalUsers() { return totalUsers; }
    public int getTotalTrains() { return totalTrains; }
    public int getTotalStations() { return totalStations; }
    public int getTotalRoutes() { return totalRoutes; }
    public double getTotalRevenue() { return totalRevenue; }
    public int getAvailableSeats() { return availableSeats; }
}
