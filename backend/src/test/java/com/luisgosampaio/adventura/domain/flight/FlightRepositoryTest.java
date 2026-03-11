package com.luisgosampaio.adventura.domain.flight;

import com.luisgosampaio.adventura.domain.group.Group;
import com.luisgosampaio.adventura.domain.group.GroupMember;
import com.luisgosampaio.adventura.domain.group.GroupRole;
import com.luisgosampaio.adventura.domain.trip.Trip;
import com.luisgosampaio.adventura.domain.trip.TripStatus;
import com.luisgosampaio.adventura.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FlightRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private FlightPassengerRepository flightPassengerRepository;

    private Trip trip;
    private Trip otherTrip;
    private GroupMember groupMember;
    private Flight earlyFlight;
    private Flight lateFlight;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("password123");
        entityManager.persistAndFlush(user);

        Group group = new Group();
        group.setName("Travel Group");
        group.setDescription("A travel group");
        group.setCreatedBy(user);
        entityManager.persistAndFlush(group);

        groupMember = new GroupMember();
        groupMember.setGroup(group);
        groupMember.setUser(user);
        groupMember.setRole(GroupRole.ADMIN);
        entityManager.persistAndFlush(groupMember);

        trip = new Trip();
        trip.setGroup(group);
        trip.setDestinations(List.of("Hungary"));
        trip.setStartDate(LocalDate.of(2026, 6, 1));
        trip.setEndDate(LocalDate.of(2026, 6, 15));
        trip.setMainCurrency("BRL");
        trip.setTotalBudget(new BigDecimal("15000.00"));
        trip.setStatus(TripStatus.PLANNING);
        entityManager.persistAndFlush(trip);

        otherTrip = new Trip();
        otherTrip.setGroup(group);
        otherTrip.setDestinations(List.of("Japan"));
        otherTrip.setStartDate(LocalDate.of(2026, 9, 1));
        otherTrip.setEndDate(LocalDate.of(2026, 9, 15));
        otherTrip.setMainCurrency("BRL");
        otherTrip.setTotalBudget(new BigDecimal("20000.00"));
        otherTrip.setStatus(TripStatus.PLANNING);
        entityManager.persistAndFlush(otherTrip);

        earlyFlight = new Flight();
        earlyFlight.setTrip(trip);
        earlyFlight.setFlightNumber("LA3045");
        earlyFlight.setAirline("LATAM");
        earlyFlight.setOriginAirport("GRU");
        earlyFlight.setDestinationAirport("LIS");
        earlyFlight.setDepartureLocalTime(LocalDateTime.of(2026, 6, 1, 8, 0));
        earlyFlight.setDepartureTimezone("America/Sao_Paulo");
        earlyFlight.setArrivalLocalTime(LocalDateTime.of(2026, 6, 1, 20, 0));
        earlyFlight.setArrivalTimezone("Europe/Lisbon");
        entityManager.persistAndFlush(earlyFlight);

        lateFlight = new Flight();
        lateFlight.setTrip(trip);
        lateFlight.setFlightNumber("TP745");
        lateFlight.setAirline("TAP Air Portugal");
        lateFlight.setOriginAirport("LIS");
        lateFlight.setDestinationAirport("BUD");
        lateFlight.setDepartureLocalTime(LocalDateTime.of(2026, 6, 2, 9, 0));
        lateFlight.setDepartureTimezone("Europe/Lisbon");
        lateFlight.setArrivalLocalTime(LocalDateTime.of(2026, 6, 2, 13, 30));
        lateFlight.setArrivalTimezone("Europe/Budapest");
        entityManager.persistAndFlush(lateFlight);
    }

    // ---- findByTripIdOrderByDepartureLocalTimeAsc ----

    @Test
    void findByTripId_ReturnsFlightList() {
        List<Flight> flights = flightRepository.findByTripIdOrderByDepartureLocalTimeAsc(trip.getId());

        assertThat(flights).hasSize(2);
    }

    @Test
    void findByTripId_ReturnsEmpty_WhenNoFlights() {
        List<Flight> flights = flightRepository.findByTripIdOrderByDepartureLocalTimeAsc(otherTrip.getId());

        assertThat(flights).isEmpty();
    }

    @Test
    void findByTripId_ReturnsFlightsOrderedByDepartureTimeAsc() {
        List<Flight> flights = flightRepository.findByTripIdOrderByDepartureLocalTimeAsc(trip.getId());

        assertThat(flights.get(0).getFlightNumber()).isEqualTo("LA3045");
        assertThat(flights.get(1).getFlightNumber()).isEqualTo("TP745");
        assertThat(flights.get(0).getDepartureLocalTime())
                .isBefore(flights.get(1).getDepartureLocalTime());
    }

    @Test
    void findByTripId_DoesNotReturnFlightsFromOtherTrips() {
        Flight otherFlight = new Flight();
        otherFlight.setTrip(otherTrip);
        otherFlight.setFlightNumber("JL717");
        otherFlight.setAirline("Japan Airlines");
        otherFlight.setOriginAirport("GRU");
        otherFlight.setDestinationAirport("NRT");
        otherFlight.setDepartureLocalTime(LocalDateTime.of(2026, 9, 1, 22, 0));
        otherFlight.setDepartureTimezone("America/Sao_Paulo");
        otherFlight.setArrivalLocalTime(LocalDateTime.of(2026, 9, 3, 6, 0));
        otherFlight.setArrivalTimezone("Asia/Tokyo");
        entityManager.persistAndFlush(otherFlight);

        List<Flight> flights = flightRepository.findByTripIdOrderByDepartureLocalTimeAsc(trip.getId());

        assertThat(flights).hasSize(2);
        assertThat(flights).noneMatch(f -> f.getFlightNumber().equals("JL717"));
    }

    // ---- FlightPassenger persistence ----

    @Test
    void flightPassenger_IsPersisted_WithFlight() {
        Flight flightWithPassenger = new Flight();
        flightWithPassenger.setTrip(trip);
        flightWithPassenger.setFlightNumber("G31234");
        flightWithPassenger.setAirline("GOL");
        flightWithPassenger.setOriginAirport("CGH");
        flightWithPassenger.setDestinationAirport("GIG");
        flightWithPassenger.setDepartureLocalTime(LocalDateTime.of(2026, 6, 5, 10, 0));
        flightWithPassenger.setDepartureTimezone("America/Sao_Paulo");
        flightWithPassenger.setArrivalLocalTime(LocalDateTime.of(2026, 6, 5, 11, 0));
        flightWithPassenger.setArrivalTimezone("America/Sao_Paulo");

        FlightPassenger passenger = new FlightPassenger();
        passenger.setFlight(flightWithPassenger);
        passenger.setGroupMember(groupMember);
        passenger.setSeatNumber("12C");
        passenger.setSeatClass(SeatClass.ECONOMY);
        passenger.setPrice(new BigDecimal("450.00"));
        passenger.setStatus(FlightPassengerStatus.CONFIRMED);
        flightWithPassenger.getPassengers().add(passenger);

        entityManager.persistAndFlush(flightWithPassenger);
        entityManager.clear();

        Flight found = flightRepository.findById(flightWithPassenger.getId()).orElseThrow();

        assertThat(found.getPassengers()).hasSize(1);
        assertThat(found.getPassengers().get(0).getSeatNumber()).isEqualTo("12C");
        assertThat(found.getPassengers().get(0).getSeatClass()).isEqualTo(SeatClass.ECONOMY);
        assertThat(found.getPassengers().get(0).getPrice()).isEqualByComparingTo(new BigDecimal("450.00"));
        assertThat(found.getPassengers().get(0).getStatus()).isEqualTo(FlightPassengerStatus.CONFIRMED);
    }

    @Test
    void flightPassenger_DefaultStatus_IsUndecided() {
        Flight flightWithPassenger = new Flight();
        flightWithPassenger.setTrip(trip);
        flightWithPassenger.setFlightNumber("AD4567");
        flightWithPassenger.setAirline("Azul");
        flightWithPassenger.setOriginAirport("VCP");
        flightWithPassenger.setDestinationAirport("GRU");
        flightWithPassenger.setDepartureLocalTime(LocalDateTime.of(2026, 6, 6, 7, 0));
        flightWithPassenger.setDepartureTimezone("America/Sao_Paulo");
        flightWithPassenger.setArrivalLocalTime(LocalDateTime.of(2026, 6, 6, 7, 45));
        flightWithPassenger.setArrivalTimezone("America/Sao_Paulo");

        FlightPassenger passenger = new FlightPassenger();
        passenger.setFlight(flightWithPassenger);
        passenger.setGroupMember(groupMember);
        flightWithPassenger.getPassengers().add(passenger);

        entityManager.persistAndFlush(flightWithPassenger);
        entityManager.clear();

        Flight found = flightRepository.findById(flightWithPassenger.getId()).orElseThrow();

        assertThat(found.getPassengers().get(0).getStatus()).isEqualTo(FlightPassengerStatus.UNDECIDED);
    }

    @Test
    void deleteFlight_CascadesTo_Passengers() {
        Flight flightWithPassenger = new Flight();
        flightWithPassenger.setTrip(trip);
        flightWithPassenger.setFlightNumber("AA100");
        flightWithPassenger.setAirline("American Airlines");
        flightWithPassenger.setOriginAirport("GRU");
        flightWithPassenger.setDestinationAirport("JFK");
        flightWithPassenger.setDepartureLocalTime(LocalDateTime.of(2026, 6, 7, 18, 0));
        flightWithPassenger.setDepartureTimezone("America/Sao_Paulo");
        flightWithPassenger.setArrivalLocalTime(LocalDateTime.of(2026, 6, 8, 2, 0));
        flightWithPassenger.setArrivalTimezone("America/New_York");

        FlightPassenger passenger = new FlightPassenger();
        passenger.setFlight(flightWithPassenger);
        passenger.setGroupMember(groupMember);
        passenger.setSeatNumber("7A");
        passenger.setSeatClass(SeatClass.BUSINESS);
        passenger.setPrice(new BigDecimal("8000.00"));
        passenger.setStatus(FlightPassengerStatus.CONFIRMED);
        flightWithPassenger.getPassengers().add(passenger);

        entityManager.persistAndFlush(flightWithPassenger);
        Long flightId = flightWithPassenger.getId();
        entityManager.clear();

        flightRepository.deleteById(flightId);
        entityManager.flush();

        assertThat(flightRepository.findById(flightId)).isEmpty();
    }

    // ---- FlightPassengerRepository — existsByFlightIdAndGroupMemberId ----

    @Test
    void existsByFlightIdAndGroupMemberId_ReturnsTrue_WhenPassengerExists() {
        Flight flight = buildFlight("LA1000", "LATAM", "GRU", "LIS",
                LocalDateTime.of(2026, 6, 10, 8, 0), "America/Sao_Paulo",
                LocalDateTime.of(2026, 6, 10, 20, 0), "Europe/Lisbon");

        FlightPassenger passenger = new FlightPassenger();
        passenger.setFlight(flight);
        passenger.setGroupMember(groupMember);
        passenger.setStatus(FlightPassengerStatus.CONFIRMED);
        flight.getPassengers().add(passenger);
        entityManager.persistAndFlush(flight);

        boolean exists = flightPassengerRepository.existsByFlightIdAndGroupMemberId(flight.getId(), groupMember.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void existsByFlightIdAndGroupMemberId_ReturnsFalse_WhenPassengerNotEnrolled() {
        Flight flight = buildFlight("LA2000", "LATAM", "GRU", "LIS",
                LocalDateTime.of(2026, 6, 11, 8, 0), "America/Sao_Paulo",
                LocalDateTime.of(2026, 6, 11, 20, 0), "Europe/Lisbon");
        entityManager.persistAndFlush(flight);

        boolean exists = flightPassengerRepository.existsByFlightIdAndGroupMemberId(flight.getId(), groupMember.getId());

        assertThat(exists).isFalse();
    }

    // ---- FlightPassengerRepository — findByIdAndFlightId ----

    @Test
    void findByIdAndFlightId_ReturnsPassenger_WhenExists() {
        Flight flight = buildFlight("LA3000", "LATAM", "GRU", "BUD",
                LocalDateTime.of(2026, 6, 12, 8, 0), "America/Sao_Paulo",
                LocalDateTime.of(2026, 6, 13, 6, 0), "Europe/Budapest");

        FlightPassenger passenger = new FlightPassenger();
        passenger.setFlight(flight);
        passenger.setGroupMember(groupMember);
        passenger.setSeatNumber("22C");
        passenger.setSeatClass(SeatClass.ECONOMY);
        passenger.setStatus(FlightPassengerStatus.CONFIRMED);
        flight.getPassengers().add(passenger);
        entityManager.persistAndFlush(flight);
        entityManager.clear();

        var result = flightPassengerRepository.findByIdAndFlightId(passenger.getId(), flight.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getSeatNumber()).isEqualTo("22C");
    }

    @Test
    void findByIdAndFlightId_ReturnsEmpty_WhenPassengerBelongsToDifferentFlight() {
        Flight flight1 = buildFlight("LA4000", "LATAM", "GRU", "LIS",
                LocalDateTime.of(2026, 6, 14, 8, 0), "America/Sao_Paulo",
                LocalDateTime.of(2026, 6, 14, 20, 0), "Europe/Lisbon");
        Flight flight2 = buildFlight("LA5000", "LATAM", "LIS", "BUD",
                LocalDateTime.of(2026, 6, 15, 9, 0), "Europe/Lisbon",
                LocalDateTime.of(2026, 6, 15, 13, 0), "Europe/Budapest");

        FlightPassenger passenger = new FlightPassenger();
        passenger.setFlight(flight1);
        passenger.setGroupMember(groupMember);
        passenger.setStatus(FlightPassengerStatus.UNDECIDED);
        flight1.getPassengers().add(passenger);

        entityManager.persistAndFlush(flight1);
        entityManager.persistAndFlush(flight2);
        entityManager.clear();

        // Look up passenger using flight2's id — should return empty
        var result = flightPassengerRepository.findByIdAndFlightId(passenger.getId(), flight2.getId());

        assertThat(result).isEmpty();
    }

    private Flight buildFlight(String number, String airline, String origin, String destination,
                                LocalDateTime departure, String depTz,
                                LocalDateTime arrival, String arrTz) {
        Flight f = new Flight();
        f.setTrip(trip);
        f.setFlightNumber(number);
        f.setAirline(airline);
        f.setOriginAirport(origin);
        f.setDestinationAirport(destination);
        f.setDepartureLocalTime(departure);
        f.setDepartureTimezone(depTz);
        f.setArrivalLocalTime(arrival);
        f.setArrivalTimezone(arrTz);
        return f;
    }
}
