package com.airportpooling.ridepooling.repository;

import com.airportpooling.ridepooling.model.Cab;
import com.airportpooling.ridepooling.model.CabStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CabRepository extends JpaRepository<Cab, Long> {
    List<Cab> findByStatus(CabStatus status);

    long countByStatus(CabStatus status);

    boolean existsByLicensePlateIgnoreCase(String licensePlate);
}
