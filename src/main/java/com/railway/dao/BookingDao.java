package com.railway.dao;

import com.railway.model.BookingDetails;
import com.railway.model.BookingSummary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingDao {

    /*
     * Checks and LOCKS one selected schedule seat.
     * FOR UPDATE keeps the row locked until the transaction ends.
     */
    public boolean areSeatsAvailable(
            Connection connection,
            int scheduleId,
            int scheduleSeatId) throws SQLException {

        String sql = """
                SELECT status
                FROM schedule_seats
                WHERE schedule_seat_id = ?
                  AND schedule_id = ?
                FOR UPDATE
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, scheduleSeatId);
            statement.setInt(2, scheduleId);

            try (ResultSet result = statement.executeQuery()) {

                if (!result.next()) {
                    return false;
                }

                return "AVAILABLE".equals(
                        result.getString("status")
                );
            }
        }
    }

    public int createBooking(
            Connection connection,
            int userId,
            int scheduleId,
            String bookingReference,
            double totalAmount) throws SQLException {

        String sql = """
                INSERT INTO bookings
                (
                    booking_reference,
                    user_id,
                    schedule_id,
                    total_amount,
                    status
                )
                VALUES
                (?, ?, ?, ?, 'CONFIRMED')
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             java.sql.Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, bookingReference);
            statement.setInt(2, userId);
            statement.setInt(3, scheduleId);
            statement.setDouble(4, totalAmount);

            statement.executeUpdate();

            try (ResultSet result = statement.getGeneratedKeys()) {
                if (result.next()) {
                    return result.getInt(1);
                }
            }
        }

        throw new SQLException("Booking ID was not generated.");
    }

    public void addBookingSeat(
            Connection connection,
            int bookingId,
            int scheduleSeatId,
            double price) throws SQLException {

        String sql = """
                INSERT INTO booking_seats
                (
                    booking_id,
                    schedule_seat_id,
                    price
                )
                VALUES
                (?, ?, ?)
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, bookingId);
            statement.setInt(2, scheduleSeatId);
            statement.setDouble(3, price);
            statement.executeUpdate();
        }
    }

    public void markSeatBooked(
            Connection connection,
            int scheduleSeatId) throws SQLException {

        String sql = """
                UPDATE schedule_seats
                SET status = 'BOOKED'
                WHERE schedule_seat_id = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, scheduleSeatId);
            statement.executeUpdate();
        }
    }

    public double getFareForSchedule(
            Connection connection,
            int scheduleId) throws SQLException {

        String sql = "SELECT fare FROM schedules WHERE schedule_id = ?";

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, scheduleId);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getDouble("fare");
                }
            }
        }

        throw new SQLException("Schedule was not found.");
    }

    public void createTransaction(
            Connection connection,
            int bookingId,
            double amount) throws SQLException {

        String sql = """
                INSERT INTO transactions
                (
                    booking_id,
                    amount,
                    status
                )
                VALUES
                (?, ?, 'SUCCESS')
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, bookingId);
            statement.setDouble(2, amount);
            statement.executeUpdate();
        }
    }

    public BookingDetails getBookingDetails(
            Connection connection,
            int userId,
            String bookingReference) throws SQLException {

        String sql = """
                SELECT
                    b.booking_id,
                    b.booking_reference,
                    b.total_amount,
                    b.status AS booking_status,
                    b.booked_at,
                    tx.status AS payment_status,
                    t.train_number,
                    t.train_name,
                    src.station_name AS source_station,
                    dest.station_name AS destination_station,
                    sc.departure_time,
                    sc.arrival_time
                FROM bookings b
                JOIN schedules sc
                    ON b.schedule_id = sc.schedule_id
                JOIN trains t
                    ON sc.train_id = t.train_id
                JOIN routes r
                    ON sc.route_id = r.route_id
                JOIN stations src
                    ON r.source_station_id = src.station_id
                JOIN stations dest
                    ON r.destination_station_id = dest.station_id
                LEFT JOIN transactions tx
                    ON b.booking_id = tx.booking_id
                WHERE b.user_id = ?
                  AND b.booking_reference = ?
                ORDER BY tx.transaction_id DESC
                LIMIT 1
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            statement.setString(2, bookingReference);

            try (ResultSet result = statement.executeQuery()) {

                if (!result.next()) {
                    return null;
                }

                return new BookingDetails(
                        result.getInt("booking_id"),
                        result.getString("booking_reference"),
                        result.getString("train_number"),
                        result.getString("train_name"),
                        result.getString("source_station"),
                        result.getString("destination_station"),
                        result.getObject("departure_time", LocalDateTime.class),
                        result.getObject("arrival_time", LocalDateTime.class),
                        result.getDouble("total_amount"),
                        result.getString("booking_status"),
                        result.getString("payment_status"),
                        result.getObject("booked_at", LocalDateTime.class),
                        new ArrayList<>()
                );
            }
        }
    }

    public List<String> getBookedSeatNames(
            Connection connection,
            int bookingId) throws SQLException {

        List<String> seatNames = new ArrayList<>();

        String sql = """
                SELECT CONCAT(c.coach_name, '-', s.seat_number) AS seat_name
                FROM booking_seats bs
                JOIN schedule_seats ss
                    ON bs.schedule_seat_id = ss.schedule_seat_id
                JOIN seats s
                    ON ss.seat_id = s.seat_id
                JOIN coaches c
                    ON s.coach_id = c.coach_id
                WHERE bs.booking_id = ?
                ORDER BY c.coach_name, s.seat_number
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, bookingId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    seatNames.add(result.getString("seat_name"));
                }
            }
        }

        return seatNames;
    }

    public List<BookingSummary> getUserBookings(
            int userId) throws Exception {

        List<BookingSummary> bookings = new ArrayList<>();

        String sql = """
                SELECT
                    b.booking_id,
                    b.booking_reference,
                    t.train_number,
                    t.train_name,
                    src.station_name AS source_station,
                    dest.station_name AS destination_station,
                    sc.departure_time,
                    b.total_amount,
                    b.status AS booking_status,
                    tx.status AS payment_status,
                    b.booked_at
                FROM bookings b
                JOIN schedules sc
                    ON b.schedule_id = sc.schedule_id
                JOIN trains t
                    ON sc.train_id = t.train_id
                JOIN routes r
                    ON sc.route_id = r.route_id
                JOIN stations src
                    ON r.source_station_id = src.station_id
                JOIN stations dest
                    ON r.destination_station_id = dest.station_id
                LEFT JOIN transactions tx
                    ON b.booking_id = tx.booking_id
                WHERE b.user_id = ?
                     AND b.status = 'CONFIRMED'
                ORDER BY b.booked_at DESC
                """;

        try (Connection connection =
                     com.railway.db.Database.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    bookings.add(new BookingSummary(
                            result.getInt("booking_id"),
                            result.getString("booking_reference"),
                            result.getString("train_number"),
                            result.getString("train_name"),
                            result.getString("source_station"),
                            result.getString("destination_station"),
                            result.getObject("departure_time", LocalDateTime.class),
                            result.getDouble("total_amount"),
                            result.getString("booking_status"),
                            result.getString("payment_status"),
                            result.getObject("booked_at", LocalDateTime.class)
                    ));
                }
            }
        }

        return bookings;
    }

    public List<BookingSummary> getAllBookings() throws Exception {

        List<BookingSummary> bookings = new ArrayList<>();

        String sql = """
                SELECT
                    b.booking_id,
                    b.booking_reference,
                    t.train_number,
                    t.train_name,
                    src.station_name AS source_station,
                    dest.station_name AS destination_station,
                    sc.departure_time,
                    b.total_amount,
                    b.status AS booking_status,
                    tx.status AS payment_status,
                    b.booked_at
                FROM bookings b
                JOIN schedules sc
                    ON b.schedule_id = sc.schedule_id
                JOIN trains t
                    ON sc.train_id = t.train_id
                JOIN routes r
                    ON sc.route_id = r.route_id
                JOIN stations src
                    ON r.source_station_id = src.station_id
                JOIN stations dest
                    ON r.destination_station_id = dest.station_id
                LEFT JOIN transactions tx
                    ON b.booking_id = tx.booking_id
                ORDER BY b.booked_at DESC
                LIMIT 200
                """;

        try (Connection connection =
                     com.railway.db.Database.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                bookings.add(new BookingSummary(
                        result.getInt("booking_id"),
                        result.getString("booking_reference"),
                        result.getString("train_number"),
                        result.getString("train_name"),
                        result.getString("source_station"),
                        result.getString("destination_station"),
                        result.getObject("departure_time", LocalDateTime.class),
                        result.getDouble("total_amount"),
                        result.getString("booking_status"),
                        result.getString("payment_status"),
                        result.getObject("booked_at", LocalDateTime.class)
                ));
            }
        }

        return bookings;
    }

    public int getScheduleIdForBooking(
            int userId,
            String bookingReference)
            throws Exception {

        String sql = """
                SELECT schedule_id
                FROM bookings
                WHERE user_id = ?
                AND booking_reference = ?
                """;

        try (Connection connection =
                    com.railway.db.Database.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    userId
            );

            statement.setString(
                    2,
                    bookingReference
            );

            try (ResultSet result =
                        statement.executeQuery()) {

                if (!result.next()) {

                    throw new Exception(
                            "Booking not found."
                    );
                }

                return result.getInt(
                        "schedule_id"
                );
            }
        }
    }


    /*
    * Cancels the booking and releases
    * all of its seats.
    *
    * This method must be called inside
    * a database transaction.
    */
    public void cancelBookingAndReleaseSeats(
            Connection connection,
            int userId,
            String bookingReference)
            throws Exception {

        int bookingId;

        String findSql = """
                SELECT
                    booking_id
                FROM bookings
                WHERE user_id = ?
                AND booking_reference = ?
                AND status = 'CONFIRMED'
                FOR UPDATE
                """;

        try (PreparedStatement statement =
                    connection.prepareStatement(
                            findSql)) {

            statement.setInt(
                    1,
                    userId
            );

            statement.setString(
                    2,
                    bookingReference
            );

            try (ResultSet result =
                        statement.executeQuery()) {

                if (!result.next()) {

                    throw new Exception(
                            "Booking is already cancelled "
                            + "or was not found."
                    );
                }

                bookingId =
                        result.getInt(
                                "booking_id"
                        );
            }
        }


        /*
        * Mark booking as cancelled.
        */
        String bookingSql = """
                UPDATE bookings
                SET status = 'CANCELLED'
                WHERE booking_id = ?
                """;

        try (PreparedStatement statement =
                    connection.prepareStatement(
                            bookingSql)) {

            statement.setInt(
                    1,
                    bookingId
            );

            statement.executeUpdate();
        }


        /*
        * Release the seats so they can
        * be booked again.
        */
        String seatSql = """
                UPDATE schedule_seats ss
                JOIN booking_seats bs
                    ON ss.schedule_seat_id =
                    bs.schedule_seat_id

                SET ss.status = 'AVAILABLE'

                WHERE bs.booking_id = ?
                """;

        try (PreparedStatement statement =
                    connection.prepareStatement(
                            seatSql)) {

            statement.setInt(
                    1,
                    bookingId
            );

            statement.executeUpdate();
        }
    }
}
