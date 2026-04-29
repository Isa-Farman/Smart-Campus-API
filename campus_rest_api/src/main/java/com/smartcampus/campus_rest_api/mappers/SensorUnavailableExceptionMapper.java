package com.smartcampus.campus_rest_api.mappers;

import com.smartcampus.campus_rest_api.exceptions.SensorUnavailableException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;


@Provider
public class SensorUnavailableExceptionMapper
        implements ExceptionMapper<SensorUnavailableException> {

    @Override
    public Response toResponse(SensorUnavailableException ex) {
        // this builds a custom json response.
        Map<String, Object> body = new HashMap<>();
        body.put("status",  403);
        body.put("error",   "Forbidden");
        body.put("message", ex.getMessage());
        body.put("hint",    "Sensor must be ACTIVE to accept new readings.");
        
        // Returns a 403 forbidden code because the sensor exists, but the action violates business rules
        return Response.status(Response.Status.FORBIDDEN)
                .type(MediaType.APPLICATION_JSON)
                .entity(body).build();
    }
}