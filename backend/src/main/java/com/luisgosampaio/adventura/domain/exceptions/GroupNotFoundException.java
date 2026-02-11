package com.luisgosampaio.adventura.domain.exceptions;

public class GroupNotFoundException extends RuntimeException {
    public GroupNotFoundException(Long id) {
        super("The group with id: " + id + " wasn't found in our records");
    }
}
