package com.airportpooling.ridepooling.controller;

import com.airportpooling.ridepooling.model.Ride;
import com.airportpooling.ridepooling.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    @Autowired
    private RideRepository rideRepository;

    @GetMapping
    public ResponseEntity<List<Ride>> getAllRides() {
        return ResponseEntity.ok(rideRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ride> getRideById(@PathVariable Long id) {
        return rideRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
