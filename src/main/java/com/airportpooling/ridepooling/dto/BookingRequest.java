package com.airportpooling.ridepooling.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class BookingRequest {

    @NotNull(message = "passengerId is required")
    @Positive(message = "passengerId must be positive")
    private Long passengerId;

    @NotNull(message = "sourceLat is required")
    @DecimalMin(value = "-90.0", message = "sourceLat must be >= -90")
    @DecimalMax(value = "90.0", message = "sourceLat must be <= 90")
    private Double sourceLat;

    @NotNull(message = "sourceLng is required")
    @DecimalMin(value = "-180.0", message = "sourceLng must be >= -180")
    @DecimalMax(value = "180.0", message = "sourceLng must be <= 180")
    private Double sourceLng;

    @NotNull(message = "destLat is required")
    @DecimalMin(value = "-90.0", message = "destLat must be >= -90")
    @DecimalMax(value = "90.0", message = "destLat must be <= 90")
    private Double destLat;

    @NotNull(message = "destLng is required")
    @DecimalMin(value = "-180.0", message = "destLng must be >= -180")
    @DecimalMax(value = "180.0", message = "destLng must be <= 180")
    private Double destLng;

    @NotNull(message = "requestedSeats is required")
    @Min(value = 1, message = "requestedSeats must be at least 1")
    private Integer requestedSeats;

    @NotNull(message = "requestedLuggage is required")
    @Min(value = 0, message = "requestedLuggage must be at least 0")
    private Integer requestedLuggage;

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public Double getSourceLat() {
        return sourceLat;
    }

    public void setSourceLat(Double sourceLat) {
        this.sourceLat = sourceLat;
    }

    public Double getSourceLng() {
        return sourceLng;
    }

    public void setSourceLng(Double sourceLng) {
        this.sourceLng = sourceLng;
    }

    public Double getDestLat() {
        return destLat;
    }

    public void setDestLat(Double destLat) {
        this.destLat = destLat;
    }

    public Double getDestLng() {
        return destLng;
    }

    public void setDestLng(Double destLng) {
        this.destLng = destLng;
    }

    public Integer getRequestedSeats() {
        return requestedSeats;
    }

    public void setRequestedSeats(Integer requestedSeats) {
        this.requestedSeats = requestedSeats;
    }

    public Integer getRequestedLuggage() {
        return requestedLuggage;
    }

    public void setRequestedLuggage(Integer requestedLuggage) {
        this.requestedLuggage = requestedLuggage;
    }
}
