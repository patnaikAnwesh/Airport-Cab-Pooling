package com.airportpooling.ridepooling.service;

import org.springframework.stereotype.Service;

/**
 * Dynamic pricing: Price = (BaseFare + Distance * RatePerKm) * DemandFactor * (1 - PoolingDiscount)
 * - distance is the real haversine distance between pickup and destination
 * - DemandFactor rises with cab utilisation: 1.0 (all cabs idle) .. 1.5 (all cabs busy)
 * - PoolingDiscount is 30% whenever more than one passenger shares a ride
 */
@Service
public class PricingService {

    private static final double BASE_FARE = 50.0;
    private static final double RATE_PER_KM = 12.0;
    private static final double POOLING_DISCOUNT = 0.3;
    private static final double EARTH_RADIUS_KM = 6371.0;

    public double calculatePrice(double distanceKm, int passengerCount, double demandFactor) {
        double poolingDiscount = (passengerCount > 1) ? POOLING_DISCOUNT : 0.0;
        return round2((BASE_FARE + (distanceKm * RATE_PER_KM)) * Math.max(demandFactor, 1.0) * (1 - poolingDiscount));
    }

    /**
     * Demand multiplier from current fleet utilisation.
     *
     * @param availableCabs cabs currently AVAILABLE
     * @param totalCabs     total registered cabs
     * @return 1.0 when the fleet is idle, up to 1.5 when fully busy
     */
    public double demandFactor(long availableCabs, long totalCabs) {
        if (totalCabs <= 0) {
            return 1.0;
        }
        double busyRatio = (double) (totalCabs - availableCabs) / totalCabs;
        return round2(1.0 + (0.5 * busyRatio));
    }

    /**
     * Great-circle distance between two coordinates in kilometres (haversine formula).
     */
    public static double haversineKm(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.pow(Math.sin(dLng / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    public static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
