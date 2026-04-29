package com.smartcampus.campus_rest_api.mappers; 

import com.smartcampus.campus_rest_api.exceptions.RoomNotEmptyException;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

// This mapper will ONLY intercepts the RoomNotEmptyException
@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {

    @Override
    public Response toResponse(RoomNotEmptyException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status",  409);
        body.put("error",   "Conflict");
        body.put("message", ex.getMessage());
        body.put("hint",    "Remove or reassign all sensors before deleting this room.");
        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(body).build();
    }
}