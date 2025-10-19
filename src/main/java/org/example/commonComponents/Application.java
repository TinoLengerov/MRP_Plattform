package org.example.commonComponents;

import com.sun.net.httpserver.HttpHandler;
import org.example.server.http.Request;
import org.example.server.http.Response;

import java.util.Map;

public interface Application {
    Response handle(Request request);
    Map<String, HttpHandler> getRoutes();
}