package com.railway.dao;

import com.railway.db.Database;
import com.railway.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDao {

    public User findByEmailAndPassword(
            String email,
            String password) throws Exception {

        String sql = """
                SELECT user_id, full_name, email, password, role
                FROM users
                WHERE email = ? AND password = ?
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);
            statement.setString(2, password);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return new User(
                            result.getInt("user_id"),
                            result.getString("full_name"),
                            result.getString("email"),
                            result.getString("password"),
                            result.getString("role")
                    );
                }
            }
        }

        return null;
    }

    public boolean emailExists(String email) throws Exception {

        String sql = "SELECT user_id FROM users WHERE email = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);

            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    public int createUser(
            String fullName,
            String email,
            String password) throws Exception {

        String sql = """
                INSERT INTO users (full_name, email, password, role)
                VALUES (?, ?, ?, 'USER')
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql,
                     java.sql.Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, fullName);
            statement.setString(2, email);
            statement.setString(3, password);

            statement.executeUpdate();

            try (ResultSet result = statement.getGeneratedKeys()) {
                if (result.next()) {
                    return result.getInt(1);
                }
            }
        }

        return 0;
    }
}
