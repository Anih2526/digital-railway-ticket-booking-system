package com.railway.dao;

import com.railway.db.Database;
import com.railway.model.AdminSummary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminDao {

    public AdminSummary getSummary() throws Exception {

        int totalBookings = 0;
        int confirmedBookings = 0;
        int totalUsers = 0;
        int totalTrains = 0;
        int totalStations = 0;
        int totalRoutes = 0;
        double totalRevenue = 0.0;
        int availableSeats = 0;

        try (Connection connection = Database.getConnection()) {

            totalBookings = getCount(
                    connection,
                    "SELECT COUNT(*) FROM bookings"
            );

            confirmedBookings = getCount(
                    connection,
                    "SELECT COUNT(*) FROM bookings WHERE status='CONFIRMED'"
            );

            totalUsers = getCount(
                    connection,
                    "SELECT COUNT(*) FROM users WHERE role='USER'"
            );

            totalTrains = getCount(
                    connection,
                    "SELECT COUNT(*) FROM trains"
            );

            totalStations = getCount(
                    connection,
                    "SELECT COUNT(*) FROM stations"
            );

            totalRoutes = getCount(
                    connection,
                    "SELECT COUNT(*) FROM routes"
            );

           totalRevenue = getDouble(
                        connection,
                        """
                        SELECT COALESCE(SUM(t.amount), 0)
                        FROM transactions t
                        JOIN bookings b
                        ON t.booking_id = b.booking_id
                        WHERE t.status = 'SUCCESS'
                        AND b.status = 'CONFIRMED'
                        """
                );

            availableSeats = getCount(
                    connection,
                    "SELECT COUNT(*) FROM schedule_seats WHERE status='AVAILABLE'"
            );
        }

        return new AdminSummary(
                totalBookings,
                confirmedBookings,
                totalUsers,
                totalTrains,
                totalStations,
                totalRoutes,
                totalRevenue,
                availableSeats
        );
    }

    private int getCount(Connection connection,
                         String sql) throws Exception {

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            result.next();
            return result.getInt(1);
        }
    }

    private double getDouble(Connection connection,
                             String sql) throws Exception {

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            result.next();
            return result.getDouble(1);
        }
    }
}
