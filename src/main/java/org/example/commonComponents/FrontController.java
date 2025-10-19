package org.example.commonComponents;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Optional;

public class FrontController implements HttpHandler {

    private final Router router;

    public FrontController(Router router) {
        this.router = router;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        Optional<Controller> opt = router.findController(path);
        if (opt.isPresent()) {
            try {
                opt.get().handle(exchange);
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, 0);
                exchange.getResponseBody().close();
            }
        } else {
            exchange.sendResponseHeaders(404, 0);
            exchange.getResponseBody().close();
        }
    }
}
