package com.smartcampus.campus_rest_api.exceptions; 

// ONLY triggered when someone tries to delete a room that still has active sensors inside it.
public class RoomNotEmptyException extends RuntimeException {
    public RoomNotEmptyException(String message) {
        super(message);
    }
}