/*package org.example.echo;

import org.example.commonComponents.Application;
import org.example.server.http.ContentType;
import org.example.server.http.Status;
import org.example.server.http.Response;
import org.example.server.http.Request;

public class echoApplication implements Application {

    @Override
    public Response handle(Request request) {
        Response response = new Response();

        response.setStatus(Status.OK);
        response.setContentType(ContentType.TEXT_PLAIN);
        response.setBody(
                "%s %s".formatted(
                        request.getMethod(),
                        request.getPath()
                )
        );

        return response;
    }
}

 */