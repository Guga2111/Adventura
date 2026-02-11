package com.luisgosampaio.adventura.domain.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("The user with id: " + id + " wasn't found in our records");
    }

    public UserNotFoundException(String email) {
        super("The user with email: " + email + " wasn't found in our records");
    }
}
