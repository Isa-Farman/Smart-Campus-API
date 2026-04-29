package com.smartcampus.campus_rest_api.exceptions;

//sent when a value is assigned to a parent item that doesn't exist 
public class LinkedResourceNotFoundException extends RuntimeException {
    public LinkedResourceNotFoundException(String message) {
        super(message);
    }
}