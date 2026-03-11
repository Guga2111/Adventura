package com.luisgosampaio.adventura.domain.exceptions;

public class FlightPassengerNotFoundException extends RuntimeException {
    public FlightPassengerNotFoundException(Long id) {
        super("The Flight Passenger with id: " + id + " wasn't found in our records");
    }
}
