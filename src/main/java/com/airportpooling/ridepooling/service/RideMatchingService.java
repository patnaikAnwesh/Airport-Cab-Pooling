package com.airportpooling.ridepooling.service;

import com.airportpooling.ridepooling.model.*;
import com.airportpooling.ridepooling.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Greedy ride matching over all CREATED rides:
 * - capacity check counts only non-cancelled bookings, so cancellations free seats for reuse
 * - detour tolerance comes from the requesting passenger's preference
 */
@Service
public class RideMatchingService {

    /** Fallback tolerance (km) when a passenger has no explicit preference. */
    private static final double DEFAULT_TOLERANCE_KM = 15.0;

    @Autowired
    private RideRepository rideRepository;

    /**
     * Finds the CREATED ride that can accommodate the request with minimal deviation.
     * Complexity: O(N) rides with an O(B) scan of each ride's bookings.
     */
    public Optional<Ride> findBestRide(Booking request) {
        List<Ride> activeRides = rideRepository.findByStatus(RideStatus.CREATED);

        return activeRides.stream()
                .filter(ride -> canAccommodate(ride, request))
                .min(Comparator.comparingDouble(ride -> deviationBetween(ride, request)));
    }

    private boolean canAccommodate(Ride ride, Booking request) {
        Cab cab = ride.getCab();
        if (cab == null || cab.getTotalSeats() == null || cab.getLuggageCapacity() == null) {
            return false;
        }
        List<Booking> activeBookings = activeBookingsOf(ride);
        int occupiedSeats = activeBookings.stream().mapToInt(b -> nvlSeats(b.getRequestedSeats())).sum();
        int occupiedLuggage = activeBookings.stream().mapToInt(b -> nvlSeats(b.getRequestedLuggage())).sum();

        boolean fits = (occupiedSeats + nvlSeats(request.getRequestedSeats()) <= cab.getTotalSeats()) &&
                (occupiedLuggage + nvlSeats(request.getRequestedLuggage()) <= cab.getLuggageCapacity());

        if (!fits) {
            return false;
        }

        double tolerance = (request.getPassenger() != null && request.getPassenger().getDetourTolerance() != null)
                ? request.getPassenger().getDetourTolerance()
                : DEFAULT_TOLERANCE_KM;
        return deviationBetween(ride, request) <= tolerance;
    }

    /**
     * Detour estimate in km: haversine distance between the ride's anchor pickup
     * (first active booking) and the requested pickup. Destinations are not yet
     * considered; this keeps matching deterministic without external routing APIs.
     */
    public double deviationBetween(Ride ride, Booking request) {
        List<Booking> activeBookings = activeBookingsOf(ride);
        if (activeBookings.isEmpty()) {
            return 0.0;
        }
        Booking anchor = activeBookings.get(0);
        return PricingService.haversineKm(
                anchor.getSourceLat(), anchor.getSourceLng(),
                request.getSourceLat(), request.getSourceLng());
    }

    private List<Booking> activeBookingsOf(Ride ride) {
        if (ride.getBookings() == null) {
            return List.of();
        }
        return ride.getBookings().stream()
                .filter(b -> b.getStatus() != null && b.getStatus() != BookingStatus.CANCELLED)
                .toList();
    }

    private int nvlSeats(Integer value) {
        return value != null ? value : 0;
    }
}
