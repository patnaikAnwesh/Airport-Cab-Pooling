package com.airportpooling.ridepooling.repository;

import com.airportpooling.ridepooling.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {
}
