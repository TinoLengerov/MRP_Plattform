package org.example.server;

import com.sun.net.httpserver.HttpHandler;
import org.example.commonComponents.Application;
import com.sun.net.httpserver.HttpServer;
import org.example.server.auth.AuthHandler;
import org.example.server.auth.AuthService;
import org.example.server.util.RequestMapper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Server {

    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private HttpServer httpServer;
    private final int port;
    private final Application application;

    public Server(int port, Application application) {
        this.port = port;
        this.application = application;
    }

    /**
     * Starts the HTTP server using the routes defined in the Application.
     *
     * @throws IOException If an I/O error occurs.
     */
    public void start() throws IOException {
        Map<String, HttpHandler> routes = application.getRoutes();
        start(this.port, routes);
    }

    /**
     * Starts the HTTP server on the specified port and registers the provided contexts.
     *
     * @param port     The port number to start the server on.
     * @param contexts A map of URL paths to their corresponding HttpHandler implementations.
     * @throws IOException If an I/O error occurs.
     */
    public void start(int port, Map<String, HttpHandler> contexts) throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        // register contexts provided by caller (Application)
        for (Map.Entry<String, HttpHandler> e : contexts.entrySet()) {
            httpServer.createContext(e.getKey(), e.getValue());
        }
        httpServer.setExecutor(null);
        httpServer.start();
        LOGGER.info("Server started on port " + port);
    }

    /**
     * Stops the HTTP server.
     */
    public void stop() {
        if (this.httpServer != null) {
            this.httpServer.stop(0);
            LOGGER.info("Server stopped.");
        }
    }
}