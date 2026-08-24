package com.airportpooling.ridepooling.repository;

import com.airportpooling.ridepooling.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
