package com.railway.dao;

import com.railway.db.Database;
import com.railway.model.Coach;
import com.railway.model.CoachSeat;
import com.railway.model.Seat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SeatDao {

    public List<Seat> getSeatsForSchedule(int scheduleId)
            throws Exception {

        List<Seat> seats = new ArrayList<>();

        String sql = """
                SELECT
                    ss.schedule_seat_id,
                    c.coach_name,
                    s.seat_number,
                    ss.status
                FROM schedule_seats ss
                JOIN seats s
                    ON ss.seat_id = s.seat_id
                JOIN coaches c
                    ON s.coach_id = c.coach_id
                WHERE ss.schedule_id = ?
                ORDER BY c.coach_name, s.seat_number
                """;

        try (Connection connection =
                     Database.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, scheduleId);

            try (ResultSet result =
                         statement.executeQuery()) {

                while (result.next()) {

                    seats.add(
                            new Seat(
                                    result.getInt(
                                            "schedule_seat_id"
                                    ),
                                    result.getString(
                                            "coach_name"
                                    ),
                                    result.getInt(
                                            "seat_number"
                                    ),
                                    result.getString(
                                            "status"
                                    )
                            )
                    );
                }
            }
        }

        return seats;
    }


    public List<Coach> getAllCoaches()
            throws Exception {

        List<Coach> coaches =
                new ArrayList<>();

        String sql = """
                SELECT
                    c.coach_id,
                    c.train_id,
                    tr.train_number,
                    c.coach_name,
                    c.seat_capacity,
                    COUNT(s.seat_id) AS seat_count

                FROM coaches c

                JOIN trains tr
                    ON c.train_id = tr.train_id

                LEFT JOIN seats s
                    ON c.coach_id = s.coach_id

                GROUP BY
                    c.coach_id,
                    c.train_id,
                    tr.train_number,
                    c.coach_name,
                    c.seat_capacity

                ORDER BY
                    tr.train_number,
                    c.coach_name
                """;

        try (Connection connection =
                     Database.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result =
                     statement.executeQuery()) {

            while (result.next()) {

                coaches.add(
                        new Coach(
                                result.getInt(
                                        "coach_id"
                                ),
                                result.getInt(
                                        "train_id"
                                ),
                                result.getString(
                                        "train_number"
                                ),
                                result.getString(
                                        "coach_name"
                                ),
                                result.getInt(
                                        "seat_capacity"
                                ),
                                result.getInt(
                                        "seat_count"
                                )
                        )
                );
            }
        }

        return coaches;
    }


    /*
     * Returns all individual seats so the admin
     * can change seat numbers.
     */
    public List<CoachSeat> getAllCoachSeats()
            throws Exception {

        List<CoachSeat> seats =
                new ArrayList<>();

        String sql = """
                SELECT
                    s.seat_id,
                    c.coach_id,
                    tr.train_number,
                    c.coach_name,
                    s.seat_number,
                    COUNT(
                        CASE
                            WHEN ss.status = 'BOOKED'
                            THEN 1
                        END
                    ) AS booked_count

                FROM seats s

                JOIN coaches c
                    ON s.coach_id = c.coach_id

                JOIN trains tr
                    ON c.train_id = tr.train_id

                LEFT JOIN schedule_seats ss
                    ON s.seat_id = ss.seat_id

                GROUP BY
                    s.seat_id,
                    c.coach_id,
                    tr.train_number,
                    c.coach_name,
                    s.seat_number

                ORDER BY
                    tr.train_number,
                    c.coach_name,
                    s.seat_number
                """;

        try (Connection connection =
                     Database.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result =
                     statement.executeQuery()) {

            while (result.next()) {

                seats.add(
                        new CoachSeat(
                                result.getInt("seat_id"),
                                result.getInt("coach_id"),
                                result.getString(
                                        "train_number"
                                ),
                                result.getString(
                                        "coach_name"
                                ),
                                result.getInt(
                                        "seat_number"
                                ),
                                result.getInt(
                                        "booked_count"
                                )
                        )
                );
            }
        }

        return seats;
    }


    /*
     * Adds a coach and automatically creates
     * seats 1, 2, 3 ... capacity.
     */
    public void addCoachAndSeats(
            int trainId,
            String coachName,
            int seatCapacity)
            throws Exception {

        if (coachName == null
                || coachName.isBlank()) {

            throw new Exception(
                    "Coach name is required."
            );
        }

        if (seatCapacity < 1) {

            throw new Exception(
                    "Seat capacity must be at least 1."
            );
        }

        try (Connection connection =
                     Database.getConnection()) {

            connection.setAutoCommit(false);

            try {

                String coachSql = """
                        INSERT INTO coaches
                            (
                                train_id,
                                coach_name,
                                seat_capacity
                            )
                        VALUES
                            (?, ?, ?)
                        """;

                int coachId;

                try (PreparedStatement statement =
                             connection.prepareStatement(
                                     coachSql,
                                     java.sql.Statement
                                             .RETURN_GENERATED_KEYS)) {

                    statement.setInt(
                            1,
                            trainId
                    );

                    statement.setString(
                            2,
                            coachName.trim()
                    );

                    statement.setInt(
                            3,
                            seatCapacity
                    );

                    statement.executeUpdate();

                    try (ResultSet result =
                                 statement.getGeneratedKeys()) {

                        if (!result.next()) {

                            throw new Exception(
                                    "Coach ID was not generated."
                            );
                        }

                        coachId =
                                result.getInt(1);
                    }
                }


                for (int seatNumber = 1;
                     seatNumber <= seatCapacity;
                     seatNumber++) {

                    addSeat(
                            connection,
                            coachId,
                            seatNumber
                    );
                }


                addSeatsToSchedules(
                        connection,
                        trainId,
                        coachId
                );


                updateTrainCapacity(
                        connection,
                        trainId
                );


                connection.commit();

            } catch (Exception exception) {

                connection.rollback();

                throw exception;
            }
        }
    }


    /*
     * Updates coach name and seat capacity.
     */
    public void updateCoachAndSeats(
            int coachId,
            String coachName,
            int newCapacity)
            throws Exception {

        if (coachName == null
                || coachName.isBlank()) {

            throw new Exception(
                    "Coach name is required."
            );
        }

        if (newCapacity < 1) {

            throw new Exception(
                    "Seat capacity must be at least 1."
            );
        }

        try (Connection connection =
                     Database.getConnection()) {

            connection.setAutoCommit(false);

            try {

                int trainId =
                        getTrainId(
                                connection,
                                coachId
                        );

                int oldCapacity =
                        getCoachCapacity(
                                connection,
                                coachId
                        );

                int currentSeatCount =
                        getSeatCount(
                                connection,
                                coachId
                        );


                /*
                 * If the new capacity is smaller,
                 * make sure the seats being removed
                 * are not already booked.
                 */
                if (newCapacity
                        < currentSeatCount) {

                    int bookedSeats =
                            getBookedSeatsAboveNumber(
                                    connection,
                                    coachId,
                                    newCapacity
                            );

                    if (bookedSeats > 0) {

                        throw new Exception(
                                "Cannot reduce capacity because "
                                + "one or more seats being removed "
                                + "are already booked."
                        );
                    }


                    String deleteScheduleSeatsSql = """
                            DELETE ss
                            FROM schedule_seats ss
                            JOIN seats s
                                ON ss.seat_id = s.seat_id
                            WHERE s.coach_id = ?
                              AND s.seat_number > ?
                            """;

                    try (PreparedStatement statement =
                                 connection.prepareStatement(
                                         deleteScheduleSeatsSql)) {

                        statement.setInt(
                                1,
                                coachId
                        );

                        statement.setInt(
                                2,
                                newCapacity
                        );

                        statement.executeUpdate();
                    }


                    String deleteSeatsSql = """
                            DELETE FROM seats
                            WHERE coach_id = ?
                              AND seat_number > ?
                            """;

                    try (PreparedStatement statement =
                                 connection.prepareStatement(
                                         deleteSeatsSql)) {

                        statement.setInt(
                                1,
                                coachId
                        );

                        statement.setInt(
                                2,
                                newCapacity
                        );

                        statement.executeUpdate();
                    }
                }


                /*
                 * If capacity increased,
                 * create the new seats.
                 */
                if (newCapacity
                        > currentSeatCount) {

                    for (int seatNumber =
                                 currentSeatCount + 1;
                         seatNumber <= newCapacity;
                         seatNumber++) {

                        addSeat(
                                connection,
                                coachId,
                                seatNumber
                        );
                    }


                    addSeatsToSchedules(
                            connection,
                            trainId,
                            coachId
                    );
                }


                String updateCoachSql = """
                        UPDATE coaches
                        SET coach_name = ?,
                            seat_capacity = ?
                        WHERE coach_id = ?
                        """;

                try (PreparedStatement statement =
                             connection.prepareStatement(
                                     updateCoachSql)) {

                    statement.setString(
                            1,
                            coachName.trim()
                    );

                    statement.setInt(
                            2,
                            newCapacity
                    );

                    statement.setInt(
                            3,
                            coachId
                    );

                    statement.executeUpdate();
                }


                updateTrainCapacity(
                        connection,
                        trainId
                );


                connection.commit();

            } catch (Exception exception) {

                connection.rollback();

                throw exception;
            }
        }
    }


    /*
     * Changes one individual seat number.
     */
    public void updateSeatNumber(
            int seatId,
            int newSeatNumber)
            throws Exception {

        if (newSeatNumber < 1) {

            throw new Exception(
                    "Seat number must be at least 1."
            );
        }

        try (Connection connection =
                     Database.getConnection()) {

            connection.setAutoCommit(false);

            try {

                int coachId =
                        getCoachId(
                                connection,
                                seatId
                        );


                int capacity =
                        getCoachCapacity(
                                connection,
                                coachId
                        );

                int bookedCount =
                        getSeatBookedCount(
                                connection,
                                seatId
                        );


                if (bookedCount > 0) {

                    throw new Exception(
                            "This seat is already booked and "
                            + "cannot be renumbered."
                    );
                }


                String checkSql = """
                        SELECT COUNT(*)
                        FROM seats
                        WHERE coach_id = ?
                          AND seat_number = ?
                          AND seat_id <> ?
                        """;

                try (PreparedStatement statement =
                             connection.prepareStatement(
                                     checkSql)) {

                    statement.setInt(
                            1,
                            coachId
                    );

                    statement.setInt(
                            2,
                            newSeatNumber
                    );

                    statement.setInt(
                            3,
                            seatId
                    );

                    try (ResultSet result =
                                 statement.executeQuery()) {

                        result.next();

                        if (result.getInt(1) > 0) {

                            throw new Exception(
                                    "That seat number already exists "
                                    + "in this coach."
                            );
                        }
                    }
                }


                String updateSql = """
                        UPDATE seats
                        SET seat_number = ?
                        WHERE seat_id = ?
                        """;

                try (PreparedStatement statement =
                             connection.prepareStatement(
                                     updateSql)) {

                    statement.setInt(
                            1,
                            newSeatNumber
                    );

                    statement.setInt(
                            2,
                            seatId
                    );

                    statement.executeUpdate();
                }


                connection.commit();

            } catch (Exception exception) {

                connection.rollback();

                throw exception;
            }
        }
    }


    private void addSeat(
            Connection connection,
            int coachId,
            int seatNumber)
            throws Exception {

        String sql = """
                INSERT INTO seats
                    (
                        coach_id,
                        seat_number
                    )
                VALUES
                    (?, ?)
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    coachId
            );

            statement.setInt(
                    2,
                    seatNumber
            );

            statement.executeUpdate();
        }
    }


    private void addSeatsToSchedules(
            Connection connection,
            int trainId,
            int coachId)
            throws Exception {

        String seatIdSql = """
                SELECT seat_id
                FROM seats
                WHERE coach_id = ?
                ORDER BY seat_number
                """;

        String scheduleSeatSql = """
                INSERT IGNORE INTO schedule_seats
                    (
                        schedule_id,
                        seat_id,
                        status
                    )
                SELECT
                    schedule_id,
                    ?,
                    'AVAILABLE'
                FROM schedules
                WHERE train_id = ?
                  AND status <> 'CANCELLED'
                """;

        try (PreparedStatement seatStatement =
                     connection.prepareStatement(
                             seatIdSql);
             PreparedStatement scheduleStatement =
                     connection.prepareStatement(
                             scheduleSeatSql)) {

            seatStatement.setInt(
                    1,
                    coachId
            );

            try (ResultSet result =
                         seatStatement.executeQuery()) {

                while (result.next()) {

                    scheduleStatement.setInt(
                            1,
                            result.getInt("seat_id")
                    );

                    scheduleStatement.setInt(
                            2,
                            trainId
                    );

                    scheduleStatement.executeUpdate();
                }
            }
        }
    }


    private int getTrainId(
            Connection connection,
            int coachId)
            throws Exception {

        String sql = """
                SELECT train_id
                FROM coaches
                WHERE coach_id = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    coachId
            );

            try (ResultSet result =
                         statement.executeQuery()) {

                if (!result.next()) {

                    throw new Exception(
                            "Coach not found."
                    );
                }

                return result.getInt(
                        "train_id"
                );
            }
        }
    }


    private int getCoachId(
            Connection connection,
            int seatId)
            throws Exception {

        String sql = """
                SELECT coach_id
                FROM seats
                WHERE seat_id = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    seatId
            );

            try (ResultSet result =
                         statement.executeQuery()) {

                if (!result.next()) {

                    throw new Exception(
                            "Seat not found."
                    );
                }

                return result.getInt(
                        "coach_id"
                );
            }
        }
    }


    private int getCoachCapacity(
            Connection connection,
            int coachId)
            throws Exception {

        String sql = """
                SELECT seat_capacity
                FROM coaches
                WHERE coach_id = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    coachId
            );

            try (ResultSet result =
                         statement.executeQuery()) {

                if (!result.next()) {

                    throw new Exception(
                            "Coach not found."
                    );
                }

                return result.getInt(
                        "seat_capacity"
                );
            }
        }
    }


    private int getSeatCount(
            Connection connection,
            int coachId)
            throws Exception {

        String sql = """
                SELECT COUNT(*)
                FROM seats
                WHERE coach_id = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    coachId
            );

            try (ResultSet result =
                         statement.executeQuery()) {

                result.next();

                return result.getInt(1);
            }
        }
    }


    private int getBookedSeatsAboveNumber(
            Connection connection,
            int coachId,
            int seatNumber)
            throws Exception {

        String sql = """
                SELECT COUNT(*)
                FROM schedule_seats ss
                JOIN seats s
                    ON ss.seat_id = s.seat_id
                WHERE s.coach_id = ?
                  AND s.seat_number > ?
                  AND ss.status = 'BOOKED'
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    coachId
            );

            statement.setInt(
                    2,
                    seatNumber
            );

            try (ResultSet result =
                         statement.executeQuery()) {

                result.next();

                return result.getInt(1);
            }
        }
    }


    private int getSeatBookedCount(
            Connection connection,
            int seatId)
            throws Exception {

        String sql = """
                SELECT COUNT(*)
                FROM schedule_seats
                WHERE seat_id = ?
                  AND status = 'BOOKED'
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    seatId
            );

            try (ResultSet result =
                         statement.executeQuery()) {

                result.next();

                return result.getInt(1);
            }
        }
    }


    private void updateTrainCapacity(
            Connection connection,
            int trainId)
            throws Exception {

        String sql = """
                UPDATE trains
                SET total_capacity = (
                    SELECT COALESCE(
                        SUM(seat_capacity),
                        0
                    )
                    FROM coaches
                    WHERE train_id = ?
                )
                WHERE train_id = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    trainId
            );

            statement.setInt(
                    2,
                    trainId
            );

            statement.executeUpdate();
        }
    }
}