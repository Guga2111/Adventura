package com.luisgosampaio.adventura.domain.trip;

import com.luisgosampaio.adventura.domain.exceptions.GroupNotFoundException;
import com.luisgosampaio.adventura.domain.exceptions.TripNotFoundException;
import com.luisgosampaio.adventura.domain.group.Group;
import com.luisgosampaio.adventura.domain.group.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final GroupRepository groupRepository;

    @Transactional(readOnly = true)
    public Trip getTrip (Long id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new TripNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Trip> getTripsByGroup (Long groupId) {
        return tripRepository.findByGroupIdOrderByStartDateDesc(groupId);
    }

    @Transactional(readOnly = true)
    public List<Trip> getAllTrips () {
        return tripRepository.findAll();
    }

    @Transactional
    public Trip createTrip (Trip trip, Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        trip.setGroup(group);
        return tripRepository.save(trip);
    }

    @Transactional
    public Trip updateTrip (TripDTO tripDTO, Long id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new TripNotFoundException(id));

        if (tripDTO.getDestiny() != null) trip.setDestiny(tripDTO.getDestiny());
        if (tripDTO.getDescription() != null) trip.setDescription(tripDTO.getDescription());
        if (tripDTO.getStartDate() != null) trip.setStartDate(tripDTO.getStartDate());
        if (tripDTO.getEndDate() != null) trip.setEndDate(tripDTO.getEndDate());
        if (tripDTO.getMainCurrency() != null) trip.setMainCurrency(tripDTO.getMainCurrency());
        if (tripDTO.getTotalBudget() != null) trip.setTotalBudget(tripDTO.getTotalBudget());
        if (tripDTO.getStatus() != null) trip.setStatus(tripDTO.getStatus());

        return tripRepository.save(trip);
    }

    @Transactional
    public void deleteTrip (Long id) {
        tripRepository.deleteById(id);
    }
}
