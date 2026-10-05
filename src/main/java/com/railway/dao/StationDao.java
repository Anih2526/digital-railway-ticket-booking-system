package com.railway.dao;

import com.railway.db.Database;
import com.railway.model.Station;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class StationDao {

    public List<Station> getAllStations() throws Exception {

        List<Station> stations = new ArrayList<>();

        String sql = """
                SELECT station_id, station_code, station_name
                FROM stations
                ORDER BY station_name
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                stations.add(new Station(
                        result.getInt("station_id"),
                        result.getString("station_code"),
                        result.getString("station_name")
                ));
            }
        }

        return stations;
    }

    public List<String> getAllStationNames() throws Exception {

        List<String> stations = new ArrayList<>();

        String sql = "SELECT station_name FROM stations ORDER BY station_name";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                stations.add(result.getString("station_name"));
            }
        }

        return stations;
    }

    public void addStation(String stationCode, String stationName) throws Exception {

        if (stationCode == null || stationCode.isBlank()
                || stationName == null || stationName.isBlank()) {
            throw new Exception("Station code and station name are required.");
        }

        String sql = """
                INSERT INTO stations (station_code, station_name)
                VALUES (?, ?)
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, stationCode.trim().toUpperCase());
            statement.setString(2, stationName.trim());
            statement.executeUpdate();
        }
    }

    public void updateStation(
            int stationId,
            String stationCode,
            String stationName) throws Exception {

        if (stationCode == null || stationCode.isBlank()
                || stationName == null || stationName.isBlank()) {
            throw new Exception("Station code and station name are required.");
        }

        String sql = """
                UPDATE stations
                SET station_code = ?, station_name = ?
                WHERE station_id = ?
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, stationCode.trim().toUpperCase());
            statement.setString(2, stationName.trim());
            statement.setInt(3, stationId);
            statement.executeUpdate();
        }
    }
}
