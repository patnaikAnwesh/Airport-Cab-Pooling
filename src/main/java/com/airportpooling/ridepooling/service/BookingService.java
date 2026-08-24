package com.airportpooling.ridepooling.service;

import com.airportpooling.ridepooling.exception.NoCabsAvailableException;
import com.airportpooling.ridepooling.exception.NotFoundException;
import com.airportpooling.ridepooling.model.*;
import com.airportpooling.ridepooling.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private RideRepository rideRepository;
    @Autowired
    private CabRepository cabRepository;
    @Autowired
    private RideMatchingService matchingService;
    @Autowired
    private PricingService pricingService;

    @Transactional
    public Booking createBooking(Booking request) {
        Ride ride = matchingService.findBestRide(request)
                .map(existing -> joinRide(existing, request))
                .orElseGet(() -> createNewRide(request));

        double distanceKm = PricingService.haversineKm(
                request.getSourceLat(), request.getSourceLng(),
                request.getDestLat(), request.getDestLng());

        long activePassengers = ride.getBookings().stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                .count();
        double demandFactor = pricingService.demandFactor(
                cabRepository.countByStatus(CabStatus.AVAILABLE),
                cabRepository.count());

        request.setRide(ride);
        // +1 accounts for this new booking; a shared ride (>1 passenger) earns the pooling discount
        request.setPrice(pricingService.calculatePrice(distanceKm, (int) activePassengers + 1, demandFactor));
        request.setStatus(BookingStatus.CONFIRMED);
        ride.getBookings().add(request);

        ride.setTotalDistance(PricingService.round2(nvl(ride.getTotalDistance()) + distanceKm));
        rideRepository.save(ride);

        return bookingRepository.save(request);
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return; // already cancelled - idempotent
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        Ride ride = booking.getRide();
        if (ride == null) {
            return;
        }
        boolean allCancelled = ride.getBookings().stream()
                .allMatch(b -> b.getStatus() == BookingStatus.CANCELLED);
        if (allCancelled) {
            ride.setStatus(RideStatus.CANCELLED);
            Cab cab = ride.getCab();
            if (cab != null) {
                cab.setStatus(CabStatus.AVAILABLE);
                cabRepository.save(cab);
            }
            rideRepository.save(ride);
        }
    }

    private Ride joinRide(Ride ride, Booking request) {
        double deviation = matchingService.deviationBetween(ride, request);
        ride.setTotalDeviation(PricingService.round2(nvl(ride.getTotalDeviation()) + deviation));
        return ride;
    }

    private Ride createNewRide(Booking request) {
        Cab cab = cabRepository.findByStatus(CabStatus.AVAILABLE).stream().findFirst()
                .orElseThrow(() -> new NoCabsAvailableException(
                        "No cabs available and no existing ride can accommodate this booking"));

        cab.setStatus(CabStatus.BUSY);
        cabRepository.save(cab);

        Ride ride = new Ride();
        ride.setCab(cab);
        ride.setStatus(RideStatus.CREATED);
        ride.setTotalDistance(0.0);
        ride.setTotalDeviation(0.0);
        return rideRepository.save(ride);
    }

    private double nvl(Double value) {
        return value != null ? value : 0.0;
    }
}
