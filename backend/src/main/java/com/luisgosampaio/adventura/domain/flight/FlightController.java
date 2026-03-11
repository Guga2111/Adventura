package com.luisgosampaio.adventura.domain.flight;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @GetMapping("/trip/{tripId}/flights")
    public ResponseEntity<List<Flight>> getFlights(@PathVariable Long tripId) {
        return ResponseEntity.ok(flightService.getFlights(tripId));
    }

    @PostMapping("/trip/{tripId}/flights")
    public ResponseEntity<Flight> createFlight(@PathVariable Long tripId,
                                               @RequestBody FlightDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(flightService.createFlight(tripId, dto));
    }

    @PutMapping("/trip/{tripId}/flights/{flightId}")
    public ResponseEntity<Flight> updateFlight(@PathVariable Long tripId,
                                               @PathVariable Long flightId,
                                               @RequestBody FlightDTO dto) {
        return ResponseEntity.ok(flightService.updateFlight(tripId, flightId, dto));
    }

    @DeleteMapping("/trip/{tripId}/flights/{flightId}")
    public ResponseEntity<Void> deleteFlight(@PathVariable Long tripId,
                                             @PathVariable Long flightId) {
        flightService.deleteFlight(tripId, flightId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/trip/{tripId}/flights/{flightId}/passengers")
    public ResponseEntity<FlightPassenger> joinFlight(@PathVariable Long tripId,
                                                      @PathVariable Long flightId,
                                                      @RequestBody FlightPassengerDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(flightService.joinFlight(tripId, flightId, dto));
    }

    @PutMapping("/trip/{tripId}/flights/{flightId}/passengers/{passengerId}")
    public ResponseEntity<FlightPassenger> updatePassenger(@PathVariable Long tripId,
                                                           @PathVariable Long flightId,
                                                           @PathVariable Long passengerId,
                                                           @RequestBody FlightPassengerDTO dto) {
        return ResponseEntity.ok(flightService.updatePassenger(tripId, flightId, passengerId, dto));
    }

    @DeleteMapping("/trip/{tripId}/flights/{flightId}/passengers/{passengerId}")
    public ResponseEntity<Void> removePassenger(@PathVariable Long tripId,
                                                @PathVariable Long flightId,
                                                @PathVariable Long passengerId) {
        flightService.removePassenger(tripId, flightId, passengerId);
        return ResponseEntity.noContent().build();
    }
}
