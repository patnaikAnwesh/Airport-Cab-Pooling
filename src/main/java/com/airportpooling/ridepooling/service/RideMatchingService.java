package com.airportpooling.ridepooling.service;

import com.airportpooling.ridepooling.model.*;
import com.airportpooling.ridepooling.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RideMatchingService {

    @Autowired
    private RideRepository rideRepository;

    public Optional<Ride> findBestRide(Booking request) {
        List<Ride> activeRides = rideRepository.findByStatus(RideStatus.CREATED);

        return activeRides.stream()
                .filter(ride -> canAccommodate(ride, request))
                .min((r1, r2) -> Double.compare(calculateDeviation(r1, request), calculateDeviation(r2, request)));
    }

    private boolean canAccommodate(Ride ride, Booking request) {
        Cab cab = ride.getCab();
        int occupiedSeats = ride.getBookings().stream().mapToInt(Booking::getRequestedSeats).sum();
        int occupiedLuggage = ride.getBookings().stream().mapToInt(Booking::getRequestedLuggage).sum();

        boolean fits = (occupiedSeats + request.getRequestedSeats() <= cab.getTotalSeats()) &&
                (occupiedLuggage + request.getRequestedLuggage() <= cab.getLuggageCapacity());

        if (!fits)
            return false;

        // Simplified detour tolerance check: deviation < passenger tolerance
        double deviation = calculateDeviation(ride, request);
        return deviation <= 15.0; // Hardcoded 15 min tolerance for now or use passenger preference
    }

    private double calculateDeviation(Ride ride, Booking request) {
        // Simplified distance-based deviation calculation
        // In a real system, use Google Maps API or OSRM
        return Math.sqrt(Math.pow(ride.getBookings().get(0).getSourceLat() - request.getSourceLat(), 2) +
                Math.pow(ride.getBookings().get(0).getSourceLng() - request.getSourceLng(), 2)) * 111; // Approx km
    }
}
