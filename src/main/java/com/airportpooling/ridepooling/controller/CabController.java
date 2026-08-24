package com.airportpooling.ridepooling.controller;

import com.airportpooling.ridepooling.exception.DuplicateResourceException;
import com.airportpooling.ridepooling.model.Cab;
import com.airportpooling.ridepooling.model.CabStatus;
import com.airportpooling.ridepooling.repository.CabRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cabs")
public class CabController {

    @Autowired
    private CabRepository cabRepository;

    @PostMapping
    public ResponseEntity<Cab> registerCab(@Valid @RequestBody Cab cab) {
        if (cabRepository.existsByLicensePlateIgnoreCase(cab.getLicensePlate())) {
            throw new DuplicateResourceException(
                    "Cab with license plate " + cab.getLicensePlate() + " already exists");
        }
        cab.setId(null);
        cab.setVersion(null);
        cab.setStatus(CabStatus.AVAILABLE);
        return ResponseEntity.ok(cabRepository.save(cab));
    }

    @GetMapping
    public ResponseEntity<List<Cab>> getAllCabs() {
        return ResponseEntity.ok(cabRepository.findAll());
    }
}
