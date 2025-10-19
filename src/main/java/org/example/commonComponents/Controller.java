package org.example.commonComponents;
import com.sun.net.httpserver.HttpHandler;
import org.example.server.http.*;

public interface Controller extends HttpHandler {
    public abstract Response handle(Request request);
}
