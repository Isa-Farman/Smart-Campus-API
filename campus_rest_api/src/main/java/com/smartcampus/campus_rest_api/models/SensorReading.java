package com.smartcampus.campus_rest_api.models;

// everytime a sensor takes a measurement a Readings Object is then created.
public class SensorReading {
    

    private String id;
    private long timestamp; 
    private double value;

    public SensorReading() {
    }

    public SensorReading(String id, long timestamp, double value) {
        this.id = id;
        this.timestamp = timestamp;
        this.value = value;
    }   

    // getters and setters.
    public String getId() { 
        return id; 
    }
    
    public void setId(String id) { 
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp; 
    }
    
    public void setTimestamp(long timestamp) { 
        this.timestamp = timestamp; 
    }

    public double getValue() { 
        return value; 
    }
    
    public void setValue(double value) { 
        this.value = value; 
    }
}