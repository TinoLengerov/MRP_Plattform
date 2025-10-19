// src/main/java/org/example/server/UserHandler.java
package org.example.server;

import com.sun.net.httpserver.HttpExchange;
import org.example.models.User;
import org.example.server.util.JsonUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;

/**
 * Handles /users endpoints:
 * - POST /users         -> register user (body: { "username":"..", "password":".." })
 * - GET  /users/{id}    -> get user info
 * - PUT  /users/{id}    -> update user (body: { "username":.., "password":.. })
 * - DELETE /users/{id}  -> delete user
 */
public class UserHandler implements com.sun.net.httpserver.HttpHandler {

    private final UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath(); // e.g. /users or /users/1

            if (method.equalsIgnoreCase("POST") && path.equals("/users")) {
                handleCreate(exchange);
                return;
            }

            if (path.startsWith("/users/")) {
                String idStr = path.substring("/users/".length());
                int id;
                try {
                    id = Integer.parseInt(idStr);
                } catch (NumberFormatException e) {
                    sendJson(exchange, 400, Map.of("error", "invalid id"));
                    return;
                }

                if (method.equalsIgnoreCase("GET")) {
                    handleGet(exchange, id);
                    return;
                }
                if (method.equalsIgnoreCase("PUT")) {
                    handleEdit(exchange, id);
                    return;
                }
                if (method.equalsIgnoreCase("DELETE")) {
                    handleDelete(exchange, id);
                    return;
                }
            }

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

    private Map<String, Object> parseRequestBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        if (body == null || body.isBlank()) {
            return Map.of();
        }
        return JsonUtil.fromJsonToMap(body);
    }

    private void handleCreate(HttpExchange exchange) throws IOException, SQLException {
        Map<String, Object> body = parseRequestBody(exchange);
        String username = body.get("username") == null ? null : body.get("username").toString();
        String password = body.get("password") == null ? null : body.get("password").toString();

        if (username == null || password == null) {
            sendJson(exchange, 400, Map.of("error", "username and password required"));
            return;
        }

        User created = userService.registerUser(username, password);
        sendJson(exchange, 201, Map.of("id", created.getId(), "username", created.getUsername()));
    }

    private void handleGet(HttpExchange exchange, int userId) throws IOException, SQLException {
        User user = userService.getUserById(userId);
        if (user == null) {
            sendJson(exchange, 404, Map.of("error", "User not found"));
            return;
        }
        sendJson(exchange, 200, Map.of("id", user.getId(), "username", user.getUsername()));
    }

    private void handleEdit(HttpExchange exchange, int userId) throws IOException, SQLException {
        Map<String, Object> body = parseRequestBody(exchange);
        String username = body.get("username") == null ? null : body.get("username").toString();
        String password = body.get("password") == null ? null : body.get("password").toString();

        userService.updateUser(userId, username, password);
        sendJson(exchange, 200, Map.of("ok", true));
    }

    private void handleDelete(HttpExchange exchange, int userId) throws IOException, SQLException {
        userService.deleteUser(userId);
        sendJson(exchange, 200, Map.of("ok", true));
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
