package com.airportpooling.ridepooling.dto;

import com.airportpooling.ridepooling.model.Booking;
import com.airportpooling.ridepooling.model.BookingStatus;
import com.airportpooling.ridepooling.model.Passenger;

/**
 * Public booking response. Embeds the passenger entity (no back-references, safe)
 * and a flattened RideSummary instead of the full Ride aggregate, which breaks
 * the infinite Ride &lt;-&gt; Booking recursion that previously crashed the GET endpoints.
 */
public record BookingResponse(
        Long id,
        Passenger passenger,
        RideSummary ride,
        Double sourceLat,
        Double sourceLng,
        Double destLat,
        Double destLng,
        Integer requestedSeats,
        Integer requestedLuggage,
        Double price,
        BookingStatus status) {

    public static BookingResponse from(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getPassenger(),
                RideSummary.from(booking.getRide()),
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
