
package com.railway.service;

import com.railway.db.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ExpiredBookingService {

    public void releaseExpiredSeats() throws Exception {

        try (Connection connection = Database.getConnection()) {

            connection.setAutoCommit(false);

            try {
                // Mark journeys as completed after their arrival time.
                String completeSchedules =
                        "UPDATE schedules " +
                        "SET status = 'COMPLETED' " +
                        "WHERE status = 'SCHEDULED' " +
                        "AND arrival_time <= NOW()";

                try (PreparedStatement ps =
                             connection.prepareStatement(completeSchedules)) {
                    ps.executeUpdate();
                }

                // Release booked seats belonging to completed journeys.
                String releaseSeats =
                        "UPDATE schedule_seats ss " +
                        "JOIN schedules s " +
                        "ON ss.schedule_id = s.schedule_id " +
                        "SET ss.status = 'AVAILABLE' " +
                        "WHERE s.status = 'COMPLETED' " +
                        "AND s.arrival_time <= NOW() " +
                        "AND ss.status = 'BOOKED'";

                try (PreparedStatement ps =
                             connection.prepareStatement(releaseSeats)) {
                    ps.executeUpdate();
                }

                connection.commit();

            } catch (Exception e) {
                connection.rollback();
                throw e;
            }
        }
    }
}
