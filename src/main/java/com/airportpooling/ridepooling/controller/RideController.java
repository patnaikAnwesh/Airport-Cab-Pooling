package com.airportpooling.ridepooling.controller;

import com.airportpooling.ridepooling.dto.RideResponse;
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
    public ResponseEntity<List<RideResponse>> getAllRides() {
        List<RideResponse> rides = rideRepository.findAll().stream()
                .map(RideResponse::from)
                .toList();
        return ResponseEntity.ok(rides);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RideResponse> getRideById(@PathVariable Long id) {
        return rideRepository.findById(id)
                .map(RideResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
