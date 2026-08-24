package com.airportpooling.ridepooling.dto;

import com.airportpooling.ridepooling.model.Cab;
import com.airportpooling.ridepooling.model.Ride;
import com.airportpooling.ridepooling.model.RideStatus;

import java.util.List;

/**
 * Public ride response. Embeds cab (safe, no back-references) and flat
 * BookingSummary objects instead of full Booking entities, breaking the
 * infinite Ride &lt;-&gt; Booking JSON serialization cycle.
 */
public record RideResponse(
        Long id,
        Cab cab,
        List<BookingSummary> bookings,
        RideStatus status,
        Double totalDistance,
        Double totalDeviation) {

    public static RideResponse from(Ride ride) {
        return new RideResponse(
                ride.getId(),
                ride.getCab(),
                ride.getBookings().stream().map(BookingSummary::from).toList(),
                ride.getStatus(),
                ride.getTotalDistance(),
                ride.getTotalDeviation());
    }
}
