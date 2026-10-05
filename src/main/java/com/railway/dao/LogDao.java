package com.railway.dao;

import com.railway.db.Database;
import com.railway.model.SystemLog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LogDao {

    public void createLog(
            Connection connection,
            Integer userId,
            String action,
            String message) throws Exception {

        String sql = """
                INSERT INTO system_logs (user_id, action, message)
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            if (userId == null) {
                statement.setNull(1, java.sql.Types.INTEGER);
            } else {
                statement.setInt(1, userId);
            }

            statement.setString(2, action);
            statement.setString(3, message);
            statement.executeUpdate();
        }
    }

    public void createLog(
            Integer userId,
            String action,
            String message) throws Exception {

        try (Connection connection = Database.getConnection()) {
            createLog(connection, userId, action, message);
        }
    }

    public List<SystemLog> getRecentLogs() throws Exception {

        List<SystemLog> logs = new ArrayList<>();

        String sql = """
                SELECT l.action,
                       l.message,
                       u.email,
                       l.log_time
                FROM system_logs l
                LEFT JOIN users u ON l.user_id = u.user_id
                ORDER BY l.log_time DESC
                LIMIT 100
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                logs.add(new SystemLog(
                        result.getString("action"),
                        result.getString("message"),
                        result.getString("email"),
                        result.getObject("log_time", LocalDateTime.class)
                ));
            }
        }

        return logs;
    }
}
