package org.example.commonComponents;

import com.sun.net.httpserver.HttpHandler;
import org.example.server.UserHandler;
import org.example.server.UserService;
import org.example.server.auth.AuthHandler;
import org.example.server.auth.AuthService;
import org.example.server.http.Request;
import org.example.server.http.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * Default-Implementierung der Application, erstellt Services + Handler und liefert die Routen-Map.
 */
public class DefaultApplication implements Application {

    @Override
    public Response handle(Request request) {
        throw new UnsupportedOperationException("handle(Request) is not used in this demo application.");
    }

    @Override
    public Map<String, HttpHandler> getRoutes() {
        // create business services
        UserService userService = new UserService();
        AuthService authService = new AuthService(userService);

        // create handlers (HttpHandler implementations)
        AuthHandler authHandler = new AuthHandler(authService);
        UserHandler userHandler = new UserHandler(userService);

        // build routing map (only user/auth for now)
        Map<String, HttpHandler> routes = new HashMap<>();
        routes.put("/auth", authHandler);
        routes.put("/users", userHandler);

        return routes;
    }
}

/*
public static void main(String[] args) throws IOException {
    int port = 8080;
    new Application().start(port);
}

public static void start(int port) throws IOException {
    // create business services
    UserService userService = new UserService();
    AuthService authService = new AuthService(userService);

    // create handlers (HttpHandler implementations)
    AuthHandler authHandler = new AuthHandler(authService);
    UserHandler userHandler = new UserHandler(userService);

    // build routing map (only user/auth for now)
    Map<String, HttpHandler> routes = new HashMap<>();
    routes.put("/auth", authHandler);
    routes.put("/users", userHandler);

    // start server with routes
    Server server = new Server();
    server.start(port, routes);

    // add graceful shutdown hook
    Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
}

 */