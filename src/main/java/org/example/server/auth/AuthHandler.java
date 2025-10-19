package org.example.server.auth;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.models.User;
import org.example.server.util.JsonUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;

/**
 * HTTP-Handler für Auth-Endpoints (login, logout, token-check).
 *
 * Pfade (Beispiel):
 * - POST /auth/login   -> body: {"username":"...","password":"..."}  -> returns {"token":"..."}
 * - GET  /auth/me      -> Header: Authorization: Bearer <token>     -> returns user info
 *
 * Du musst den Server so konfigurieren, dass Requests mit Pfad /auth/* an diesen Handler gehen.
 */
public class AuthHandler implements HttpHandler {

    private final AuthService authService;

    public AuthHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath(); // z.B. /auth/login

        try {
            if (method.equalsIgnoreCase("POST") && path.endsWith("/login")) {
                handleLogin(exchange);
                return;
            }

            if (method.equalsIgnoreCase("POST") && path.endsWith("/register")) {
                handleRegister(exchange);
                return;
            }

            if (method.equalsIgnoreCase("POST") && path.endsWith("/logout")) {
                handleLogout(exchange);
                return;
            }

            if (method.equalsIgnoreCase("GET") && path.endsWith("/me")) {
                handleMe(exchange);
                return;
            }

            // unknown route
            sendJson(exchange, 404, Map.of("error", "Not found"));
        } catch (IllegalArgumentException e) {
            sendJson(exchange, 400, Map.of("error", e.getMessage()));
        } catch (SQLException e) {
            e.printStackTrace();
            sendJson(exchange, 500, Map.of("error", "Database error"));
        } catch (Exception e) {
            e.printStackTrace();
            sendJson(exchange, 500, Map.of("error", "Internal server error"));
        }
    }

    private void handleLogin(HttpExchange exchange) throws IOException, SQLException {
        Map<String, Object> body = parseRequestBody(exchange);
        String username = (body.get("username") == null) ? null : body.get("username").toString();
        String password = (body.get("password") == null) ? null : body.get("password").toString();

        if (username == null || password == null) {
            sendJson(exchange, 400, Map.of("error", "username and password required"));
            return;
        }

        String token = authService.login(username, password);
        sendJson(exchange, 200, Map.of("token", token));
    }

    private void handleRegister(HttpExchange exchange) throws IOException, SQLException {
        Map<String, Object> body = parseRequestBody(exchange);
        String username = getStringFromBody(body, "username");
        String password = getStringFromBody(body, "password");

        if (username == null || password == null) {
            sendJson(exchange, 400, Map.of("error", "username and password required"));
            return;
        }

        boolean success = authService.register(username, password, null, null);
        if (success) {
            sendJson(exchange, 201, Map.of("message", "User registered successfully"));
        } else {
            sendJson(exchange, 400, Map.of("error", "Registration failed - user may already exist"));
        }
    }

    private void handleLogout(HttpExchange exchange) throws IOException, SQLException {
        String token = extractTokenFromHeader(exchange);

        if (token == null) {
            sendJson(exchange, 400, Map.of("error", "Token required in Authorization header"));
            return;
        }

        authService.logout(token);
        sendJson(exchange, 200, Map.of("message", "Logged out successfully"));
    }

    private void handleMe(HttpExchange exchange) throws IOException, SQLException {
        // Token from Authorization header
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendJson(exchange, 401, Map.of("error", "Authorization header required"));
            return;
        }
        String token = authHeader.substring("Bearer ".length());
        User user = authService.validateToken(token);
        if (user == null) {
            sendJson(exchange, 401, Map.of("error", "Invalid token"));
            return;
        }

        // Gib Benutzerinformationen zurück (nicht das Passwort)
        sendJson(exchange, 200, Map.of(
                "id", user.getId(),
                "username", user.getUsername()
        ));
    }

    // Hilfsfunktionen


    private String getStringFromBody(Map<String, Object> body, String key) {
        return body.get(key) == null ? null : body.get(key).toString();
    }

    private String extractTokenFromHeader(HttpExchange exchange) {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring("Bearer ".length());
        }
        return null;
    }


    private Map<String, Object> parseRequestBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        if (body == null || body.isBlank()) {
            return Map.of();
        }
        return JsonUtil.fromJson(body, Map.class);
    }

    private void sendJson(HttpExchange exchange, int statusCode, Object object) throws IOException {
        String json = JsonUtil.toJson(object);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}
