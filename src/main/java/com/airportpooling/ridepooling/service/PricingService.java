package com.airportpooling.ridepooling.service;

import org.springframework.stereotype.Service;

@Service
public class PricingService {
    private static final double BASE_FARE = 50.0;
    private static final double RATE_PER_KM = 12.0;

    public double calculatePrice(double distance, int passengerCount, double demandFactor) {
        double poolingDiscount = (passengerCount > 1) ? 0.3 : 0.0;
        return (BASE_FARE + (distance * RATE_PER_KM)) * demandFactor * (1 - poolingDiscount);
    }
}
