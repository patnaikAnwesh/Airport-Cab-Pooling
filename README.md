# Smart Airport Ride Pooling Backend

A Spring Boot-based backend system that groups passengers into shared cabs while optimizing routes and pricing.

## Tech Stack
- **Java 17**
- **Spring Boot 3.2.2**
- **Maven**
- **H2 (In-memory Database)**
- **SpringDoc OpenAPI (Swagger)**

## Features
- **Ride Matching Algorithm**: Greedy matching over all `CREATED` rides — checks seat/luggage capacity (counting only non-cancelled bookings) and pickup proximity against the requesting passenger's own detour tolerance.
- **Dynamic Pricing**: Real haversine distance between pickup and destination, a demand factor that scales with fleet utilisation (1.0 idle → 1.5 fully busy), and a 30% pooling discount for shared rides.
- **Concurrency Safety**: Uses `@Transactional`, Optimistic Locking (`@Version`) and automatic retry on optimistic-lock conflicts during booking creation.
- **Real-time Cancellation**: Cancelling a booking frees its seats/luggage for reuse by later bookings; when every booking on a ride is cancelled, the ride is closed and the cab returns to `AVAILABLE`.
- **Input Validation & Error Handling**: Bean Validation on all request payloads plus a global exception handler returning consistent `{ "error": ... }` bodies with correct HTTP codes (400/404/409/500).

## Setup & Run Instructions
1. Ensure you have **JDK 17** and **Maven** installed.
2. Clone the repository and navigate to the project directory.
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```
4. Access the API documentation (Swagger UI):
   [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)
5. H2 Console: [http://localhost:8081/h2-console](http://localhost:8081/h2-console) (JDBC URL: `jdbc:h2:mem:testdb`, User: `sa`, Pass: `password`)

## DSA Approach: Ride Matching
- **Complexity**: O(N · B) where N is the number of `CREATED` rides and B is the average bookings per ride.
- **Logic**: Greedy matching — filter rides that fit capacity (ignoring cancelled bookings) and whose anchor pickup is within the passenger's detour tolerance, then pick the ride with minimum haversine deviation from the requested pickup.

## Dynamic Pricing Formula
```
Price = (BaseFare + (HaversineDistance * RatePerKm)) * DemandFactor * (1 - PoolingDiscount)
```
- BaseFare = 50, RatePerKm = 12
- DemandFactor = 1.0 + 0.5 × (busy cabs / total cabs), i.e. between 1.0 and 1.5
- PoolingDiscount = 30% whenever more than one passenger shares the ride
