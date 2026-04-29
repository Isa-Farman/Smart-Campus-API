package com.smartcampus.campus_rest_api.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;


@Path("/")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response discover() {
        Map<String, Object> body = new HashMap<>();
        body.put("name",        "API manager");
        body.put("version",     "1.0");
        body.put("contact",     "isa.20241345@iit.ac.lk");
        body.put("description", "REST API for managing a campus.");

        // Primary resource collection links.
        Map<String, String> resources = new HashMap<>();
        resources.put("rooms",   "/api/v1/rooms");
        resources.put("sensors", "/api/v1/sensors");
        body.put("resources", resources);

        // links block follows HATEOAS convention.
        Map<String, String> links = new HashMap<>();
        links.put("self",    "/api/v1");
        links.put("rooms",   "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");
        body.put("_links", links);

        return Response.ok(body).build();
    }
}