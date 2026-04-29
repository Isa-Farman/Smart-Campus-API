package com.smartcampus.campus_rest_api.exceptions;
//sent when a post reading is attempted on a sensor.
public class SensorUnavailableException extends RuntimeException {
    public SensorUnavailableException(String message) {
        super(message);
    }
}