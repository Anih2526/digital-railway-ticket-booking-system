
package com.railway.dao;

import com.railway.db.Database;
import com.railway.model.Schedule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class ScheduleDao {

    /*
     * Automatically mark expired scheduled journeys as completed.
     */
    public void updateExpiredSchedules() throws Exception {

        String sql = """
                UPDATE schedules
                SET status = 'COMPLETED'
                WHERE status = 'SCHEDULED'
                  AND arrival_time <= NOW()
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.executeUpdate();
        }
    }

    public List<Schedule> getAllSchedules() throws Exception {

        updateExpiredSchedules();

        List<Schedule> schedules = new ArrayList<>();

        String sql = """
                SELECT
                    s.schedule_id,
                    s.train_id,
                    s.route_id,
                    t.train_number,
                    t.train_name,
                    r.route_name,
                    s.departure_time,
                    s.arrival_time,
                    s.fare,
                    s.status
                FROM schedules s
                JOIN trains t ON s.train_id = t.train_id
                JOIN routes r ON s.route_id = r.route_id
                ORDER BY s.departure_time DESC
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                schedules.add(readSchedule(result));
            }
        }

        return schedules;
    }

    public Schedule getSchedule(int scheduleId) throws Exception {

        updateExpiredSchedules();

        String sql = """
                SELECT
                    s.schedule_id,
                    s.train_id,
                    s.route_id,
                    t.train_number,
                    t.train_name,
                    r.route_name,
                    s.departure_time,
                    s.arrival_time,
                    s.fare,
                    s.status
                FROM schedules s
                JOIN trains t ON s.train_id = t.train_id
                JOIN routes r ON s.route_id = r.route_id
                WHERE s.schedule_id = ?
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, scheduleId);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return readSchedule(result);
                }
            }
        }

        return null;
    }

    public double getFare(int scheduleId) throws Exception {

        String sql =
                "SELECT fare FROM schedules WHERE schedule_id = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, scheduleId);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getDouble("fare");
                }
            }
        }

        throw new Exception("Schedule was not found.");
    }

    public void addSchedule(
            int trainId,
            int routeId,
            LocalDateTime departureTime,
            LocalDateTime arrivalTime,
            double fare) throws Exception {

        validateTimesAndFare(departureTime, arrivalTime, fare);

        if (!departureTime.isAfter(LocalDateTime.now())) {
            throw new Exception(
                    "Departure time must be in the future."
            );
        }

        try (Connection connection = Database.getConnection()) {

            connection.setAutoCommit(false);

            try {

                String sql = """
                        INSERT INTO schedules
                            (train_id, route_id, departure_time,
                             arrival_time, fare, status)
                        VALUES (?, ?, ?, ?, ?, 'SCHEDULED')
                        """;

                int scheduleId;

                try (PreparedStatement statement =
                             connection.prepareStatement(
                                     sql,
                                     java.sql.Statement.RETURN_GENERATED_KEYS)) {

                    statement.setInt(1, trainId);
                    statement.setInt(2, routeId);
                    statement.setObject(3, departureTime);
                    statement.setObject(4, arrivalTime);
                    statement.setDouble(5, fare);

                    statement.executeUpdate();

                    try (ResultSet result = statement.getGeneratedKeys()) {
                        if (!result.next()) {
                            throw new Exception(
                                    "Schedule ID was not generated."
                            );
                        }

                        scheduleId = result.getInt(1);
                    }
                }

                String seatSql = """
                        INSERT INTO schedule_seats
                            (schedule_id, seat_id, status)
                        SELECT ?, seat_id, 'AVAILABLE'
                        FROM seats
                        WHERE coach_id IN (
                            SELECT coach_id
                            FROM coaches
                            WHERE train_id = ?
                        )
                        """;

                try (PreparedStatement statement =
                             connection.prepareStatement(seatSql)) {

                    statement.setInt(1, scheduleId);
                    statement.setInt(2, trainId);
                    statement.executeUpdate();
                }

                connection.commit();

            } catch (Exception exception) {
                connection.rollback();
                throw exception;
            }
        }
    }

    public void updateSchedule(
            int scheduleId,
            int trainId,
            int routeId,
            LocalDateTime departureTime,
            LocalDateTime arrivalTime,
            double fare,
            String status) throws Exception {

        validateTimesAndFare(departureTime, arrivalTime, fare);

        /*
         * Admins may not change status manually through the edit form.
         * Only SCHEDULED journeys can be edited.
         */
        if (!"SCHEDULED".equals(status)) {
            throw new Exception(
                    "Only scheduled journeys can be edited."
            );
        }

        try (Connection connection = Database.getConnection()) {

            connection.setAutoCommit(false);

            try {

                int oldTrainId;
                int bookedSeats;

                String checkSql = """
                        SELECT
                            train_id,
                            status,
                            (SELECT COUNT(*)
                             FROM schedule_seats ss
                             WHERE ss.schedule_id = s.schedule_id
                               AND ss.status = 'BOOKED') AS booked_count
                        FROM schedules s
                        WHERE schedule_id = ?
                        FOR UPDATE
                        """;

                try (PreparedStatement check =
                             connection.prepareStatement(checkSql)) {

                    check.setInt(1, scheduleId);

                    try (ResultSet result = check.executeQuery()) {

                        if (!result.next()) {
                            throw new Exception(
                                    "Schedule was not found."
                            );
                        }

                        String currentStatus =
                                result.getString("status");

                        if (!"SCHEDULED".equals(currentStatus)) {
                            throw new Exception(
                                    "Completed or cancelled journeys cannot be edited."
                            );
                        }

                        oldTrainId = result.getInt("train_id");
                        bookedSeats = result.getInt("booked_count");
                    }
                }

                if (oldTrainId != trainId && bookedSeats > 0) {
                    throw new Exception(
                            "The train cannot be changed because this schedule already has booked seats."
                    );
                }

                String sql = """
                        UPDATE schedules
                        SET train_id = ?,
                            route_id = ?,
                            departure_time = ?,
                            arrival_time = ?,
                            fare = ?
                        WHERE schedule_id = ?
                          AND status = 'SCHEDULED'
                        """;

                try (PreparedStatement statement =
                             connection.prepareStatement(sql)) {

                    statement.setInt(1, trainId);
                    statement.setInt(2, routeId);
                    statement.setObject(3, departureTime);
                    statement.setObject(4, arrivalTime);
                    statement.setDouble(5, fare);
                    statement.setInt(6, scheduleId);

                    if (statement.executeUpdate() != 1) {
                        throw new Exception(
                                "The schedule is no longer editable."
                        );
                    }
                }

                if (oldTrainId != trainId) {

                    try (PreparedStatement delete =
                                 connection.prepareStatement(
                                         "DELETE FROM schedule_seats WHERE schedule_id = ?")) {

                        delete.setInt(1, scheduleId);
                        delete.executeUpdate();
                    }

                    String seatSql = """
                            INSERT INTO schedule_seats
                                (schedule_id, seat_id, status)
                            SELECT ?, s.seat_id, 'AVAILABLE'
                            FROM seats s
                            JOIN coaches c ON s.coach_id = c.coach_id
                            WHERE c.train_id = ?
                            """;

                    try (PreparedStatement seatStatement =
                                 connection.prepareStatement(seatSql)) {

                        seatStatement.setInt(1, scheduleId);
                        seatStatement.setInt(2, trainId);
                        seatStatement.executeUpdate();
                    }
                }

                connection.commit();

            } catch (Exception exception) {
                connection.rollback();
                throw exception;
            }
        }
    }

    public void updateStatus(
            int scheduleId,
            String status) throws Exception {

        if (!isValidStatus(status)) {
            throw new Exception("Invalid schedule status.");
        }

        /*
         * Prevent manual status changes on completed journeys.
         * A scheduled journey may be cancelled manually.
         */
        String sql;

        if ("CANCELLED".equals(status)) {
            sql = """
                    UPDATE schedules
                    SET status = 'CANCELLED'
                    WHERE schedule_id = ?
                      AND status = 'SCHEDULED'
                    """;
        } else if ("COMPLETED".equals(status)) {
            sql = """
                    UPDATE schedules
                    SET status = 'COMPLETED'
                    WHERE schedule_id = ?
                      AND status = 'SCHEDULED'
                      AND arrival_time <= NOW()
                    """;
        } else {
            throw new Exception(
                    "Schedules cannot be changed back to SCHEDULED."
            );
        }

        try (Connection connection = Database.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, scheduleId);

            if (statement.executeUpdate() != 1) {
                throw new Exception(
                        "The schedule status cannot be changed."
                );
            }
        }
    }

    private Schedule readSchedule(ResultSet result) throws Exception {

        return new Schedule(
                result.getInt("schedule_id"),
                result.getInt("train_id"),
                result.getInt("route_id"),
                result.getString("train_number"),
                result.getString("train_name"),
                result.getString("route_name"),
                result.getObject(
                        "departure_time",
                        LocalDateTime.class
                ),
                result.getObject(
                        "arrival_time",
                        LocalDateTime.class
                ),
                result.getDouble("fare"),
                result.getString("status")
        );
    }

    private void validateTimesAndFare(
            LocalDateTime departureTime,
            LocalDateTime arrivalTime,
            double fare) throws Exception {

        if (departureTime == null || arrivalTime == null) {
            throw new Exception(
                    "Departure and arrival times are required."
            );
        }

        if (!arrivalTime.isAfter(departureTime)) {
            throw new Exception(
                    "Arrival time must be after departure time."
            );
        }

        if (fare < 0) {
            throw new Exception("Fare cannot be negative.");
        }
    }

    private boolean isValidStatus(String status) {
        return "SCHEDULED".equals(status)
                || "COMPLETED".equals(status)
                || "CANCELLED".equals(status);
    }
}
