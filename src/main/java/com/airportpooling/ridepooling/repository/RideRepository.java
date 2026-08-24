package com.airportpooling.ridepooling.repository;

import com.airportpooling.ridepooling.model.Ride;
import com.airportpooling.ridepooling.model.RideStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RideRepository extends JpaRepository<Ride, Long> {
    List<Ride> findByStatus(RideStatus status);
}
