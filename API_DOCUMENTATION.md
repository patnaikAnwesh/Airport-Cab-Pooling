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
      "name": "John Doe",
      "email": "john@example.com",
      "detourTolerance": 5.0
    },
    "ride": {
      "id": 1,
      "status": "ONGOING",
      "totalDistance": 25.5,
      "totalDeviation": 3.2
    },
    "sourceLat": 28.7041,
    "sourceLng": 77.1025,
    "destLat": 28.5562,
    "destLng": 77.1000,
    "requestedSeats": 2,
    "requestedLuggage": 1,
    "price": 450.0,
    "status": "CONFIRMED"
  }
]
```

---

### Create Booking

Create a new booking request for a ride.

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

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `passengerId` | Long | Yes | ID of the passenger making the booking |
| `sourceLat` | Double | Yes | Latitude of pickup location |
| `sourceLng` | Double | Yes | Longitude of pickup location |
| `destLat` | Double | Yes | Latitude of destination |
| `destLng` | Double | Yes | Longitude of destination |
| `requestedSeats` | Integer | Yes | Number of seats required |
| `requestedLuggage` | Integer | Yes | Number of luggage items |

**Response:** `200 OK`

**Response Body:**
```json
{
  "id": 1,
  "passenger": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "detourTolerance": 5.0
  },
  "ride": null,
  "sourceLat": 28.7041,
  "sourceLng": 77.1025,
  "destLat": 28.5562,
  "destLng": 77.1000,
  "requestedSeats": 2,
  "requestedLuggage": 1,
  "price": 450.0,
  "status": "PENDING"
}
```

**Error Responses:**

- `500 Internal Server Error` - If passenger with the given ID is not found
  ```json
  {
    "error": "Passenger not found"
  }
  ```

---

### Cancel Booking

Cancel an existing booking by ID.

**Endpoint:** `DELETE /api/bookings/{id}`

**Path Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `id` | Long | Yes | ID of the booking to cancel |

**Response:** `204 No Content`

**Example:**
```
DELETE /api/bookings/1
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

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `driverName` | String | Yes | Name of the cab driver |
| `licensePlate` | String | Yes | License plate number |
| `totalSeats` | Integer | Yes | Total number of seats in the cab |
| `luggageCapacity` | Integer | Yes | Maximum luggage capacity |

**Note:** The `status` field is automatically set to `AVAILABLE` when registering a new cab.

**Response:** `200 OK`

**Response Body:**
```json
{
  "id": 1,
  "driverName": "John Smith",
  "licensePlate": "ABC-1234",
  "totalSeats": 4,
  "luggageCapacity": 3,
  "status": "AVAILABLE",
  "version": 0
}
```

---

### Get All Cabs

Retrieve a list of all registered cabs.

**Endpoint:** `GET /api/cabs`

**Response:** `200 OK`

**Response Body:**
```json
[
  {
    "id": 1,
    "driverName": "John Smith",
    "licensePlate": "ABC-1234",
    "totalSeats": 4,
    "luggageCapacity": 3,
    "status": "AVAILABLE",
    "version": 0
  },
  {
    "id": 2,
    "driverName": "Jane Doe",
    "licensePlate": "XYZ-5678",
    "totalSeats": 6,
    "luggageCapacity": 5,
    "status": "BUSY",
    "version": 1
  }
]
```

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
      "driverName": "John Smith",
      "licensePlate": "ABC-1234",
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
        "price": 450.0,
        "status": "CONFIRMED"
      }
    ],
    "status": "ONGOING",
    "totalDistance": 25.5,
    "totalDeviation": 3.2
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

**Response:** `200 OK`

**Response Body:**
```json
{
  "id": 1,
  "cab": {
    "id": 1,
    "driverName": "John Smith",
    "licensePlate": "ABC-1234",
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
      "price": 450.0,
      "status": "CONFIRMED"
    }
  ],
  "status": "ONGOING",
  "totalDistance": 25.5,
  "totalDeviation": 3.2
}
```

**Error Responses:**

- `404 Not Found` - If ride with the given ID does not exist

---

## Data Models

### Booking

Represents a passenger's booking request.

| Field | Type | Description |
|-------|------|-------------|
| `id` | Long | Unique identifier (auto-generated) |
| `passenger` | Passenger | Reference to the passenger making the booking |
| `ride` | Ride | Reference to the assigned ride (null if not yet assigned) |
| `sourceLat` | Double | Latitude of pickup location |
| `sourceLng` | Double | Longitude of pickup location |
| `destLat` | Double | Latitude of destination |
| `destLng` | Double | Longitude of destination |
| `requestedSeats` | Integer | Number of seats requested |
| `requestedLuggage` | Integer | Number of luggage items |
| `price` | Double | Calculated price for the booking |
| `status` | BookingStatus | Current status of the booking |

### BookingStatus Enum

Possible values:
- `PENDING` - Booking is pending assignment to a ride
- `CONFIRMED` - Booking has been confirmed and assigned to a ride
- `CANCELLED` - Booking has been cancelled
- `COMPLETED` - Booking has been completed

---

### Cab

Represents a registered cab/vehicle.

| Field | Type | Description |
|-------|------|-------------|
| `id` | Long | Unique identifier (auto-generated) |
| `driverName` | String | Name of the cab driver |
| `licensePlate` | String | License plate number |
| `totalSeats` | Integer | Total number of seats available |
| `luggageCapacity` | Integer | Maximum luggage capacity |
| `status` | CabStatus | Current status of the cab |
| `version` | Long | Version number for optimistic locking |

### CabStatus Enum

Possible values:
- `AVAILABLE` - Cab is available for new rides
- `BUSY` - Cab is currently on a ride
- `OFFLINE` - Cab is offline/unavailable

---

### Ride

Represents a ride that may contain multiple bookings (ride pooling).

| Field | Type | Description |
|-------|------|-------------|
| `id` | Long | Unique identifier (auto-generated) |
| `cab` | Cab | Reference to the cab assigned to this ride |
| `bookings` | List<Booking> | List of bookings assigned to this ride |
| `status` | RideStatus | Current status of the ride |
| `totalDistance` | Double | Total distance of the ride |
| `totalDeviation` | Double | Total deviation from direct routes |

### RideStatus Enum

Possible values:
- `CREATED` - Ride has been created but not started
- `ONGOING` - Ride is currently in progress
- `COMPLETED` - Ride has been completed
- `CANCELLED` - Ride has been cancelled

---

### Passenger

Represents a passenger/user of the system.

| Field | Type | Description |
|-------|------|-------------|
| `id` | Long | Unique identifier (auto-generated) |
| `name` | String | Name of the passenger |
| `email` | String | Email address of the passenger |
| `detourTolerance` | Double | Maximum detour tolerance in kilometers |

---

### BookingRequest (DTO)

Data Transfer Object for creating bookings.

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `passengerId` | Long | Yes | ID of the passenger making the booking |
| `sourceLat` | Double | Yes | Latitude of pickup location |
| `sourceLng` | Double | Yes | Longitude of pickup location |
| `destLat` | Double | Yes | Latitude of destination |
| `destLng` | Double | Yes | Longitude of destination |
| `requestedSeats` | Integer | Yes | Number of seats required |
| `requestedLuggage` | Integer | Yes | Number of luggage items |

---

## Status Codes

The API uses standard HTTP status codes:

| Code | Description |
|------|-------------|
| `200 OK` | Request successful |
| `204 No Content` | Request successful, no content to return |
| `404 Not Found` | Resource not found |
| `500 Internal Server Error` | Server error occurred |

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
- SpringDoc OpenAPI is included in the project dependencies, so Swagger UI may be available at `/swagger-ui.html` (if configured)

---

## Additional Resources

- **Spring Boot Documentation:** https://spring.io/projects/spring-boot
- **H2 Database:** https://www.h2database.com/html/main.html

