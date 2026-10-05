package com.railway.dao;

import com.railway.db.Database;
import com.railway.model.RevenueReport;
import com.railway.model.RevenueRow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ReportDao {

    public RevenueReport getRevenueReport() throws Exception {
        String sql = """
                SELECT
                    COALESCE(SUM(CASE WHEN t.status = 'SUCCESS' AND b.status = 'CONFIRMED' THEN t.amount ELSE 0 END), 0) AS revenue,
                    COALESCE(SUM(CASE WHEN t.status = 'SUCCESS' AND b.status = 'CONFIRMED' THEN 1 ELSE 0 END), 0) AS successful_transactions,
                    COALESCE(SUM(CASE WHEN b.status = 'CONFIRMED' THEN 1 ELSE 0 END), 0) AS confirmed_bookings,
                    COALESCE(SUM(CASE WHEN b.status = 'CANCELLED' THEN 1 ELSE 0 END), 0) AS cancelled_bookings,
                    COALESCE(AVG(CASE WHEN b.status = 'CONFIRMED' THEN b.total_amount END), 0) AS average_booking_amount
                FROM bookings b
                LEFT JOIN transactions t ON b.booking_id = t.booking_id
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            result.next();

            return new RevenueReport(
                    result.getDouble("revenue"),
                    result.getInt("successful_transactions"),
                    result.getInt("confirmed_bookings"),
                    result.getInt("cancelled_bookings"),
                    result.getDouble("average_booking_amount")
            );
        }
    }

    public List<RevenueRow> getRevenueRows() throws Exception {
        List<RevenueRow> rows = new ArrayList<>();

        String sql = """
                SELECT
                    DATE(s.departure_time) AS journey_date,
                    CONCAT(src.station_name, ' → ', dest.station_name) AS route,
                    COUNT(DISTINCT b.booking_id) AS bookings,
                    COALESCE(SUM(CASE WHEN t.status = 'SUCCESS' AND b.status = 'CONFIRMED' THEN t.amount ELSE 0 END), 0) AS revenue
                FROM bookings b
                JOIN schedules s ON b.schedule_id = s.schedule_id
                JOIN routes r ON s.route_id = r.route_id
                JOIN stations src ON r.source_station_id = src.station_id
                JOIN stations dest ON r.destination_station_id = dest.station_id
                LEFT JOIN transactions t ON b.booking_id = t.booking_id
                GROUP BY DATE(s.departure_time), route
                ORDER BY journey_date DESC, route
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                rows.add(new RevenueRow(
                        result.getDate("journey_date").toString(),
                        result.getString("route"),
                        result.getInt("bookings"),
                        result.getDouble("revenue")
                ));
            }
        }

        return rows;
    }
}
