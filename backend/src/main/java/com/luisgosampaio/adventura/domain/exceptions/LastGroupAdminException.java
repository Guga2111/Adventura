package com.luisgosampaio.adventura.domain.exceptions;

public class LastGroupAdminException extends RuntimeException {
    public LastGroupAdminException(Long groupId) {
        super("Cannot remove the last admin of group with id: " + groupId + ". Promote another member to admin first.");
    }
}