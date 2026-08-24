# Airport Ride Pooling API Documentation

## Overview

The Airport Ride Pooling API is a RESTful service for managing airport ride-sharing operations. It handles cab registration, booking management, and ride tracking.

**Base URL:** `http://localhost:8081`
**Server Port:** `8081`
**Database:** H2 (In-Memory)

---

## Table of Contents

1. [Bookings API](#bookings-api)
2. [Cabs API](#cabs-api)
3. [Rides API](#rides-api)
4. [Data Models](#data-models)
5. [Status Codes](#status-codes)

---

## Bookings API

### Get All Bookings

Retrieve a list of all bookings in the system.

**Endpoint:** `GET /api/bookings`

**Response:** `200 OK`

**Response Body:**
```json
[
  {
    "id": 1,
    "passenger": {
      "id": 1,
      "name": "Alice",
      "email": "alice@example.com",
      "detourTolerance": 15.0
    },
    "ride": {
      "id": 1,
      "status": "CREATED",
      "totalDistance": 16.71,
      "totalDeviation": 0.0
    },
    "sourceLat": 28.7041,
    "sourceLng": 77.1025,
    "destLat": 28.5562,
    "destLng": 77.1000,
    "requestedSeats": 2,
    "requestedLuggage": 1,
    "price": 250.65,
    "status": "CONFIRMED"
  }
]
```

---

### Create Booking

Create a new booking request. The system immediately attempts to pool the booking onto the best-matching `CREATED` ride, or assigns a fresh cab when no match exists.

**Endpoint:** `POST /api/bookings`

**Request Body:**
```json
{
  "passengerId": 1,
  "sourceLat": 28.7041,
  "sourceLng": 77.1025,
  "destLat": 28.5562,
  "destLng": 77.1000,
  "requestedSeats": 2,
  "requestedLuggage": 1
}
```

**Request Parameters:**

| Parameter | Type | Required | Constraints | Description |
|-----------|------|----------|-------------|-------------|
| `passengerId` | Long | Yes | > 0 | ID of an existing passenger |
| `sourceLat` | Double | Yes | -90..90 | Latitude of pickup location |
| `sourceLng` | Double | Yes | -180..180 | Longitude of pickup location |
| `destLat` | Double | Yes | -90..90 | Latitude of destination |
| `destLng` | Double | Yes | -180..180 | Longitude of destination |
| `requestedSeats` | Integer | Yes | >= 1 | Number of seats required |
| `requestedLuggage` | Integer | Yes | >= 0 | Number of luggage items |

**Response:** `200 OK`

The created booking with status `CONFIRMED`, its assigned ride summary, and a dynamically computed price.

**Error Responses:**

- `400 Bad Request` - Validation failure
  ```json
  {
    "error": "Validation failed",
    "details": "requestedSeats: requestedSeats must be at least 1"
  }
  ```
- `404 Not Found` - Passenger does not exist
  ```json
  { "error": "Passenger not found" }
  ```
- `409 Conflict` - No available cab and no existing ride can accommodate the request, or a concurrent-update conflict persisted after automatic retries
  ```json
  { "error": "No cabs available and no existing ride can accommodate this booking" }
  ```

---

### Cancel Booking

Cancel an existing booking by ID. Freed seats/luggage become reusable by future bookings on the same ride. When all bookings of a ride are cancelled, the ride is closed and its cab returns to `AVAILABLE`. Cancelling twice is idempotent.

**Endpoint:** `DELETE /api/bookings/{id}`

**Path Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `id` | Long | Yes | ID of the booking to cancel |

**Responses:**

- `204 No Content` - Booking cancelled
- `404 Not Found` - Booking does not exist
  ```json
  { "error": "Booking not found" }
  ```

---

## Cabs API

### Register Cab

Register a new cab in the system.

**Endpoint:** `POST /api/cabs`

**Request Body:**
```json
{
  "driverName": "John Smith",
  "licensePlate": "ABC-1234",
  "totalSeats": 4,
  "luggageCapacity": 3
}
```

**Request Parameters:**

| Parameter | Type | Required | Constraints | Description |
|-----------|------|----------|-------------|-------------|
| `driverName` | String | Yes | not blank | Name of the cab driver |
| `licensePlate` | String | Yes | not blank, unique (case-insensitive) | License plate number |
| `totalSeats` | Integer | Yes | >= 1 | Total number of seats in the cab |
| `luggageCapacity` | Integer | Yes | >= 0 | Maximum luggage capacity |

**Note:** The `status` field is automatically set to `AVAILABLE`; any client-supplied `id`/`version` are ignored.

**Response:** `200 OK`

**Response Body:**
```json
{
  "id": 3,
  "driverName": "John Smith",
  "licensePlate": "ABC-1234",
  "totalSeats": 4,
  "luggageCapacity": 3,
  "status": "AVAILABLE",
  "version": 0
}
```

**Error Responses:**

- `400 Bad Request` - Validation failure
- `409 Conflict` - Duplicate license plate
  ```json
  { "error": "Cab with license plate ABC-1234 already exists" }
  ```

---

### Get All Cabs

Retrieve a list of all registered cabs.

**Endpoint:** `GET /api/cabs`

**Response:** `200 OK`

**Response Body:** Array of Cab objects (see [Cab](#cab)).

---

## Rides API

### Get All Rides

Retrieve a list of all rides in the system.

**Endpoint:** `GET /api/rides`

**Response:** `200 OK`

**Response Body:**
```json
[
  {
    "id": 1,
    "cab": {
      "id": 1,
      "driverName": "John Driver",
      "licensePlate": "XYZ-123",
      "totalSeats": 4,
      "luggageCapacity": 3,
      "status": "BUSY",
      "version": 1
    },
    "bookings": [
      {
        "id": 1,
        "sourceLat": 28.7041,
        "sourceLng": 77.1025,
        "destLat": 28.5562,
        "destLng": 77.1000,
        "requestedSeats": 2,
        "requestedLuggage": 1,
        "price": 250.65,
        "status": "CONFIRMED"
      }
    ],
    "status": "CREATED",
    "totalDistance": 16.71,
    "totalDeviation": 0.12
  }
]
```

---

### Get Ride by ID

Retrieve a specific ride by its ID.

**Endpoint:** `GET /api/rides/{id}`

**Path Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `id` | Long | Yes | ID of the ride |

**Responses:**

- `200 OK` - Ride object (see above)
- `404 Not Found` - Ride does not exist (empty body)

---

## Data Models

### Booking (response)

| Field | Type | Description |
|-------|------|-------------|
| `id` | Long | Unique identifier (auto-generated) |
| `passenger` | Passenger | The passenger making the booking |
| `ride` | RideSummary \| null | Flattened ride reference (`id`, `status`, `totalDistance`, `totalDeviation`) |
| `sourceLat` / `sourceLng` | Double | Pickup coordinates |
| `destLat` / `destLng` | Double | Destination coordinates |
| `requestedSeats` | Integer | Number of seats requested |
| `requestedLuggage` | Integer | Number of luggage items |
| `price` | Double | Dynamically computed price |
| `status` | BookingStatus | Current status |

### BookingStatus Enum

- `PENDING` - Booking is pending assignment (not currently produced)
- `CONFIRMED` - Booking has been confirmed and assigned to a ride
- `CANCELLED` - Booking has been cancelled
- `COMPLETED` - Booking has been completed

---

### Cab

| Field | Type | Description |
|-------|------|-------------|
| `id` | Long | Unique identifier (auto-generated) |
| `driverName` | String | Name of the cab driver |
| `licensePlate` | String | License plate number (unique) |
| `totalSeats` | Integer | Total number of seats |
| `luggageCapacity` | Integer | Maximum luggage capacity |
| `status` | CabStatus | Current status |
| `version` | Long | Version number for optimistic locking |

### CabStatus Enum

- `AVAILABLE` - Cab is available for new rides
- `BUSY` - Cab is currently on a ride
- `OFFLINE` - Cab is offline/unavailable

---

### Ride (response)

| Field | Type | Description |
|-------|------|-------------|
| `id` | Long | Unique identifier (auto-generated) |
| `cab` | Cab | Cab assigned to this ride |
| `bookings` | List&lt;BookingSummary&gt; | Flat booking views attached to this ride (no nested `ride`/`passenger` references) |
| `status` | RideStatus | Current status |
| `totalDistance` | Double | Accumulated haversine distance of member bookings (km) |
| `totalDeviation` | Double | Accumulated pickup deviation of pooled bookings (km) |

### RideStatus Enum

- `CREATED` - Ride is open and accepting pooled bookings
- `ONGOING` - Ride is in progress
- `COMPLETED` - Ride has been completed
- `CANCELLED` - Ride was cancelled (all bookings cancelled)

---

### Passenger

| Field | Type | Description |
|-------|------|-------------|
| `id` | Long | Unique identifier (auto-generated) |
| `name` | String | Name of the passenger |
| `email` | String | Email address of the passenger |
| `detourTolerance` | Double | Maximum detour tolerance in kilometers used during matching |

---

### Pricing Formula

```
Price = (BaseFare + HaversineDistance * RatePerKm) * DemandFactor * (1 - PoolingDiscount)
BaseFare = 50.0, RatePerKm = 12.0
DemandFactor = 1.0 + 0.5 * (busyCabs / totalCabs)   // range 1.0 .. 1.5
PoolingDiscount = 0.30 when more than one passenger shares the ride, else 0
```

---

## Status Codes

| Code | Description |
|------|-------------|
| `200 OK` | Request successful |
| `204 No Content` | Cancellation successful |
| `400 Bad Request` | Request body failed validation |
| `404 Not Found` | Resource not found |
| `409 Conflict` | Duplicate resource, no capacity available, or concurrent modification after retries |
| `500 Internal Server Error` | Unexpected server error |

---

## Example Usage

### Creating a Booking

```bash
curl -X POST http://localhost:8081/api/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "passengerId": 1,
    "sourceLat": 28.7041,
    "sourceLng": 77.1025,
    "destLat": 28.5562,
    "destLng": 77.1000,
    "requestedSeats": 2,
    "requestedLuggage": 1
  }'
```

### Registering a Cab

```bash
curl -X POST http://localhost:8081/api/cabs \
  -H "Content-Type: application/json" \
  -d '{
    "driverName": "John Smith",
    "licensePlate": "ABC-1234",
    "totalSeats": 4,
    "luggageCapacity": 3
  }'
```

### Getting All Rides

```bash
curl -X GET http://localhost:8081/api/rides
```

### Cancelling a Booking

```bash
curl -X DELETE http://localhost:8081/api/bookings/1
```

---

## Notes

- The API uses H2 in-memory database, so data will be lost when the application restarts
- H2 Console is enabled and accessible at `http://localhost:8081/h2-console`
- Database credentials:
  - URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: `password`
- The application uses Spring Boot 3.2.2 with Java 17
- Swagger UI is available at `http://localhost:8081/swagger-ui/index.html`

---

## Additional Resources

- **Spring Boot Documentation:** https://spring.io/projects/spring-boot
- **H2 Database:** https://www.h2database.com/html/main.html
