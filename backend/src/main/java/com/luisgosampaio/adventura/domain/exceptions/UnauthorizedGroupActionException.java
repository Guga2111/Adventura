package com.luisgosampaio.adventura.domain.exceptions;

public class UnauthorizedGroupActionException extends RuntimeException {
    public UnauthorizedGroupActionException(Long groupId, Long userId) {
        super("User with id: " + userId + " does not have ADMIN permission on group with id: " + groupId);
    }
}