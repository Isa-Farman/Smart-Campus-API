# Smart Campus Sensor & Room Management API

## Overview

This project is a RESTful API built using **JAX-RS (Jersey)** for the University of Westminster's "Smart Campus" initiative. It provides a versioned, resource-oriented HTTP interface for managing campus **Rooms** and their associated **Sensors**, as well as maintaining a historical log of **SensorReadings**.

The API follows REST architectural principles including stateless communication, a clear resource hierarchy, meaningful HTTP status codes, and structured JSON responses. All data is stored in-memory using `HashMap` and `ArrayList` data structures — no external database is used.

---

## Technology Stack

| Technology                     | Purpose                                      |
|------------------------------|----------------------------------------------|
| Java 8                        | Core programming language                    |
| JAX-RS (Jersey 2.41)         | RESTful API framework                        |
| Apache Tomcat 9              | Servlet container and web server             |
| Maven                        | Build and dependency management              |
| Jackson                      | JSON serialization and deserialization       |
| ConcurrentHashMap            | Thread-safe in-memory data storage           |
---


## Project Structure

```
smart-campus-api/
├── src/main/java/com/smartcampus/
│   ├── app/
│   │   └── SmartCampusApplication.java       # JAX-RS Application entry point
│   ├── model/
│   │   ├── Room.java
│   │   ├── Sensor.java
│   │   └── SensorReading.java
│   ├── resource/
│   │   ├── DiscoveryResource.java
│   │   ├── RoomResource.java
│   │   ├── SensorResource.java
│   │   └── SensorReadingResource.java
│   ├── exception/
│   │   ├── RoomNotEmptyException.java
│   │   ├── LinkedResourceNotFoundException.java
│   │   ├── SensorUnavailableException.java
│   │   ├── RoomNotEmptyExceptionMapper.java
│   │   ├── LinkedResourceNotFoundExceptionMapper.java
│   │   ├── SensorUnavailableExceptionMapper.java
│   │   └── GlobalExceptionMapper.java
│   ├── filter/
│   │   └── LoggingFilter.java
│   └── store/
│       └── DataStore.java                    # In-memory data storage
├── src/main/webapp/WEB-INF/
│   └── web.xml
└── pom.xml
```

---

## How to Build and Run

### Prerequisites

- Java 11 or higher
- Apache Maven 3.6+

### Steps

1. **Clone the repository:**
   ```bash
   git clone https://github.com/Isa-Farman/Smart-Campus-API.git
   cd Smart-Campus-API
   ```

2. **Build the project:**
   ```bash
   mvn clean package
   ```

3. **Run the embedded server (Grizzly/Jetty):**
   ```bash
   mvn exec:java
   ```
   Or, if using the JAR:
   ```bash
   java -jar target/smart-campus-api.jar
   ```

4. **Access the API:**
   ```
   http://localhost:8080/api/v1
   ```

---

## Sample curl Commands

### 1. Discovery Endpoint
```bash
curl -X GET http://localhost:8080/api/v1
```

### 2. Create a Room
```bash
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id":"LIB-301","name":"Library Quiet Study","capacity":50}'
```

### 3. Get All Sensors Filtered by Type
```bash
curl -X GET "http://localhost:8080/api/v1/sensors?type=CO2"
```

### 4. Register a New Sensor
```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"CO2-001","type":"CO2","status":"ACTIVE","currentValue":412.5,"roomId":"LIB-301"}'
```

### 5. Post a Sensor Reading
```bash
curl -X POST http://localhost:8080/api/v1/sensors/CO2-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value":430.2}'
```

### 6. Get All Readings for a Sensor
```bash
curl -X GET http://localhost:8080/api/v1/sensors/CO2-001/readings
```

### 7. Delete a Room (safe — no sensors assigned)
```bash
curl -X DELETE http://localhost:8080/api/v1/rooms/LIB-301
```

---

---

## Available Endpoints

**Base URL:** `http://localhost:8080/campus_api/api/v1`

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1` | API discovery and metadata |
| `GET` | `/api/v1/rooms` | Get all rooms |
| `POST` | `/api/v1/rooms` | Create a new room |
| `GET` | `/api/v1/rooms/{roomId}` | Get a specific room by ID |
| `DELETE` | `/api/v1/rooms/{roomId}` | Delete a room (only if no sensors) |
| `GET` | `/api/v1/sensors` | Get all sensors (supports `?type=` filter) |
| `POST` | `/api/v1/sensors` | Register a new sensor |
| `GET` | `/api/v1/sensors/{sensorId}` | Get a specific sensor by ID |
| `GET` | `/api/v1/sensors/{sensorId}/readings` | Get all readings for a sensor |
| `POST` | `/api/v1/sensors/{sensorId}/readings` | Add a new reading for a sensor |

---
