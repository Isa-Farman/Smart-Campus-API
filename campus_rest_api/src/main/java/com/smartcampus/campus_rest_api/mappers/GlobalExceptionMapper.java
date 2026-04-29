package com.smartcampus.campus_rest_api.mappers;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    //privately writes error details to console
    private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable ex) {
        // Pass through JAX-RS built-in exceptions 
        if (ex instanceof WebApplicationException) {
            return ((WebApplicationException) ex).getResponse();
        }
        // original java error to console
        LOG.log(Level.SEVERE, "Unhandled exception: " + ex.getMessage(), ex);

        Map<String, Object> body = new HashMap<>();
        body.put("status",  500);
        body.put("error",   "Internal Server Error");
        body.put("message", "An unexpected error occurred. Please contact support.");
        
        // writes the 500 error to postman as json
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(body).build();
    }
}