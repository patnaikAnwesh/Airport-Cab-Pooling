package com.airportpooling.ridepooling.service;

import com.airportpooling.ridepooling.model.*;
import com.airportpooling.ridepooling.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

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
        Optional<Ride> bestRide = matchingService.findBestRide(request);

        Ride ride;
        if (bestRide.isPresent()) {
            ride = bestRide.get();
        } else {
            // Create a new ride if no existing ride matches
            Cab cab = cabRepository.findByStatus(CabStatus.AVAILABLE).stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("No cabs available"));

            cab.setStatus(CabStatus.BUSY);
            cabRepository.save(cab);

            ride = new Ride();
            ride.setCab(cab);
            ride.setStatus(RideStatus.CREATED);
            ride = rideRepository.save(ride);
        }

        request.setRide(ride);
        request.setPrice(pricingService.calculatePrice(10.0, ride.getBookings().size() + 1, 1.2));
        request.setStatus(BookingStatus.CONFIRMED);

        return bookingRepository.save(request);
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        Ride ride = booking.getRide();
        if (ride.getBookings().stream().allMatch(b -> b.getStatus() == BookingStatus.CANCELLED)) {
            ride.setStatus(RideStatus.CANCELLED);
            ride.getCab().setStatus(CabStatus.AVAILABLE);
            rideRepository.save(ride);
            cabRepository.save(ride.getCab());
        }
    }
}
