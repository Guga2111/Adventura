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

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Trip>> getTripsByUser (@PathVariable Long userId) {
        return new ResponseEntity<>(tripService.getTripsByUser(userId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Trip>> getAllTrips () {
        return new ResponseEntity<>(tripService.getAllTrips(), HttpStatus.OK);
    }

    @PostMapping("/group/{groupId}/user/{userId}")
    public ResponseEntity<Trip> createTrip (@RequestBody Trip trip, @PathVariable Long groupId, @PathVariable Long userId) {
        return new ResponseEntity<>(tripService.createTrip(trip, groupId, userId), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/user/{userId}")
    public ResponseEntity<Trip> updateTrip (@RequestBody TripDTO tripDTO, @PathVariable Long id, @PathVariable Long userId) {
        return new ResponseEntity<>(tripService.updateTrip(tripDTO, id, userId), HttpStatus.OK);
    }

    @DeleteMapping("/{id}/user/{userId}")
    public ResponseEntity<Void> deleteTrip (@PathVariable Long id, @PathVariable Long userId) {
        tripService.deleteTrip(id, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
