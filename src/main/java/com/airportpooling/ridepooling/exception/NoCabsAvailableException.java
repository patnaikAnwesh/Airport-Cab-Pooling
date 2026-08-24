package com.airportpooling.ridepooling.exception;

public class NoCabsAvailableException extends RuntimeException {
    public NoCabsAvailableException(String message) {
        super(message);
    }
}
