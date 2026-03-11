package com.luisgosampaio.adventura.domain.flight;

import com.luisgosampaio.adventura.domain.exceptions.FlightNotFoundException;
import com.luisgosampaio.adventura.domain.exceptions.FlightPassengerNotFoundException;
import com.luisgosampaio.adventura.domain.exceptions.MemberNotFoundException;
import com.luisgosampaio.adventura.domain.exceptions.TripNotFoundException;
import com.luisgosampaio.adventura.domain.group.GroupMember;
import com.luisgosampaio.adventura.domain.group.GroupMemberRepository;
import com.luisgosampaio.adventura.domain.trip.Trip;
import com.luisgosampaio.adventura.domain.trip.TripRepository;
import com.luisgosampaio.adventura.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightService {

    private final TripRepository tripRepository;
    private final FlightRepository flightRepository;
    private final FlightPassengerRepository flightPassengerRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Transactional(readOnly = true)
    public List<Flight> getFlights(Long tripId) {
        Long userId = SecurityUtils.getAuthenticatedUserId();

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException(tripId));

        Long groupId = trip.getGroup().getId();
        groupMemberRepository.findByUserIdAndGroupId(userId, groupId)
                .orElseThrow(() -> new MemberNotFoundException(groupId, userId));

        return flightRepository.findByTripIdOrderByDepartureLocalTimeAsc(tripId);
    }

    @Transactional
    public Flight createFlight(Long tripId, FlightDTO dto) {
        Long userId = SecurityUtils.getAuthenticatedUserId();

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException(tripId));

        Long groupId = trip.getGroup().getId();
        groupMemberRepository.findByUserIdAndGroupId(userId, groupId)
                .orElseThrow(() -> new MemberNotFoundException(groupId, userId));

        Flight flight = new Flight();
        flight.setTrip(trip);
        mapDtoToFlight(dto, flight, groupId);

        return flightRepository.save(flight);
    }

    @Transactional
    public Flight updateFlight(Long tripId, Long flightId, FlightDTO dto) {
        Long userId = SecurityUtils.getAuthenticatedUserId();

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException(tripId));

        Long groupId = trip.getGroup().getId();
        groupMemberRepository.findByUserIdAndGroupId(userId, groupId)
                .orElseThrow(() -> new MemberNotFoundException(groupId, userId));

        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new FlightNotFoundException(flightId));

        if (!flight.getTrip().getId().equals(tripId)) {
            throw new FlightNotFoundException(flightId);
        }

        mapDtoToFlight(dto, flight, groupId);

        return flightRepository.save(flight);
    }

    @Transactional
    public void deleteFlight(Long tripId, Long flightId) {
        Long userId = SecurityUtils.getAuthenticatedUserId();

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException(tripId));

        Long groupId = trip.getGroup().getId();
        groupMemberRepository.findByUserIdAndGroupId(userId, groupId)
                .orElseThrow(() -> new MemberNotFoundException(groupId, userId));

        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new FlightNotFoundException(flightId));

        if (!flight.getTrip().getId().equals(tripId)) {
            throw new FlightNotFoundException(flightId);
        }

        flightRepository.delete(flight);
    }

    @Transactional
    public FlightPassenger joinFlight(Long tripId, Long flightId, FlightPassengerDTO dto) {
        Long userId = SecurityUtils.getAuthenticatedUserId();

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException(tripId));

        Long groupId = trip.getGroup().getId();
        GroupMember member = groupMemberRepository.findByUserIdAndGroupId(userId, groupId)
                .orElseThrow(() -> new MemberNotFoundException(groupId, userId));

        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new FlightNotFoundException(flightId));

        if (!flight.getTrip().getId().equals(tripId)) {
            throw new FlightNotFoundException(flightId);
        }

        if (flightPassengerRepository.existsByFlightIdAndGroupMemberId(flightId, member.getId())) {
            throw new IllegalArgumentException("You are already enrolled in this flight");
        }

        FlightPassenger passenger = new FlightPassenger();
        passenger.setFlight(flight);
        passenger.setGroupMember(member);
        passenger.setSeatNumber(dto.seatNumber());
        passenger.setSeatClass(dto.seatClass());
        passenger.setPrice(dto.price());
        passenger.setStatus(dto.status() != null ? dto.status() : FlightPassengerStatus.UNDECIDED);

        return flightPassengerRepository.save(passenger);
    }

    @Transactional
    public FlightPassenger updatePassenger(Long tripId, Long flightId, Long passengerId, FlightPassengerDTO dto) {
        Long userId = SecurityUtils.getAuthenticatedUserId();

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException(tripId));

        Long groupId = trip.getGroup().getId();
        groupMemberRepository.findByUserIdAndGroupId(userId, groupId)
                .orElseThrow(() -> new MemberNotFoundException(groupId, userId));

        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new FlightNotFoundException(flightId));

        if (!flight.getTrip().getId().equals(tripId)) {
            throw new FlightNotFoundException(flightId);
        }

        FlightPassenger passenger = flightPassengerRepository.findByIdAndFlightId(passengerId, flightId)
                .orElseThrow(() -> new FlightPassengerNotFoundException(passengerId));

        if (dto.seatNumber() != null) passenger.setSeatNumber(dto.seatNumber());
        if (dto.seatClass() != null) passenger.setSeatClass(dto.seatClass());
        if (dto.price() != null) passenger.setPrice(dto.price());
        if (dto.status() != null) passenger.setStatus(dto.status());

        return flightPassengerRepository.save(passenger);
    }

    @Transactional
    public void removePassenger(Long tripId, Long flightId, Long passengerId) {
        Long userId = SecurityUtils.getAuthenticatedUserId();

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException(tripId));

        Long groupId = trip.getGroup().getId();
        groupMemberRepository.findByUserIdAndGroupId(userId, groupId)
                .orElseThrow(() -> new MemberNotFoundException(groupId, userId));

        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new FlightNotFoundException(flightId));

        if (!flight.getTrip().getId().equals(tripId)) {
            throw new FlightNotFoundException(flightId);
        }

        FlightPassenger passenger = flightPassengerRepository.findByIdAndFlightId(passengerId, flightId)
                .orElseThrow(() -> new FlightPassengerNotFoundException(passengerId));

        flightPassengerRepository.delete(passenger);
    }

    private void mapDtoToFlight(FlightDTO dto, Flight flight, Long groupId) {
        if (dto.flightNumber() != null) flight.setFlightNumber(dto.flightNumber());
        if (dto.airline() != null) flight.setAirline(dto.airline());
        if (dto.originAirport() != null) flight.setOriginAirport(dto.originAirport());
        if (dto.destinationAirport() != null) flight.setDestinationAirport(dto.destinationAirport());
        if (dto.departureLocalTime() != null) flight.setDepartureLocalTime(dto.departureLocalTime());
        if (dto.departureTimezone() != null) flight.setDepartureTimezone(dto.departureTimezone());
        if (dto.arrivalLocalTime() != null) flight.setArrivalLocalTime(dto.arrivalLocalTime());
        if (dto.arrivalTimezone() != null) flight.setArrivalTimezone(dto.arrivalTimezone());
        if (dto.bookingReference() != null) flight.setBookingReference(dto.bookingReference());
        if (dto.notes() != null) flight.setNotes(dto.notes());

        if (dto.passengers() != null) {
            flight.getPassengers().clear();

            List<FlightPassenger> passengers = new ArrayList<>();
            for (FlightPassengerDTO pDto : dto.passengers()) {
                GroupMember member = groupMemberRepository.findByIdAndGroupId(pDto.groupMemberId(), groupId)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "GroupMember with id " + pDto.groupMemberId() + " does not belong to this group"));

                FlightPassenger passenger = new FlightPassenger();
                passenger.setFlight(flight);
                passenger.setGroupMember(member);
                passenger.setSeatNumber(pDto.seatNumber());
                passenger.setSeatClass(pDto.seatClass());
                passenger.setPrice(pDto.price());
                passenger.setStatus(pDto.status() != null ? pDto.status() : FlightPassengerStatus.UNDECIDED);
                passengers.add(passenger);
            }
            flight.getPassengers().addAll(passengers);
        }
    }
}
