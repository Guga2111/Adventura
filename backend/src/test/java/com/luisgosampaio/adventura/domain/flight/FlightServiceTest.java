package com.luisgosampaio.adventura.domain.flight;

import com.luisgosampaio.adventura.domain.exceptions.FlightNotFoundException;
import com.luisgosampaio.adventura.domain.exceptions.FlightPassengerNotFoundException;
import com.luisgosampaio.adventura.domain.exceptions.MemberNotFoundException;
import com.luisgosampaio.adventura.domain.exceptions.TripNotFoundException;
import com.luisgosampaio.adventura.domain.group.Group;
import com.luisgosampaio.adventura.domain.group.GroupMember;
import com.luisgosampaio.adventura.domain.group.GroupMemberRepository;
import com.luisgosampaio.adventura.domain.group.GroupRole;
import com.luisgosampaio.adventura.domain.trip.Trip;
import com.luisgosampaio.adventura.domain.trip.TripRepository;
import com.luisgosampaio.adventura.domain.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceTest {

    @Mock
    private TripRepository tripRepository;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private FlightPassengerRepository flightPassengerRepository;

    @Mock
    private GroupMemberRepository groupMemberRepository;

    @InjectMocks
    private FlightService flightService;

    private User user;
    private Group group;
    private GroupMember member;
    private Trip trip;
    private Flight flight;
    private FlightDTO flightDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John Doe");

        group = new Group();
        group.setId(1L);
        group.setName("Travel Group");

        member = new GroupMember();
        member.setId(1L);
        member.setGroup(group);
        member.setUser(user);
        member.setRole(GroupRole.MEMBER);

        trip = new Trip();
        trip.setId(1L);
        trip.setGroup(group);
        trip.setDestinations(List.of("Hungary"));
        trip.setStartDate(LocalDate.of(2026, 6, 1));
        trip.setEndDate(LocalDate.of(2026, 6, 15));

        flight = new Flight();
        flight.setId(1L);
        flight.setTrip(trip);
        flight.setFlightNumber("LA3045");
        flight.setAirline("LATAM");
        flight.setOriginAirport("GRU");
        flight.setDestinationAirport("BUD");
        flight.setDepartureLocalTime(LocalDateTime.of(2026, 6, 1, 14, 0));
        flight.setDepartureTimezone("America/Sao_Paulo");
        flight.setArrivalLocalTime(LocalDateTime.of(2026, 6, 2, 6, 0));
        flight.setArrivalTimezone("Europe/Budapest");

        flightDTO = new FlightDTO(
                "LA3045", "LATAM", "GRU", "BUD",
                LocalDateTime.of(2026, 6, 1, 14, 0), "America/Sao_Paulo",
                LocalDateTime.of(2026, 6, 2, 6, 0), "Europe/Budapest",
                "ABC123", "Window seat preferred", null
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void setUpSecurityContext(Long userId) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("test@test.com", null, Collections.emptyList());
        authentication.setDetails(userId);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // ---- getFlights ----

    @Test
    void getFlights_ReturnsList() {
        setUpSecurityContext(1L);
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(groupMemberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(member));
        when(flightRepository.findByTripIdOrderByDepartureLocalTimeAsc(1L)).thenReturn(List.of(flight));

        List<Flight> result = flightService.getFlights(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFlightNumber()).isEqualTo("LA3045");
        verify(flightRepository).findByTripIdOrderByDepartureLocalTimeAsc(1L);
    }

    @Test
    void getFlights_ReturnsEmptyList_WhenNoFlights() {
        setUpSecurityContext(1L);
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(groupMemberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(member));
        when(flightRepository.findByTripIdOrderByDepartureLocalTimeAsc(1L)).thenReturn(Collections.emptyList());

        List<Flight> result = flightService.getFlights(1L);

        assertThat(result).isEmpty();
    }

    @Test
    void getFlights_ThrowsTripNotFoundException() {
        setUpSecurityContext(1L);
        when(tripRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> flightService.getFlights(99L))
                .isInstanceOf(TripNotFoundException.class);

        verify(flightRepository, never()).findByTripIdOrderByDepartureLocalTimeAsc(any());
    }

    @Test
    void getFlights_ThrowsMemberNotFoundException_WhenUserNotInGroup() {
        setUpSecurityContext(2L);
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(groupMemberRepository.findByUserIdAndGroupId(2L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> flightService.getFlights(1L))
                .isInstanceOf(MemberNotFoundException.class);

        verify(flightRepository, never()).findByTripIdOrderByDepartureLocalTimeAsc(any());
    }

    // ---- createFlight ----

    @Test
    void createFlight_Success() {
        setUpSecurityContext(1L);
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(groupMemberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(member));
        when(flightRepository.save(any(Flight.class))).thenReturn(flight);

        Flight result = flightService.createFlight(1L, flightDTO);

        assertThat(result).isNotNull();
        assertThat(result.getFlightNumber()).isEqualTo("LA3045");
        assertThat(result.getAirline()).isEqualTo("LATAM");
        verify(flightRepository).save(any(Flight.class));
    }

    @Test
    void createFlight_WithPassengers_Success() {
        setUpSecurityContext(1L);
        FlightPassengerDTO passengerDTO = new FlightPassengerDTO(
                1L, "28E", SeatClass.ECONOMY, new BigDecimal("1500.00"), FlightPassengerStatus.CONFIRMED
        );
        FlightDTO dtoWithPassengers = new FlightDTO(
                "LA3045", "LATAM", "GRU", "BUD",
                LocalDateTime.of(2026, 6, 1, 14, 0), "America/Sao_Paulo",
                LocalDateTime.of(2026, 6, 2, 6, 0), "Europe/Budapest",
                null, null, List.of(passengerDTO)
        );

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(groupMemberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(member));
        when(groupMemberRepository.findByIdAndGroupId(1L, 1L)).thenReturn(Optional.of(member));
        when(flightRepository.save(any(Flight.class))).thenAnswer(inv -> inv.getArgument(0));

        Flight result = flightService.createFlight(1L, dtoWithPassengers);

        assertThat(result.getPassengers()).hasSize(1);
        assertThat(result.getPassengers().get(0).getSeatNumber()).isEqualTo("28E");
        assertThat(result.getPassengers().get(0).getSeatClass()).isEqualTo(SeatClass.ECONOMY);
        assertThat(result.getPassengers().get(0).getPrice()).isEqualByComparingTo(new BigDecimal("1500.00"));
        assertThat(result.getPassengers().get(0).getStatus()).isEqualTo(FlightPassengerStatus.CONFIRMED);
    }

    @Test
    void createFlight_WithPassengers_ThrowsWhenMemberNotInGroup() {
        setUpSecurityContext(1L);
        FlightPassengerDTO passengerDTO = new FlightPassengerDTO(
                99L, "3A", SeatClass.BUSINESS, new BigDecimal("5000.00"), null
        );
        FlightDTO dtoWithInvalidPassenger = new FlightDTO(
                "LA3045", "LATAM", "GRU", "BUD",
                LocalDateTime.of(2026, 6, 1, 14, 0), "America/Sao_Paulo",
                LocalDateTime.of(2026, 6, 2, 6, 0), "Europe/Budapest",
                null, null, List.of(passengerDTO)
        );

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(groupMemberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(member));
        when(groupMemberRepository.findByIdAndGroupId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> flightService.createFlight(1L, dtoWithInvalidPassenger))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("99");

        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void createFlight_ThrowsTripNotFoundException() {
        setUpSecurityContext(1L);
        when(tripRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> flightService.createFlight(99L, flightDTO))
                .isInstanceOf(TripNotFoundException.class);

        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void createFlight_ThrowsMemberNotFoundException_WhenUserNotInGroup() {
        setUpSecurityContext(2L);
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(groupMemberRepository.findByUserIdAndGroupId(2L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> flightService.createFlight(1L, flightDTO))
                .isInstanceOf(MemberNotFoundException.class);

        verify(flightRepository, never()).save(any(Flight.class));
    }

    // ---- updateFlight ----

    @Test
    void updateFlight_AllFields() {
        setUpSecurityContext(1L);
        FlightDTO updateDTO = new FlightDTO(
                "TP745", "TAP Air Portugal", "LIS", "BUD",
                LocalDateTime.of(2026, 6, 1, 9, 0), "Europe/Lisbon",
                LocalDateTime.of(2026, 6, 1, 13, 30), "Europe/Budapest",
                "XYZ789", "Direct flight", null
        );

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(groupMemberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(member));
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(flightRepository.save(any(Flight.class))).thenReturn(flight);

        Flight result = flightService.updateFlight(1L, 1L, updateDTO);

        assertThat(flight.getFlightNumber()).isEqualTo("TP745");
        assertThat(flight.getAirline()).isEqualTo("TAP Air Portugal");
        assertThat(flight.getOriginAirport()).isEqualTo("LIS");
        assertThat(flight.getDestinationAirport()).isEqualTo("BUD");
        assertThat(flight.getBookingReference()).isEqualTo("XYZ789");
        assertThat(flight.getNotes()).isEqualTo("Direct flight");
        verify(flightRepository).save(flight);
    }

    @Test
    void updateFlight_PartialUpdate_OnlyChangesProvidedFields() {
        setUpSecurityContext(1L);
        FlightDTO partialDTO = new FlightDTO(
                "LA9999", null, null, null,
                null, null, null, null,
                null, null, null
        );

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(groupMemberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(member));
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(flightRepository.save(any(Flight.class))).thenReturn(flight);

        flightService.updateFlight(1L, 1L, partialDTO);

        assertThat(flight.getFlightNumber()).isEqualTo("LA9999");
        assertThat(flight.getAirline()).isEqualTo("LATAM");
        assertThat(flight.getOriginAirport()).isEqualTo("GRU");
    }

    @Test
    void updateFlight_ThrowsFlightNotFoundException_WhenFlightDoesNotExist() {
        setUpSecurityContext(1L);
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(groupMemberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(member));
        when(flightRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> flightService.updateFlight(1L, 99L, flightDTO))
                .isInstanceOf(FlightNotFoundException.class);

        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void updateFlight_ThrowsFlightNotFoundException_WhenFlightBelongsToDifferentTrip() {
        setUpSecurityContext(1L);
        Trip otherTrip = new Trip();
        otherTrip.setId(99L);
        otherTrip.setGroup(group);
        flight.setTrip(otherTrip);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(groupMemberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(member));
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));

        assertThatThrownBy(() -> flightService.updateFlight(1L, 1L, flightDTO))
                .isInstanceOf(FlightNotFoundException.class);

        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void updateFlight_ThrowsTripNotFoundException() {
        setUpSecurityContext(1L);
        when(tripRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> flightService.updateFlight(99L, 1L, flightDTO))
                .isInstanceOf(TripNotFoundException.class);
    }

    @Test
    void updateFlight_ThrowsMemberNotFoundException_WhenUserNotInGroup() {
        setUpSecurityContext(2L);
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(groupMemberRepository.findByUserIdAndGroupId(2L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> flightService.updateFlight(1L, 1L, flightDTO))
                .isInstanceOf(MemberNotFoundException.class);
    }

    // ---- deleteFlight ----

    @Test
    void deleteFlight_Success() {
        setUpSecurityContext(1L);
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(groupMemberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(member));
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));

        flightService.deleteFlight(1L, 1L);

        verify(flightRepository).delete(flight);
    }

    @Test
    void deleteFlight_ThrowsFlightNotFoundException_WhenFlightDoesNotExist() {
        setUpSecurityContext(1L);
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(groupMemberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(member));
        when(flightRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> flightService.deleteFlight(1L, 99L))
                .isInstanceOf(FlightNotFoundException.class);

        verify(flightRepository, never()).delete(any(Flight.class));
    }

    @Test
    void deleteFlight_ThrowsFlightNotFoundException_WhenFlightBelongsToDifferentTrip() {
        setUpSecurityContext(1L);
        Trip otherTrip = new Trip();
        otherTrip.setId(99L);
        otherTrip.setGroup(group);
        flight.setTrip(otherTrip);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(groupMemberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(member));
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));

        assertThatThrownBy(() -> flightService.deleteFlight(1L, 1L))
                .isInstanceOf(FlightNotFoundException.class);

        verify(flightRepository, never()).delete(any(Flight.class));
    }

    @Test
    void deleteFlight_ThrowsTripNotFoundException() {
        setUpSecurityContext(1L);
        when(tripRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> flightService.deleteFlight(99L, 1L))
                .isInstanceOf(TripNotFoundException.class);

        verify(flightRepository, never()).delete(any(Flight.class));
    }

    @Test
    void deleteFlight_ThrowsMemberNotFoundException_WhenUserNotInGroup() {
        setUpSecurityContext(2L);
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(groupMemberRepository.findByUserIdAndGroupId(2L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> flightService.deleteFlight(1L, 1L))
                .isInstanceOf(MemberNotFoundException.class);

        verify(flightRepository, never()).delete(any(Flight.class));
    }

    // ---- joinFlight ----

    @Test
    void joinFlight_Success() {
        setUpSecurityContext(1L);
        FlightPassengerDTO dto = new FlightPassengerDTO(null, "28E", SeatClass.ECONOMY, new BigDecimal("980.00"), FlightPassengerStatus.CONFIRMED);
        FlightPassenger savedPassenger = new FlightPassenger();
        savedPassenger.setId(1L);
        savedPassenger.setGroupMember(member);
        savedPassenger.setSeatNumber("28E");
        savedPassenger.setSeatClass(SeatClass.ECONOMY);
        savedPassenger.setPrice(new BigDecimal("980.00"));
        savedPassenger.setStatus(FlightPassengerStatus.CONFIRMED);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(groupMemberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(member));
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(flightPassengerRepository.existsByFlightIdAndGroupMemberId(1L, 1L)).thenReturn(false);
        when(flightPassengerRepository.save(any(FlightPassenger.class))).thenReturn(savedPassenger);

        FlightPassenger result = flightService.joinFlight(1L, 1L, dto);

        assertThat(result.getSeatNumber()).isEqualTo("28E");
        assertThat(result.getSeatClass()).isEqualTo(SeatClass.ECONOMY);
        assertThat(result.getStatus()).isEqualTo(FlightPassengerStatus.CONFIRMED);
        verify(flightPassengerRepository).save(any(FlightPassenger.class));
    }

    @Test
    void joinFlight_DefaultsStatusToUndecided_WhenStatusIsNull() {
        setUpSecurityContext(1L);
        FlightPassengerDTO dto = new FlightPassengerDTO(null, null, null, null, null);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(groupMemberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(member));
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(flightPassengerRepository.existsByFlightIdAndGroupMemberId(1L, 1L)).thenReturn(false);
        when(flightPassengerRepository.save(any(FlightPassenger.class))).thenAnswer(inv -> inv.getArgument(0));

        FlightPassenger result = flightService.joinFlight(1L, 1L, dto);

        assertThat(result.getStatus()).isEqualTo(FlightPassengerStatus.UNDECIDED);
    }

    @Test
    void joinFlight_ThrowsIllegalArgument_WhenAlreadyEnrolled() {
        setUpSecurityContext(1L);
        FlightPassengerDTO dto = new FlightPassengerDTO(null, "28E", SeatClass.ECONOMY, new BigDecimal("980.00"), FlightPassengerStatus.CONFIRMED);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(groupMemberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(member));
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(flightPassengerRepository.existsByFlightIdAndGroupMemberId(1L, 1L)).thenReturn(true);

        assertThatThrownBy(() -> flightService.joinFlight(1L, 1L, dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already enrolled");

        verify(flightPassengerRepository, never()).save(any(FlightPassenger.class));
    }

    @Test
    void joinFlight_ThrowsFlightNotFoundException_WhenFlightBelongsToDifferentTrip() {
        setUpSecurityContext(1L);
        Trip otherTrip = new Trip();
        otherTrip.setId(99L);
        otherTrip.setGroup(group);
        flight.setTrip(otherTrip);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(groupMemberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(member));
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));

        assertThatThrownBy(() -> flightService.joinFlight(1L, 1L, new FlightPassengerDTO(null, null, null, null, null)))
                .isInstanceOf(FlightNotFoundException.class);

        verify(flightPassengerRepository, never()).save(any(FlightPassenger.class));
    }

    @Test
    void joinFlight_ThrowsTripNotFoundException() {
        setUpSecurityContext(1L);
        when(tripRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> flightService.joinFlight(99L, 1L, new FlightPassengerDTO(null, null, null, null, null)))
                .isInstanceOf(TripNotFoundException.class);
    }

    @Test
    void joinFlight_ThrowsMemberNotFoundException_WhenUserNotInGroup() {
        setUpSecurityContext(2L);
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(groupMemberRepository.findByUserIdAndGroupId(2L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> flightService.joinFlight(1L, 1L, new FlightPassengerDTO(null, null, null, null, null)))
                .isInstanceOf(MemberNotFoundException.class);
    }

    // ---- updatePassenger ----

    @Test
    void updatePassenger_Success() {
        setUpSecurityContext(1L);
        FlightPassenger existingPassenger = new FlightPassenger();
        existingPassenger.setId(1L);
        existingPassenger.setGroupMember(member);
        existingPassenger.setSeatNumber("28E");
        existingPassenger.setSeatClass(SeatClass.ECONOMY);
        existingPassenger.setPrice(new BigDecimal("980.00"));
        existingPassenger.setStatus(FlightPassengerStatus.UNDECIDED);

        FlightPassengerDTO dto = new FlightPassengerDTO(null, "3A", SeatClass.BUSINESS, new BigDecimal("4200.00"), FlightPassengerStatus.CONFIRMED);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(groupMemberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(member));
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(flightPassengerRepository.findByIdAndFlightId(1L, 1L)).thenReturn(Optional.of(existingPassenger));
        when(flightPassengerRepository.save(any(FlightPassenger.class))).thenReturn(existingPassenger);

        FlightPassenger result = flightService.updatePassenger(1L, 1L, 1L, dto);

        assertThat(existingPassenger.getSeatNumber()).isEqualTo("3A");
        assertThat(existingPassenger.getSeatClass()).isEqualTo(SeatClass.BUSINESS);
        assertThat(existingPassenger.getPrice()).isEqualByComparingTo(new BigDecimal("4200.00"));
        assertThat(existingPassenger.getStatus()).isEqualTo(FlightPassengerStatus.CONFIRMED);
        verify(flightPassengerRepository).save(existingPassenger);
    }

    @Test
    void updatePassenger_PartialUpdate_OnlyChangesStatus() {
        setUpSecurityContext(1L);
        FlightPassenger existingPassenger = new FlightPassenger();
        existingPassenger.setId(1L);
        existingPassenger.setSeatNumber("28E");
        existingPassenger.setSeatClass(SeatClass.ECONOMY);
        existingPassenger.setStatus(FlightPassengerStatus.UNDECIDED);

        FlightPassengerDTO dto = new FlightPassengerDTO(null, null, null, null, FlightPassengerStatus.CANCELLED);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(groupMemberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(member));
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(flightPassengerRepository.findByIdAndFlightId(1L, 1L)).thenReturn(Optional.of(existingPassenger));
        when(flightPassengerRepository.save(any(FlightPassenger.class))).thenReturn(existingPassenger);

        flightService.updatePassenger(1L, 1L, 1L, dto);

        assertThat(existingPassenger.getStatus()).isEqualTo(FlightPassengerStatus.CANCELLED);
        assertThat(existingPassenger.getSeatNumber()).isEqualTo("28E");
        assertThat(existingPassenger.getSeatClass()).isEqualTo(SeatClass.ECONOMY);
    }

    @Test
    void updatePassenger_ThrowsFlightPassengerNotFoundException() {
        setUpSecurityContext(1L);
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(groupMemberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(member));
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(flightPassengerRepository.findByIdAndFlightId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> flightService.updatePassenger(1L, 1L, 99L, new FlightPassengerDTO(null, null, null, null, FlightPassengerStatus.CONFIRMED)))
                .isInstanceOf(FlightPassengerNotFoundException.class);

        verify(flightPassengerRepository, never()).save(any(FlightPassenger.class));
    }

    @Test
    void updatePassenger_ThrowsFlightNotFoundException_WhenFlightBelongsToDifferentTrip() {
        setUpSecurityContext(1L);
        Trip otherTrip = new Trip();
        otherTrip.setId(99L);
        otherTrip.setGroup(group);
        flight.setTrip(otherTrip);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(groupMemberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(member));
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));

        assertThatThrownBy(() -> flightService.updatePassenger(1L, 1L, 1L, new FlightPassengerDTO(null, null, null, null, null)))
                .isInstanceOf(FlightNotFoundException.class);
    }

    @Test
    void updatePassenger_ThrowsMemberNotFoundException_WhenUserNotInGroup() {
        setUpSecurityContext(2L);
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(groupMemberRepository.findByUserIdAndGroupId(2L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> flightService.updatePassenger(1L, 1L, 1L, new FlightPassengerDTO(null, null, null, null, null)))
                .isInstanceOf(MemberNotFoundException.class);
    }

    // ---- removePassenger ----

    @Test
    void removePassenger_Success() {
        setUpSecurityContext(1L);
        FlightPassenger passenger = new FlightPassenger();
        passenger.setId(1L);
        passenger.setGroupMember(member);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(groupMemberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(member));
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(flightPassengerRepository.findByIdAndFlightId(1L, 1L)).thenReturn(Optional.of(passenger));

        flightService.removePassenger(1L, 1L, 1L);

        verify(flightPassengerRepository).delete(passenger);
    }

    @Test
    void removePassenger_ThrowsFlightPassengerNotFoundException() {
        setUpSecurityContext(1L);
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(groupMemberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(member));
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(flightPassengerRepository.findByIdAndFlightId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> flightService.removePassenger(1L, 1L, 99L))
                .isInstanceOf(FlightPassengerNotFoundException.class);

        verify(flightPassengerRepository, never()).delete(any(FlightPassenger.class));
    }

    @Test
    void removePassenger_ThrowsFlightNotFoundException_WhenFlightBelongsToDifferentTrip() {
        setUpSecurityContext(1L);
        Trip otherTrip = new Trip();
        otherTrip.setId(99L);
        otherTrip.setGroup(group);
        flight.setTrip(otherTrip);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(groupMemberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(member));
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));

        assertThatThrownBy(() -> flightService.removePassenger(1L, 1L, 1L))
                .isInstanceOf(FlightNotFoundException.class);

        verify(flightPassengerRepository, never()).delete(any(FlightPassenger.class));
    }

    @Test
    void removePassenger_ThrowsTripNotFoundException() {
        setUpSecurityContext(1L);
        when(tripRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> flightService.removePassenger(99L, 1L, 1L))
                .isInstanceOf(TripNotFoundException.class);

        verify(flightPassengerRepository, never()).delete(any(FlightPassenger.class));
    }

    @Test
    void removePassenger_ThrowsMemberNotFoundException_WhenUserNotInGroup() {
        setUpSecurityContext(2L);
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(groupMemberRepository.findByUserIdAndGroupId(2L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> flightService.removePassenger(1L, 1L, 1L))
                .isInstanceOf(MemberNotFoundException.class);

        verify(flightPassengerRepository, never()).delete(any(FlightPassenger.class));
    }
}
