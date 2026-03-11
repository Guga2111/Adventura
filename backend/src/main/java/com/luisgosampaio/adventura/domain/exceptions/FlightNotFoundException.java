package com.luisgosampaio.adventura.domain.exceptions;

public class FlightNotFoundException extends RuntimeException {
    public FlightNotFoundException(Long id) {
        super("The Flight with id: " + id + " wasn't found in our records");
    }
}
