package com.airportpooling.ridepooling.controller;

import com.airportpooling.ridepooling.dto.BookingRequest;
import com.airportpooling.ridepooling.model.Booking;
import com.airportpooling.ridepooling.model.Passenger;
import com.airportpooling.ridepooling.repository.PassengerRepository;
import com.airportpooling.ridepooling.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private com.airportpooling.ridepooling.repository.BookingRepository bookingRepository;

    @GetMapping
    public ResponseEntity<java.util.List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody BookingRequest request) {
        Passenger passenger = passengerRepository.findById(request.getPassengerId())
                .orElseThrow(() -> new RuntimeException("Passenger not found"));

        Booking booking = new Booking();
        booking.setPassenger(passenger);
        booking.setSourceLat(request.getSourceLat());
        booking.setSourceLng(request.getSourceLng());
        booking.setDestLat(request.getDestLat());
        booking.setDestLng(request.getDestLng());
        booking.setRequestedSeats(request.getRequestedSeats());
        booking.setRequestedLuggage(request.getRequestedLuggage());

        return ResponseEntity.ok(bookingService.createBooking(booking));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.noContent().build();
    }
}
