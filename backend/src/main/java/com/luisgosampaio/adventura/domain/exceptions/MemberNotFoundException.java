package com.luisgosampaio.adventura.domain.exceptions;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(Long groupId, Long userId) {
        super("Member with userId: " + userId + " wasn't found in group: " + groupId);
    }
}
