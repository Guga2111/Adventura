package com.luisgosampaio.adventura.domain.flight;

import java.time.LocalDateTime;
import java.util.List;

public record FlightDTO(
        String flightNumber,
        String airline,
        String originAirport,
        String destinationAirport,
        LocalDateTime departureLocalTime,
        String departureTimezone,
        LocalDateTime arrivalLocalTime,
        String arrivalTimezone,
        String bookingReference,
        String notes,
        List<FlightPassengerDTO> passengers
) {}
