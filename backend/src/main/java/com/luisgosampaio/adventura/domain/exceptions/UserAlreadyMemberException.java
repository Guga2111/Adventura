package com.luisgosampaio.adventura.domain.exceptions;

public class UserAlreadyMemberException extends RuntimeException {
    public UserAlreadyMemberException(Long groupId, Long userId) {
        super("User with id: " + userId + " is already a member of group: " + groupId);
    }
}
