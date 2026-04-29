package com.smartcampus.campus_rest_api;

import com.smartcampus.campus_rest_api.models.SensorReading;
import com.smartcampus.campus_rest_api.models.Room;
import com.smartcampus.campus_rest_api.models.Sensor;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// storage for entire api.
// uses a singleteon pattern.
public class Storage {

    // Using ConcurrentHashMap ensures that if multiple API requests happen at the same time so the server will not crash.
    private static final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private static final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    private static final Map<String, List<SensorReading>> SensorReading = new ConcurrentHashMap<>();

    //getters
    public static Map<String, Room> getRooms() {
        return rooms;
    }

    public static Map<String, Sensor> getSensors() {
        return sensors;
    }

    public static Map<String, List<SensorReading>> getSensorReadings() {
        return SensorReading;
    }

    public static List<SensorReading> getReadingsForSensor(String sensorId) {
        return SensorReading.computeIfAbsent(sensorId, k -> new ArrayList<>());
    }
}