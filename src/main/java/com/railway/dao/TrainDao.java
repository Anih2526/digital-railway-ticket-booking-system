package com.railway.dao;

import com.railway.db.Database;
import com.railway.model.Train;
import com.railway.model.TrainSearchResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TrainDao {

    public List<TrainSearchResult> searchTrains(
            String from,
            String to,
            LocalDate journeyDate,
            String trainQuery) throws Exception {

        List<TrainSearchResult> trains = new ArrayList<>();

        String sql = """
                SELECT
                    sc.schedule_id,
                    tr.train_number,
                    tr.train_name,
                    src.station_name AS source_station,
                    dest.station_name AS destination_station,
                    sc.departure_time,
                    sc.arrival_time,
                    sc.fare,
                    (
                        SELECT COUNT(*)
                        FROM schedule_seats ss2
                        WHERE ss2.schedule_id = sc.schedule_id
                          AND ss2.status = 'AVAILABLE'
                    ) AS available_seats
                FROM schedules sc
                JOIN trains tr
                    ON sc.train_id = tr.train_id
                JOIN routes r
                    ON sc.route_id = r.route_id
                JOIN stations src
                    ON r.source_station_id = src.station_id
                JOIN stations dest
                    ON r.destination_station_id = dest.station_id
                WHERE LOWER(src.station_name) = LOWER(?)
                  AND LOWER(dest.station_name) = LOWER(?)
                  AND DATE(sc.departure_time) = ?
                  AND sc.status = 'SCHEDULED'
                  AND (
                      ? = ''
                      OR LOWER(tr.train_number) LIKE LOWER(?)
                      OR LOWER(tr.train_name) LIKE LOWER(?)
                  )
                ORDER BY sc.departure_time
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, from);
            statement.setString(2, to);
            statement.setString(3, journeyDate.toString());

            String query =
                    trainQuery == null
                            ? ""
                            : trainQuery.trim();

            statement.setString(4, query);
            statement.setString(5, "%" + query + "%");
            statement.setString(6, "%" + query + "%");

            try (ResultSet result =
                         statement.executeQuery()) {

                while (result.next()) {

                    LocalDateTime departure =
                            result.getObject(
                                    "departure_time",
                                    LocalDateTime.class
                            );

                    LocalDateTime arrival =
                            result.getObject(
                                    "arrival_time",
                                    LocalDateTime.class
                            );

                    trains.add(
                            new TrainSearchResult(
                                    result.getInt(
                                            "schedule_id"
                                    ),
                                    result.getString(
                                            "train_number"
                                    ),
                                    result.getString(
                                            "train_name"
                                    ),
                                    result.getString(
                                            "source_station"
                                    ),
                                    result.getString(
                                            "destination_station"
                                    ),
                                    departure,
                                    arrival,
                                    result.getDouble(
                                            "fare"
                                    ),
                                    result.getInt(
                                            "available_seats"
                                    )
                            )
                    );
                }
            }
        }

        return trains;
    }


    /*
     * Returns all trains.
     *
     * total_capacity is kept here only so the Train
     * model can display the current capacity.
     *
     * Capacity itself is managed from the
     * Seats / Seat Capacity page.
     */
    public List<Train> getAllTrains()
            throws Exception {

        List<Train> trains =
                new ArrayList<>();

        String sql = """
                SELECT
                    train_id,
                    train_number,
                    train_name,
                    total_capacity
                FROM trains
                ORDER BY train_number
                """;

        try (Connection connection =
                     Database.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result =
                     statement.executeQuery()) {

            while (result.next()) {

                trains.add(
                        new Train(
                                result.getInt(
                                        "train_id"
                                ),
                                result.getString(
                                        "train_number"
                                ),
                                result.getString(
                                        "train_name"
                                ),
                                result.getInt(
                                        "total_capacity"
                                )
                        )
                );
            }
        }

        return trains;
    }


    /*
     * Adds a train.
     *
     * Capacity is initially set to 0.
     * The actual capacity is calculated automatically
     * when coaches/seats are added from the
     * Seats / Seat Capacity page.
     */
    public int addTrain(
            String trainNumber,
            String trainName)
            throws Exception {

        String sql = """
                INSERT INTO trains
                    (
                        train_number,
                        train_name,
                        total_capacity
                    )
                VALUES
                    (?, ?, 0)
                """;

        try (Connection connection =
                     Database.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             java.sql.Statement
                                     .RETURN_GENERATED_KEYS)) {

            statement.setString(
                    1,
                    trainNumber.trim()
            );

            statement.setString(
                    2,
                    trainName.trim()
            );

            statement.executeUpdate();

            try (ResultSet result =
                         statement.getGeneratedKeys()) {

                if (result.next()) {

                    return result.getInt(1);
                }
            }
        }

        throw new Exception(
                "Train could not be added."
        );
    }


    /*
     * Updates only train number and train name.
     *
     * Capacity is NOT changed here.
     * Capacity is controlled from the
     * Seats / Seat Capacity page.
     */
    public void updateTrain(
            int trainId,
            String trainNumber,
            String trainName)
            throws Exception {

        String sql = """
                UPDATE trains
                SET train_number = ?,
                    train_name = ?
                WHERE train_id = ?
                """;

        try (Connection connection =
                     Database.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    trainNumber.trim()
            );

            statement.setString(
                    2,
                    trainName.trim()
            );

            statement.setInt(
                    3,
                    trainId
            );

            statement.executeUpdate();
        }
    }
}