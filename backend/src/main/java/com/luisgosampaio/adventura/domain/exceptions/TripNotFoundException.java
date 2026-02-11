package com.luisgosampaio.adventura.domain.exceptions;

public class TripNotFoundException extends RuntimeException {
    public TripNotFoundException(Long id) {
        super("The Trip with id: " + id + " wasn't found in our records");
    }
}
