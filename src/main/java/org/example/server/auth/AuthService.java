package org.example.server.auth;

import org.example.models.User;
import org.example.server.UserService;
import org.example.databases.Database;

import java.sql.SQLException;

/**
 * AuthService kapselt Authentifizierungs-Operationen.
 * - login delegiert an UserService.loginUser(...) und gibt Token zurück
 * - validateToken prüft Token über UserService.getUserByToken(...)
 * - logout markiert ein Token als ungültig (benötigt eine DB-Methode zum Entfernen/Invalidieren)
 */
public class AuthService {
    private final UserService userService;

    public AuthService(UserService userService) {
        this.userService = userService;
    }

    public boolean register(String username, String password, String displayName, String email) {
        try {
            User user = userService.registerUser(username, password);
            return user != null;
        } catch (Exception e) {
            return false;
        }
    }


    public String login(String username, String password) throws IllegalArgumentException, SQLException {
        return userService.loginUser(username, password);
    }

    public User validateToken(String token) throws SQLException {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }
        return userService.getUserByToken(token);
    }

    public void logout(String token) throws SQLException {
        if (token == null || token.trim().isEmpty()) {
            return;
        }
        Database.invalidateToken(token);
    }
}
