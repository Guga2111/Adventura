package com.luisgosampaio.adventura.domain.trip;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/trip")
public class TripController {

    private final TripService tripService;

    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTrip (@PathVariable Long id) {
        return new ResponseEntity<>(tripService.getTrip(id), HttpStatus.OK);
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<Trip>> getTripsByGroup (@PathVariable Long groupId) {
        return new ResponseEntity<>(tripService.getTripsByGroup(groupId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Trip>> getAllTrips () {
        return new ResponseEntity<>(tripService.getAllTrips(), HttpStatus.OK);
    }

    @PostMapping("/group/{groupId}")
    public ResponseEntity<Trip> createTrip (Trip trip, Long groupId) {
        return new ResponseEntity<>(tripService.createTrip(trip, groupId), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Trip> updateTrip (TripDTO tripDTO, @PathVariable Long id) {
        return new ResponseEntity<>(tripService.updateTrip(tripDTO, id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip (@PathVariable Long id) {
        tripService.deleteTrip(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
