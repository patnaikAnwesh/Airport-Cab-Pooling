package com.airportpooling.ridepooling.dto;

import com.airportpooling.ridepooling.model.Ride;
import com.airportpooling.ridepooling.model.RideStatus;

/**
 * Flattened ride view embedded inside booking responses.
 * Deliberately excludes the bookings collection to break the
 * Ride <-> Booking JSON serialization cycle.
 */
public record RideSummary(Long id, RideStatus status, Double totalDistance, Double totalDeviation) {

    public static RideSummary from(Ride ride) {
        if (ride == null) {
            return null;
        }
        return new RideSummary(ride.getId(), ride.getStatus(), ride.getTotalDistance(), ride.getTotalDeviation());
    }
}
