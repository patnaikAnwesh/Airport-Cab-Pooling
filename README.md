# Smart Airport Ride Pooling Backend

A Spring Boot-based backend system that groups passengers into shared cabs while optimizing routes and pricing.

## Tech Stack
- **Java 17**
- **Spring Boot 3.2.2**
- **Maven**
- **H2 (In-memory Database)**
- **Lombok**
- **SpringDoc OpenAPI (Swagger)**

## Features
- **Ride Matching Algorithm**: Groups passengers based on seat/luggage capacity and detour tolerance.
- **Dynamic Pricing**: Calculates fares based on distance, demand, and pooling discounts.
- **Concurrency Safety**: Uses `@Transactional` and Optimistic Locking (`@Version`) for seat allocation.
- **Real-time Cancellation**: Handles booking cancellations and frees up cab capacity.

## Setup & Run Instructions
1. Ensure you have **JDK 17** and **Maven** installed.
2. Clone the repository and navigate to the project directory.
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```
4. Access the API documentation (Swagger UI):
   [http://localhost:8080/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)
5. H2 Console: [http://localhost:8080/h2-console](http://localhost:8081/h2-console) (JDBC URL: `jdbc:h2:mem:testdb`, User: `sa`, Pass: `password`)

## DSA Approach: Ride Matching
- **Complexity**: $O(N \cdot K)$ where $N$ is candidate cabs and $K$ is average passengers per cab.
- **Logic**: Greedy matching that minimizes travel deviation while respecting all participant constraints.

## Dynamic Pricing Formula
`Price = (BaseFare + (Distance * Rate)) * DemandFactor * (1 - PoolingDiscount)`
- Default Pooling Discount: 30% for pooled rides.
