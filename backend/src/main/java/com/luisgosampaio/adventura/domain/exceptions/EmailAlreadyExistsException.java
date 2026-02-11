package com.luisgosampaio.adventura.domain.exceptions;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("The email: " + email + " is already registered");
    }
}
