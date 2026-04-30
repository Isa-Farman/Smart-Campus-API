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

# Coursework Report — Question Answers

---

## Part 1: Service Architecture & Setup

### Question 1.1 — JAX-RS Resource Lifecycle

**Q: Explain the default lifecycle of a JAX-RS Resource class. Is a new instance created per request or is it a singleton? How does this affect in-memory data management?**

JAX-RS operates on a **per-request lifecycle** by default, which means a completely fresh instance of each resource class is created when an HTTP request arrives and then discarded once the response has been sent. This behaviour holds unless the class is explicitly marked with `@Singleton`, which tells the runtime to reuse one shared instance across all requests.

This design has direct consequences for how in-memory data must be handled. Because each request produces its own resource object, any data stored in instance-level fields will cease to exist the moment that request completes. For example, if a `HashMap<String, Room>` were declared as a field inside `RoomResource`, it would be wiped clean after every single request, making it impossible to retain any rooms between calls. To work around this, all shared state must live in **static fields** within a dedicated class such as `DataStore`, ensuring the data outlives any individual resource instance.

In this project, `DataStore` holds three static `ConcurrentHashMap` collections covering rooms, sensors, and readings. The choice of `ConcurrentHashMap` over a standard `HashMap` is important because a real web server handles multiple requests at the same time, each running on its own thread. A plain `HashMap` is not thread-safe — two threads writing to it simultaneously can corrupt the structure or cause data loss. `ConcurrentHashMap` handles concurrent access internally without needing explicit `synchronized` blocks, keeping the data safe and consistent across all simultaneous requests.

---

### Question 1.2 — HATEOAS and Hypermedia in REST

**Q: Why is the provision of "Hypermedia" (HATEOAS) considered a hallmark of advanced RESTful design? How does this approach benefit client developers compared to static documentation?**

HATEOAS, short for **Hypermedia As The Engine Of Application State**, is regarded as a defining quality of mature REST APIs. Its core idea is that every response should carry embedded links pointing to related resources and available actions, so a client can navigate the API by following those links rather than having to know every URL in advance.

Compared to static documentation, this approach offers several practical advantages for client developers:

**Reduced coupling** — When the server-side URL structure changes, the updated links appear in the response automatically. Clients that navigate by following links rather than hardcoding paths do not break when the API evolves.

**Lower barrier to entry** — A developer can begin at the root endpoint and explore the full API by reading the links in each response, without needing to consult any external reference material first.

**Greater resilience** — As the API grows and changes over time, a HATEOAS-driven client adapts naturally by following whatever links the server currently provides. A client built against a static document, by contrast, must be manually updated every time the API changes, or it will begin sending requests to invalid URLs.

---

## Part 2: Room Management

### Question 2.1 — IDs vs Full Objects in List Responses

**Q: When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client-side processing.**

Choosing between returning a list of room IDs or full room objects involves a trade-off between response size and the number of requests required.

Returning only IDs keeps each response very small, which is beneficial in bandwidth-limited environments. However, it forces the client to make an individual `GET /rooms/{id}` call for every ID in order to retrieve any meaningful information. This is the **N+1 request problem** — for a campus with 100 rooms, the client must fire 101 separate HTTP requests, adding significant latency and increasing load on the server.

Returning full room objects increases the size of the initial response but eliminates every follow-up request. The client receives everything it needs in a single round trip, which is considerably more efficient in practice. It also simplifies client-side code, since there is no need to implement logic that iterates over IDs and fetches each room individually.

In this implementation, `GET /api/v1/rooms` returns the complete room objects. This is the more practical approach for a campus management system, where a facilities manager would typically need room names, capacities, and associated sensor counts visible at once — not just a bare list of identifiers.

---

### Question 2.2 — Is DELETE Idempotent?

**Q: Is the DELETE operation idempotent in your implementation? Provide a detailed justification by describing what happens if a client mistakenly sends the exact same DELETE request for a room multiple times.**

The DELETE operation in this implementation is **idempotent**, meaning that sending the same request more than once produces the same end state on the server as sending it just once.

As a concrete example: if a client sends `DELETE /api/v1/rooms/T001` and the room exists with no sensors assigned, the room is removed and a `200 OK` is returned. If that exact same request is sent again, the room no longer exists in the data store, so the server returns `404 Not Found`. Crucially, idempotency is defined in terms of **server state**, not the response code. In both cases the outcome is identical — the room does not exist after the request — so the operation is considered idempotent regardless of the different status codes.

This is a well-established and widely accepted REST pattern. Sending the same DELETE request multiple times does not create duplicate deletions, corrupt any data, or leave orphaned sensors behind.

It is worth noting that if the room still has active sensors assigned when a DELETE is attempted, the request is blocked and a `409 Conflict` is returned. This prevents sensor records from being left without a valid parent room, preserving referential integrity across the in-memory data structures.

---

## Part 3: Sensor Operations & Linking

### Question 3.1 — `@Consumes` and Media Type Mismatches

**Q: We explicitly use the `@Consumes(MediaType.APPLICATION_JSON)` annotation on the POST method. Explain the technical consequences if a client attempts to send data in a different format, such as `text/plain` or `application/xml`. How does JAX-RS handle this mismatch?**

The `@Consumes(MediaType.APPLICATION_JSON)` annotation declares that the POST method will only accept request bodies where the `Content-Type` header is set to `application/json`. If a client sends a request using a different format — such as `text/plain` or `application/xml` — JAX-RS handles the situation entirely at the framework level, before any application code runs.

When the runtime receives the request, it inspects the `Content-Type` header and attempts to locate a resource method whose `@Consumes` declaration matches it. If no matching method is found, it immediately responds with **HTTP 415 Unsupported Media Type** and the method body is never invoked.

This is a meaningful safety guarantee. Without the `@Consumes` constraint, unexpected data formats could reach the deserialization layer and produce obscure `NullPointerException` or parsing failures deep within the code — errors that are harder to debug and far less informative to the client. By declaring the accepted format explicitly, the API enforces its contract at the framework level and returns a clear, standardised error to any client that violates it, without putting any application state at risk.

---

### Question 3.2 — `@QueryParam` vs Path Segment for Filtering

**Q: You implemented this filtering using `@QueryParam`. Contrast this with an alternative design where the type is part of the URL path (e.g., `/api/v1/sensors/type/CO2`). Why is the query parameter approach generally considered superior for filtering and searching collections?**

Using `@QueryParam` to filter a collection, as in `GET /api/v1/sensors?type=CO2`, is the preferred approach over embedding the filter value in the URL path for several interconnected reasons.

**Semantic correctness** is the most fundamental. In REST, a URL path identifies a specific resource. A path such as `/sensors/type/CO2` implies that `CO2` is a distinct resource nested under `type`, which has no meaning in this domain. A query parameter, by contrast, is understood to be a modifier on a collection request rather than a navigation step to a new resource.

**Optionality** is another key advantage. With `@QueryParam`, the filter is entirely optional — `GET /api/v1/sensors` returns all sensors, while `GET /api/v1/sensors?type=CO2` narrows the result. JAX-RS simply passes `null` when the parameter is absent, requiring no additional route. A path-based approach would need a separate route or conditional routing logic to handle the unfiltered case.

**Composability** is also significantly stronger with query parameters. Adding a second filter is as straightforward as appending another parameter, for example `?type=CO2&status=ACTIVE`. Achieving the same using path segments would require a rigid and increasingly unwieldy URL structure.

Finally, embedding a filter value in a path can create **routing conflicts** in JAX-RS, where `/{sensorId}` and `/type/{value}` are structurally ambiguous, potentially causing the wrong method to be matched entirely.

---

## Part 4: Deep Nesting with Sub-Resources

### Question 4.1 — The Sub-Resource Locator Pattern

**Q: Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path in one massive controller class?**

The Sub-Resource Locator is a JAX-RS pattern where a resource method does not return an HTTP response directly. Instead, it returns an instance of another resource class, which the runtime then uses to continue resolving the remaining portion of the URL. In this project, `SensorResource` contains a locator method annotated with `@Path("/{sensorId}/readings")` that instantiates and returns a `SensorReadingResource`, which handles the actual `GET` and `POST` operations on readings for that sensor.

The primary architectural benefit is **separation of concerns**. Each class has one clearly defined responsibility — `SensorResource` manages the sensor collection, and `SensorReadingResource` manages the reading history of a specific sensor. Neither class needs to understand the internal workings of the other beyond the point of delegation.

The alternative — defining every nested path inside a single controller class — becomes very difficult to manage in a large API. A monolithic class handling sensors, sensor readings, room assignments, and every nested path within them would grow rapidly, making it hard to read, test, and modify safely. Any change to reading logic would sit in the same file as sensor logic, raising the risk of accidentally introducing bugs in unrelated functionality.

The Sub-Resource Locator pattern enables **modular composition** instead. Each class can be developed and tested independently. Adding new sub-resources in the future — such as `/sensors/{id}/alerts` — requires only a new class and a single locator method, leaving all existing code untouched. This directly supports the Single Responsibility Principle and keeps the overall codebase maintainable as the API grows.

---

## Part 5: Advanced Error Handling, Exception Mapping & Logging

### Question 5.1 — HTTP 422 vs 404 for Missing References

**Q: Why is HTTP 422 often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?**

`404 Not Found` is the appropriate response when the resource identified by the **request URL** cannot be located on the server. For example, calling `GET /api/v1/rooms/UNKNOWN-ID` correctly returns a 404 because the URL itself points to something that does not exist.

The situation is different when a client sends a well-formed `POST` request to a valid endpoint — such as `/api/v1/sensors` — with a correctly structured JSON body, but the `roomId` value inside that body references a room that does not exist in the system. In this case the endpoint is reachable, the JSON is syntactically valid, and there is nothing wrong with the URL. The failure is entirely within the **content of the payload**, specifically a field that references a non-existent entity.

This is precisely the scenario that `422 Unprocessable Entity` was designed to communicate. It tells the client that the request was understood and parsed successfully, but could not be acted upon because of a semantic problem in the data. Using 404 here would be misleading — a developer receiving a 404 in response to a POST might assume the endpoint itself does not exist, rather than realising that a value inside their request body is invalid.

A 422 response, particularly when paired with a descriptive JSON error body identifying the problematic field and its value, gives the client developer the precise information needed to correct the request and resubmit it successfully.

---

### Question 5.2 — Security Risks of Exposing Stack Traces

**Q: From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?**

Returning raw Java stack traces in API responses is a significant security risk because they expose internal implementation details that were never intended to be public.

**Internal path disclosure** is one of the most immediate dangers. A stack trace often includes the full server-side file path where the error occurred, such as `/home/ubuntu/SmartCampusAPI/src/main/java/com/management`. This reveals the operating system, the deployment directory structure, and the package layout of the application — all useful information for planning further attacks.

**Technology and version fingerprinting** is another serious concern. Stack traces expose the exact names and versions of frameworks and libraries being used, such as `jersey-server-2.41` or `jackson-databind-2.13`. An attacker can cross-reference these against public vulnerability databases like the CVE registry to identify known exploits that target those specific versions, turning an accidental information leak into a direct attack vector.

**Application logic disclosure** is arguably the most dangerous risk. A stack trace reveals the exact sequence of method calls that led to the error, including class names, method signatures, and line numbers. This gives an attacker a detailed map of the application's internal control flow, making it far easier to identify weak points and craft inputs designed to trigger specific failure modes such as injection attacks or authentication bypasses.

For these reasons, the `GlobalExceptionMapper` in this implementation intercepts all unhandled exceptions, logs the full details server-side for legitimate debugging, and returns only a safe, generic error message to the client with no internal information exposed.

---

### Question 5.3 — JAX-RS Filters for Cross-Cutting Concerns

**Q: Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting `Logger.info()` statements inside every single resource method?**

Using a JAX-RS filter for logging is significantly more robust and maintainable than placing `Logger.info()` calls manually inside each resource method, for several reasons.

**Eliminating duplication** is the most immediate benefit. In an API with many endpoints, writing the same logging boilerplate into every method means the same code exists in dozens of places. If the log format ever needs to change, every single method must be updated individually — a process that is both time-consuming and prone to inconsistency. A filter centralises the logic in one place, so any change is made once and takes effect everywhere automatically.

**Separation of concerns** is a core principle that filters enforce naturally. A resource method's job is to process a request and produce a response — nothing more. Embedding logging statements directly into that logic mixes observability concerns with business logic, making methods harder to read and maintain. Filters keep these responsibilities cleanly separated.

**Guaranteed execution** is another critical advantage. A manually placed log statement can be accidentally omitted in a new method, deleted during a refactor, or skipped by an early return. A registered JAX-RS filter is invoked by the framework for every single request and response without exception, ensuring complete and consistent API observability regardless of how many endpoints exist or are added in the future.

---

