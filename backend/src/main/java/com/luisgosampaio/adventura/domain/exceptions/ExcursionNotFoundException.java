package com.luisgosampaio.adventura.domain.exceptions;

public class ExcursionNotFoundException extends RuntimeException {
    public ExcursionNotFoundException(Long id) {
        super("The Excursion with id: " + id + " wasn't found in our records");
    }
}
