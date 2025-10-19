package org.example.server;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

import org.example.databases.Database;
import org.example.models.User;

import java.sql.SQLException;
import java.util.UUID;

public class UserService {

    private static final SecureRandom secureRandom = new SecureRandom();


    public User registerUser(String username, String password) throws IllegalArgumentException, SQLException {
        // Validation
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (username.length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters long");
        }

        // Check if user already exists
        User existingUser = Database.findUserByUsername(username);
        if (existingUser != null) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Create user
        Database.insertUser(username, password);
        return Database.findUserByUsername(username);
    }



    public String loginUser(String username, String password) throws IllegalArgumentException, SQLException {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Username and password cannot be null");
        }

        User user = Database.findUserByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        // In production, use proper password hashing (bcrypt)
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid password");
        }

        // Generate token
        String token = generateSecureToken();
        Database.storeToken(user.getId(), token, Instant.now().plus(30, ChronoUnit.DAYS));


        //String token = username + "-mrpToken-" + UUID.randomUUID().toString().substring(0, 8);
        //Database.storeToken(user.getId(), token);

        return token;
    }

    public static String generateSecureToken() {
        byte[] bytes = new byte[32]; // 256-bit
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public User getUserById(int userId) throws SQLException {
        return Database.findUserById(userId);
    }

    public User getUserByToken(String token) throws SQLException {
        Integer userId = Database.validateToken(token);
        if (userId != null) {
            return Database.findUserById(userId);
        }
        return null;
    }

    public void updateUser(int userId, String newUsername, String newPassword) throws IllegalArgumentException, SQLException {
        User user = Database.findUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        // Validate new username if provided
        if (newUsername != null && !newUsername.trim().isEmpty()) {
            if (newUsername.length() < 3) {
                throw new IllegalArgumentException("Username must be at least 3 characters long");
            }
            User userWithSameUsername = Database.findUserByUsername(newUsername);
            if (userWithSameUsername != null && userWithSameUsername.getId() != userId) {
                throw new IllegalArgumentException("Username already exists");
            }
        } else {
            // Keep existing username if not provided
            newUsername = user.getUsername();
        }

        // Keep existing password if not provided
        if (newPassword == null || newPassword.trim().isEmpty()) {
            User existingUser = Database.findUserByUsername(user.getUsername());
            newPassword = existingUser.getPassword();
        }

        Database.updateUser(userId, newUsername, newPassword);
    }

    public void deleteUser(int userId) throws SQLException {
        User user = Database.findUserById(userId);

        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        Database.deleteUser(userId);
    }
}