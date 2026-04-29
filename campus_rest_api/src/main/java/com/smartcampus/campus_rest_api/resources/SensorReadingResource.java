package com.smartcampus.campus_rest_api.resources;

import com.smartcampus.campus_rest_api.Storage;
import com.smartcampus.campus_rest_api.exceptions.SensorUnavailableException;
import com.smartcampus.campus_rest_api.models.SensorReading;
import com.smartcampus.campus_rest_api.models.Sensor;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;

    // sensor resource routes traffic to this.
    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    public Response getReadings() { 
        List<SensorReading> readings = Storage.getReadingsForSensor(sensorId); 
        return Response.ok(readings).build();
    }

    @POST // posts the new readings received.
    public Response addReading(SensorReading reading) {
        Sensor sensor = Storage.getSensors().get(sensorId); 
        // this prevents offline or broken sensors from recording new data
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(
                "Sensor '" + sensorId + "' is under MAINTENANCE and cannot accept readings.");
        }
        if ("OFFLINE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(
                "Sensor '" + sensorId + "' is OFFLINE and cannot accept readings.");
        }
        // If the hardware failed to send an ID it then generate a secure UUID
        if (reading.getId() == null || reading.getId().trim().isEmpty()) {
            reading.setId(UUID.randomUUID().toString());
        }
        // If the hardware failed to send a time then this uses the server's current UNIX time.
        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        // saves the data to storage.
        Storage.getReadingsForSensor(sensorId).add(reading);
        
        // it update the main sensor value.
        sensor.setCurrentValue(reading.getValue());

        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}