package com.luisgosampaio.adventura.domain.exceptions.config;

import com.luisgosampaio.adventura.domain.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GroupNotFoundException.class)
    public ResponseEntity<Object> handleGroupNotFoundException (GroupNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(Arrays.asList(e.getLocalizedMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TripNotFoundException.class)
    public ResponseEntity<Object> handleTripNotFoundException (TripNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(Arrays.asList(e.getLocalizedMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException (UserNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(Arrays.asList(e.getLocalizedMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<Object> handleMemberNotFoundException (MemberNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(Arrays.asList(e.getLocalizedMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyMemberException.class)
    public ResponseEntity<Object> handleUserAlreadyMemberException (UserAlreadyMemberException e) {
        ErrorResponse errorResponse = new ErrorResponse(Arrays.asList(e.getLocalizedMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Object> handleEmailAlreadyExistsException (EmailAlreadyExistsException e) {
        ErrorResponse errorResponse = new ErrorResponse(Arrays.asList(e.getLocalizedMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
}
