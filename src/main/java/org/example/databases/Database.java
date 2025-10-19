package org.example.databases;

import java.sql.*;
import java.time.Instant;

import org.example.models.User;
import org.example.server.UserService;
import org.example.server.util.JsonUtil;

public class Database {
    private static final String URL = "jdbc:postgresql://localhost:5432/mrp_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "password";

    // Verbindung herstellen
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Beispiel: alle User auslesen
    public static void printAllUsers() {
        String sql = "SELECT * FROM users";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println(rs.getInt("id") + " - " + rs.getString("username"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Beispiel: neuen User einfügen
    public static void insertUser(String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
            System.out.println("User eingefügt!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateUser(int id, String newUsername, String newPassword) throws SQLException {
        String sql = "UPDATE users SET username = ?, password = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newUsername);
            ps.setString(2, newPassword);
            ps.setInt(3, id);
            ps.executeUpdate();
        }
    }

    public static void deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public static User findUserById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("id"), rs.getString("username"));
            }
        }
        return null;
    }

    public static User findUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"));
            }
        }
        return null;
    }

    public static void storeToken(int userId, String token, Instant expiresAt) throws SQLException {
        String sql = "INSERT INTO user_tokens (user_id, token, expires_at) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, token);
            // Use the provided expiresAt parameter instead of hardcoded time
            ps.setTimestamp(3, Timestamp.from(expiresAt));
            ps.executeUpdate();
        }
    }

    public static Integer validateToken(String token) throws SQLException {
        String sql = "SELECT user_id FROM user_tokens WHERE token = ? AND expires_at > CURRENT_TIMESTAMP";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        }
        return null;
    }


    public static void invalidateToken(String token) throws SQLException {
        String sql = "UPDATE user_tokens SET expires_at = CURRENT_TIMESTAMP WHERE token = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            ps.executeUpdate();
        }
    }

    public static void deleteUserByUsername(String username) throws SQLException {
        String sql = "DELETE FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.executeUpdate();
        }
    }

}
