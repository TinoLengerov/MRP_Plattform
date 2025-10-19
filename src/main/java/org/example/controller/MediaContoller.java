package org.example.controller;

import org.example.server.*;
import org.example.services.*;


//public class MediaContoller extends Controller {

    /*private final MediaService mediaService;

    public MediaController() {
        this.mediaService = new MediaService();
    }

    @Override
    public Response handle(Request request) {
        if (request.getMethod().equals("GET")) {
            return readWeather(request);
        }

        // TODO
        throw new RuntimeException("404");
    }

    // private User readUser(String id)

    private Response readWeather(Request request) {

        String city = request.getPath().split("/")[2];
        Weather weather = weatherService.getByCity(city);

        Response response = new Response();
        response.setStatus(Status.OK);
        response.setContentType(ContentType.TEXT_PLAIN);
        response.setBody(String.valueOf(weather.getTemperature()));

        return response;
    }
}*/