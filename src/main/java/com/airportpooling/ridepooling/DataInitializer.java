package com.airportpooling.ridepooling;

import com.airportpooling.ridepooling.model.*;
import com.airportpooling.ridepooling.repository.CabRepository;
import com.airportpooling.ridepooling.repository.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private PassengerRepository passengerRepository;
    @Autowired
    private CabRepository cabRepository;

    @Override
    public void run(String... args) throws Exception {
        // Seed Passengers
        passengerRepository.save(new Passenger(null, "Alice", "alice@example.com", 15.0));
        passengerRepository.save(new Passenger(null, "Bob", "bob@example.com", 10.0));

        // Seed Cabs
        cabRepository.save(new Cab(null, "John Driver", "XYZ-123", 4, 3, CabStatus.AVAILABLE, 0L));
        cabRepository.save(new Cab(null, "Jane Driver", "ABC-789", 6, 5, CabStatus.AVAILABLE, 0L));
    }
}
