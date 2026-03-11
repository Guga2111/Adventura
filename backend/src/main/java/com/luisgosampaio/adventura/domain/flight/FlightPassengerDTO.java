package com.luisgosampaio.adventura.domain.flight;

import java.math.BigDecimal;

public record FlightPassengerDTO(
        Long groupMemberId,
        String seatNumber,
        SeatClass seatClass,
        BigDecimal price,
        FlightPassengerStatus status
) {}
