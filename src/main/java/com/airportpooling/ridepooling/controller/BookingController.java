package com.airportpooling.ridepooling.controller;

import com.airportpooling.ridepooling.dto.BookingRequest;
import com.airportpooling.ridepooling.dto.BookingResponse;
import com.airportpooling.ridepooling.exception.NotFoundException;
import com.airportpooling.ridepooling.model.Booking;
import com.airportpooling.ridepooling.model.Passenger;
import com.airportpooling.ridepooling.repository.PassengerRepository;
import com.airportpooling.ridepooling.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private static final int MAX_BOOKING_RETRIES = 3;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private com.airportpooling.ridepooling.repository.BookingRepository bookingRepository;

    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        List<BookingResponse> bookings = bookingRepository.findAll().stream()
                .map(BookingResponse::from)
                .toList();
        return ResponseEntity.ok(bookings);
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        Passenger passenger = passengerRepository.findById(request.getPassengerId())
                .orElseThrow(() -> new NotFoundException("Passenger not found"));

        OptimisticLockingFailureException lastConflict = null;
        for (int attempt = 1; attempt <= MAX_BOOKING_RETRIES; attempt++) {
            try {
                Booking booking = toBooking(passenger, request);
                return ResponseEntity.ok(BookingResponse.from(bookingService.createBooking(booking)));
            } catch (OptimisticLockingFailureException e) {
                lastConflict = e;
            }
        }
        throw lastConflict;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.noContent().build();
    }

    private Booking toBooking(Passenger passenger, BookingRequest request) {
        Booking booking = new Booking();
        booking.setPassenger(passenger);
        booking.setSourceLat(request.getSourceLat());
        booking.setSourceLng(request.getSourceLng());
        booking.setDestLat(request.getDestLat());
        booking.setDestLng(request.getDestLng());
        booking.setRequestedSeats(request.getRequestedSeats());
        booking.setRequestedLuggage(request.getRequestedLuggage());
        return booking;
    }
}
