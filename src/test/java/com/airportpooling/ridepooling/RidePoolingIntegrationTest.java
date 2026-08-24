package com.airportpooling.ridepooling;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Black-box integration tests over the HTTP API.
 * Tests are ordered and geographically isolated (Delhi/Mumbai/Kolkata/Chennai clusters)
 * so ride matching cannot leak bookings across test scenarios.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RidePoolingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static Long t6BookingXId;
    private static Long t6BookingYId;
    private static Long t6BookingZId;

    @Test
    @Order(1)
    void seededCabsAreAvailable() throws Exception {
        mockMvc.perform(get("/api/cabs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.licensePlate == 'XYZ-123')].status").value("AVAILABLE"))
                .andExpect(jsonPath("$[?(@.licensePlate == 'ABC-789')].status").value("AVAILABLE"));
    }

    @Test
    @Order(2)
    void cabRegistrationValidationAndDuplicates() throws Exception {
        String body = """
                {"driverName":"Ravi","licensePlate":"TST-T2-A","totalSeats":4,"luggageCapacity":2}
                """;
        mockMvc.perform(post("/api/cabs").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AVAILABLE"))
                .andExpect(jsonPath("$.version").isNumber());

        mockMvc.perform(post("/api/cabs").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").isNotEmpty());

        mockMvc.perform(post("/api/cabs").contentType(MediaType.APPLICATION_JSON).content("""
                        {"driverName":"Bad","licensePlate":"TST-T2-BAD","totalSeats":0,"luggageCapacity":1}
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(3)
    void createBookingAssignsRideAndComputesPrice() throws Exception {
        mockMvc.perform(post("/api/bookings").contentType(MediaType.APPLICATION_JSON).content("""
                        {"passengerId":1,"sourceLat":28.7041,"sourceLng":77.1025,
                         "destLat":28.5562,"destLng":77.1000,"requestedSeats":2,"requestedLuggage":0}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.passenger.name").value("Alice"))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.price").isNumber())
                .andExpect(jsonPath("$.ride.id").isNumber())
                .andExpect(jsonPath("$.ride.status").value("CREATED"));
    }

    @Test
    @Order(4)
    void nearbyBookingsPoolOntoSameRideWithDiscount() throws Exception {
        MvcResult first = mockMvc.perform(post("/api/bookings").contentType(MediaType.APPLICATION_JSON).content("""
                        {"passengerId":2,"sourceLat":19.0760,"sourceLng":72.8777,
                         "destLat":19.0100,"destLng":72.8600,"requestedSeats":1,"requestedLuggage":0}
                        """))
                .andExpect(status().isOk())
                .andReturn();
        MvcResult second = mockMvc.perform(post("/api/bookings").contentType(MediaType.APPLICATION_JSON).content("""
                        {"passengerId":2,"sourceLat":19.0764,"sourceLng":72.8781,
                         "destLat":19.0105,"destLng":72.8605,"requestedSeats":1,"requestedLuggage":0}
                        """))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode firstBody = objectMapper.readTree(first.getResponse().getContentAsString());
        JsonNode secondBody = objectMapper.readTree(second.getResponse().getContentAsString());

        assertThat(firstBody.get("ride").get("id").asLong())
                .isEqualTo(secondBody.get("ride").get("id").asLong());
        assertThat(secondBody.get("price").asDouble())
                .isLessThan(firstBody.get("price").asDouble());
    }

    @Test
    @Order(5)
    void bookingValidationErrors() throws Exception {
        mockMvc.perform(post("/api/bookings").contentType(MediaType.APPLICATION_JSON).content("""
                        {"passengerId":1,"sourceLat":12.9716,"sourceLng":77.5946,
                         "destLat":12.9100,"destLng":77.5800,"requestedLuggage":0}
                        """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/bookings").contentType(MediaType.APPLICATION_JSON).content("""
                        {"passengerId":1,"sourceLat":12.9716,"sourceLng":77.5946,
                         "destLat":12.9100,"destLng":77.5800,"requestedSeats":0,"requestedLuggage":0}
                        """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/bookings").contentType(MediaType.APPLICATION_JSON).content("""
                        {"passengerId":9999,"sourceLat":12.9716,"sourceLng":77.5946,
                         "destLat":12.9100,"destLng":77.5800,"requestedSeats":1,"requestedLuggage":0}
                        """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Passenger not found"));
    }

    @Test
    @Order(6)
    void cancellationFreesCapacityForReuse() throws Exception {
        mockMvc.perform(post("/api/cabs").contentType(MediaType.APPLICATION_JSON).content("""
                        {"driverName":"Kolkata Cabbie","licensePlate":"TST-T6-A","totalSeats":4,"luggageCapacity":3}
                        """))
                .andExpect(status().isOk());

        // Consume the last spare cab so booking Z below can ONLY succeed by pooling
        // onto X/Y's ride - this is what makes the freed-capacity regression detectable.
        mockMvc.perform(post("/api/bookings").contentType(MediaType.APPLICATION_JSON).content("""
                        {"passengerId":2,"sourceLat":12.9716,"sourceLng":77.5946,
                         "destLat":12.9100,"destLng":77.5800,"requestedSeats":1,"requestedLuggage":0}
                        """))
                .andExpect(status().isOk());

        MvcResult x = mockMvc.perform(post("/api/bookings").contentType(MediaType.APPLICATION_JSON).content("""
                        {"passengerId":1,"sourceLat":22.5726,"sourceLng":88.3639,
                         "destLat":22.5200,"destLng":88.3500,"requestedSeats":2,"requestedLuggage":0}
                        """))
                .andExpect(status().isOk())
                .andReturn();
        MvcResult y = mockMvc.perform(post("/api/bookings").contentType(MediaType.APPLICATION_JSON).content("""
                        {"passengerId":2,"sourceLat":22.5730,"sourceLng":88.3642,
                         "destLat":22.5210,"destLng":88.3510,"requestedSeats":2,"requestedLuggage":0}
                        """))
                .andExpect(status().isOk())
                .andReturn();
        t6BookingXId = objectMapper.readTree(x.getResponse().getContentAsString()).get("id").asLong();
        t6BookingYId = objectMapper.readTree(y.getResponse().getContentAsString()).get("id").asLong();
        assertThat(t6BookingYId).isNotNull();

        mockMvc.perform(delete("/api/bookings/" + t6BookingXId))
                .andExpect(status().isNoContent());

        MvcResult z = mockMvc.perform(post("/api/bookings").contentType(MediaType.APPLICATION_JSON).content("""
                        {"passengerId":1,"sourceLat":22.5731,"sourceLng":88.3640,
                         "destLat":22.5215,"destLng":88.3515,"requestedSeats":2,"requestedLuggage":0}
                        """))
                .andExpect(status().isOk())
                .andReturn();
        t6BookingZId = objectMapper.readTree(z.getResponse().getContentAsString()).get("id").asLong();
    }

    @Test
    @Order(7)
    void fullCancellationReleasesCab() throws Exception {
        assertThat(t6BookingYId).isNotNull();
        assertThat(t6BookingZId).isNotNull();

        mockMvc.perform(get("/api/bookings")).andExpect(status().isOk());

        MvcResult rides = mockMvc.perform(get("/api/rides")).andExpect(status().isOk()).andReturn();
        JsonNode ridesNode = objectMapper.readTree(rides.getResponse().getContentAsString());
        String plate = findPlateOfRideContainingBooking(ridesNode, t6BookingYId);
        assertThat(plate).isNotBlank();

        mockMvc.perform(delete("/api/bookings/" + t6BookingYId)).andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/bookings/" + t6BookingZId)).andExpect(status().isNoContent());

        MvcResult cabs = mockMvc.perform(get("/api/cabs")).andExpect(status().isOk()).andReturn();
        JsonNode cabsNode = objectMapper.readTree(cabs.getResponse().getContentAsString());
        assertThat(statusOfPlate(cabsNode, plate)).isEqualTo("AVAILABLE");
    }

    @Test
    @Order(8)
    void getAllBookingsReturnsRecursionFreeJson() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/bookings")).andExpect(status().isOk()).andReturn();
        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(root.isArray()).isTrue();
        assertThat(root.size()).isGreaterThan(0);
        for (JsonNode booking : root) {
            assertThat(booking.has("passenger")).isTrue();
            if (!booking.get("ride").isNull()) {
                assertThat(booking.get("ride").has("bookings")).isFalse();
            }
        }
    }

    @Test
    @Order(9)
    void getAllRidesReturnsRecursionFreeJson() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/rides")).andExpect(status().isOk()).andReturn();
        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(root.isArray()).isTrue();
        assertThat(root.size()).isGreaterThan(0);
        for (JsonNode ride : root) {
            assertThat(ride.has("cab")).isTrue();
            for (JsonNode booking : ride.get("bookings")) {
                assertThat(booking.has("ride")).isFalse();
                assertThat(booking.has("passenger")).isFalse();
            }
        }
    }

    @Test
    @Order(10)
    void unknownResourceIdsReturn404() throws Exception {
        mockMvc.perform(get("/api/rides/99999")).andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/bookings/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Booking not found"));
    }

    @Test
    @Order(11)
    void unmatchedBookingFallsBackToNewRide() throws Exception {
        mockMvc.perform(post("/api/bookings").contentType(MediaType.APPLICATION_JSON).content("""
                        {"passengerId":1,"sourceLat":13.0827,"sourceLng":80.2707,
                         "destLat":13.0200,"destLng":80.2600,"requestedSeats":1,"requestedLuggage":0}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ride.id").isNumber())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    @Order(12)
    void openApiDocsAreExposed() throws Exception {
        mockMvc.perform(get("/v3/api-docs")).andExpect(status().isOk());
    }

    private String findPlateOfRideContainingBooking(JsonNode rides, long bookingId) {
        for (JsonNode ride : rides) {
            for (Iterator<JsonNode> it = ride.get("bookings").elements(); it.hasNext(); ) {
                if (it.next().get("id").asLong() == bookingId) {
                    return ride.get("cab").get("licensePlate").asText();
                }
            }
        }
        return "";
    }

    private String statusOfPlate(JsonNode cabs, String plate) {
        for (JsonNode cab : cabs) {
            if (plate.equals(cab.get("licensePlate").asText())) {
                return cab.get("status").asText();
            }
        }
        return "";
    }
}
