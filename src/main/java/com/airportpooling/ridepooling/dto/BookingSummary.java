package com.airportpooling.ridepooling.dto;

import com.airportpooling.ridepooling.model.Booking;
import com.airportpooling.ridepooling.model.BookingStatus;

/**
 * Flattened booking view embedded inside ride responses.
 * Excludes the ride and passenger back-references to break the
 * infinite Ride &lt;-&gt; Booking JSON serialization cycle.
 */
public record BookingSummary(
        Long id,
        Double sourceLat,
        Double sourceLng,
        Double destLat,
        Double destLng,
        Integer requestedSeats,
        Integer requestedLuggage,
        Double price,
        BookingStatus status) {

    public static BookingSummary from(Booking booking) {
        return new BookingSummary(
                booking.getId(),
                booking.getSourceLat(),
                booking.getSourceLng(),
                booking.getDestLat(),
                booking.getDestLng(),
                booking.getRequestedSeats(),
                booking.getRequestedLuggage(),
                booking.getPrice(),
                booking.getStatus());
    }
}
