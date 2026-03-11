package com.luisgosampaio.adventura.domain.flight;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FlightPassengerRepository extends JpaRepository<FlightPassenger, Long> {

    Optional<FlightPassenger> findByIdAndFlightId(Long id, Long flightId);

    boolean existsByFlightIdAndGroupMemberId(Long flightId, Long groupMemberId);
}
