package com.railway.dao;

import com.railway.db.Database;
import com.railway.model.Route;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RouteDao {

    public List<Route> getAllRoutes() throws Exception {

        List<Route> routes = new ArrayList<>();

        String sql = """
                SELECT
                    r.route_id,
                    r.route_name,
                    r.source_station_id,
                    r.destination_station_id,
                    src.station_name AS source_station,
                    dest.station_name AS destination_station
                FROM routes r
                JOIN stations src
                    ON r.source_station_id = src.station_id
                JOIN stations dest
                    ON r.destination_station_id = dest.station_id
                ORDER BY r.route_name
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                routes.add(new Route(
                        result.getInt("route_id"),
                        result.getString("route_name"),
                        result.getInt("source_station_id"),
                        result.getInt("destination_station_id"),
                        result.getString("source_station"),
                        result.getString("destination_station")
                ));
            }
        }

        return routes;
    }

    public void addRoute(
            String routeName,
            int sourceStationId,
            int destinationStationId) throws Exception {

        if (routeName == null || routeName.isBlank()) {
            throw new Exception("Route name is required.");
        }

        if (sourceStationId == destinationStationId) {
            throw new Exception("Source and destination cannot be the same.");
        }

        String sql = """
                INSERT INTO routes
                    (route_name, source_station_id, destination_station_id)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, routeName.trim());
            statement.setInt(2, sourceStationId);
            statement.setInt(3, destinationStationId);
            statement.executeUpdate();
        }
    }

    public void updateRoute(
            int routeId,
            String routeName,
            int sourceStationId,
            int destinationStationId) throws Exception {

        if (routeName == null || routeName.isBlank()) {
            throw new Exception("Route name is required.");
        }

        if (sourceStationId == destinationStationId) {
            throw new Exception("Source and destination cannot be the same.");
        }

        String sql = """
                UPDATE routes
                SET route_name = ?,
                    source_station_id = ?,
                    destination_station_id = ?
                WHERE route_id = ?
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, routeName.trim());
            statement.setInt(2, sourceStationId);
            statement.setInt(3, destinationStationId);
            statement.setInt(4, routeId);
            statement.executeUpdate();
        }
    }
}
