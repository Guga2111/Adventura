package com.luisgosampaio.adventura.domain.exceptions;

public class LastGroupMemberException extends RuntimeException {
    public LastGroupMemberException(Long groupId) {
        super("Cannot remove the last member of group with id: " + groupId);
    }
}