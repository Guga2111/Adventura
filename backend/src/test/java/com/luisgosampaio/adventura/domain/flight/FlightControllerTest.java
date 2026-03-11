package com.luisgosampaio.adventura.domain.flight;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.luisgosampaio.adventura.domain.exceptions.FlightNotFoundException;
import com.luisgosampaio.adventura.domain.exceptions.FlightPassengerNotFoundException;
import com.luisgosampaio.adventura.domain.exceptions.TripNotFoundException;
import com.luisgosampaio.adventura.domain.group.Group;
import com.luisgosampaio.adventura.domain.group.GroupMember;
import com.luisgosampaio.adventura.domain.trip.Trip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FlightControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock
    private FlightService flightService;

    @InjectMocks
    private FlightController flightController;

    private Flight flight;
    private FlightDTO flightDTO;
    private GroupMember groupMember;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(flightController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        Group group = new Group();
        group.setId(1L);
        group.setName("Travel Group");

        Trip trip = new Trip();
        trip.setId(1L);
        trip.setGroup(group);
        trip.setDestinations(List.of("Hungary"));
        trip.setStartDate(LocalDate.of(2026, 6, 1));
        trip.setEndDate(LocalDate.of(2026, 6, 15));

        groupMember = new GroupMember();
        groupMember.setId(1L);
        groupMember.setGroup(group);

        FlightPassenger passenger = new FlightPassenger();
        passenger.setId(1L);
        passenger.setGroupMember(groupMember);
        passenger.setSeatNumber("28E");
        passenger.setSeatClass(SeatClass.ECONOMY);
        passenger.setPrice(new BigDecimal("1500.00"));
        passenger.setStatus(FlightPassengerStatus.CONFIRMED);

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
        flight.setPassengers(List.of(passenger));

        flightDTO = new FlightDTO(
                "LA3045", "LATAM", "GRU", "BUD",
                LocalDateTime.of(2026, 6, 1, 14, 0), "America/Sao_Paulo",
                LocalDateTime.of(2026, 6, 2, 6, 0), "Europe/Budapest",
                "ABC123", null, null
        );
    }

    // ---- GET /trip/{tripId}/flights ----

    @Test
    void getFlights_ReturnsOk() throws Exception {
        when(flightService.getFlights(1L)).thenReturn(List.of(flight));

        mockMvc.perform(get("/trip/1/flights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].flightNumber").value("LA3045"))
                .andExpect(jsonPath("$[0].airline").value("LATAM"))
                .andExpect(jsonPath("$[0].originAirport").value("GRU"))
                .andExpect(jsonPath("$[0].destinationAirport").value("BUD"))
                .andExpect(jsonPath("$[0].passengers[0].seatNumber").value("28E"))
                .andExpect(jsonPath("$[0].passengers[0].seatClass").value("ECONOMY"));
    }

    @Test
    void getFlights_ReturnsEmptyList() throws Exception {
        when(flightService.getFlights(1L)).thenReturn(List.of());

        mockMvc.perform(get("/trip/1/flights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getFlights_ThrowsTripNotFoundException() {
        when(flightService.getFlights(99L)).thenThrow(new TripNotFoundException(99L));

        assertThatThrownBy(() -> mockMvc.perform(get("/trip/99/flights")))
                .cause()
                .isInstanceOf(TripNotFoundException.class);
    }

    // ---- POST /trip/{tripId}/flights ----

    @Test
    void createFlight_ReturnsCreated() throws Exception {
        when(flightService.createFlight(eq(1L), any(FlightDTO.class))).thenReturn(flight);

        mockMvc.perform(post("/trip/1/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(flightDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.flightNumber").value("LA3045"))
                .andExpect(jsonPath("$.airline").value("LATAM"));
    }

    @Test
    void createFlight_ThrowsTripNotFoundException() {
        when(flightService.createFlight(eq(99L), any(FlightDTO.class)))
                .thenThrow(new TripNotFoundException(99L));

        assertThatThrownBy(() -> mockMvc.perform(post("/trip/99/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(flightDTO))))
                .cause()
                .isInstanceOf(TripNotFoundException.class);
    }

    // ---- PUT /trip/{tripId}/flights/{flightId} ----

    @Test
    void updateFlight_ReturnsOk() throws Exception {
        when(flightService.updateFlight(eq(1L), eq(1L), any(FlightDTO.class))).thenReturn(flight);

        mockMvc.perform(put("/trip/1/flights/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(flightDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flightNumber").value("LA3045"));
    }

    @Test
    void updateFlight_ThrowsFlightNotFoundException() {
        when(flightService.updateFlight(eq(1L), eq(99L), any(FlightDTO.class)))
                .thenThrow(new FlightNotFoundException(99L));

        assertThatThrownBy(() -> mockMvc.perform(put("/trip/1/flights/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(flightDTO))))
                .cause()
                .isInstanceOf(FlightNotFoundException.class);
    }

    @Test
    void updateFlight_ThrowsTripNotFoundException() {
        when(flightService.updateFlight(eq(99L), eq(1L), any(FlightDTO.class)))
                .thenThrow(new TripNotFoundException(99L));

        assertThatThrownBy(() -> mockMvc.perform(put("/trip/99/flights/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(flightDTO))))
                .cause()
                .isInstanceOf(TripNotFoundException.class);
    }

    // ---- DELETE /trip/{tripId}/flights/{flightId} ----

    @Test
    void deleteFlight_ReturnsNoContent() throws Exception {
        doNothing().when(flightService).deleteFlight(1L, 1L);

        mockMvc.perform(delete("/trip/1/flights/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteFlight_ThrowsFlightNotFoundException() {
        org.mockito.Mockito.doThrow(new FlightNotFoundException(99L))
                .when(flightService).deleteFlight(1L, 99L);

        assertThatThrownBy(() -> mockMvc.perform(delete("/trip/1/flights/99")))
                .cause()
                .isInstanceOf(FlightNotFoundException.class);
    }

    // ---- POST /trip/{tripId}/flights/{flightId}/passengers ----

    @Test
    void joinFlight_ReturnsCreated() throws Exception {
        FlightPassengerDTO dto = new FlightPassengerDTO(null, "28E", SeatClass.ECONOMY, new BigDecimal("980.00"), FlightPassengerStatus.CONFIRMED);
        FlightPassenger passenger = new FlightPassenger();
        passenger.setId(1L);
        passenger.setGroupMember(groupMember);
        passenger.setSeatNumber("28E");
        passenger.setSeatClass(SeatClass.ECONOMY);
        passenger.setPrice(new BigDecimal("980.00"));
        passenger.setStatus(FlightPassengerStatus.CONFIRMED);

        when(flightService.joinFlight(eq(1L), eq(1L), any(FlightPassengerDTO.class))).thenReturn(passenger);

        mockMvc.perform(post("/trip/1/flights/1/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.seatNumber").value("28E"))
                .andExpect(jsonPath("$.seatClass").value("ECONOMY"))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void joinFlight_ReturnsCreated_WithEmptyBody() throws Exception {
        FlightPassenger passenger = new FlightPassenger();
        passenger.setId(2L);
        passenger.setGroupMember(groupMember);
        passenger.setStatus(FlightPassengerStatus.UNDECIDED);

        when(flightService.joinFlight(eq(1L), eq(1L), any(FlightPassengerDTO.class))).thenReturn(passenger);

        mockMvc.perform(post("/trip/1/flights/1/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("UNDECIDED"));
    }

    @Test
    void joinFlight_ThrowsFlightNotFoundException() {
        when(flightService.joinFlight(eq(1L), eq(99L), any(FlightPassengerDTO.class)))
                .thenThrow(new FlightNotFoundException(99L));

        assertThatThrownBy(() -> mockMvc.perform(post("/trip/1/flights/99/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")))
                .cause()
                .isInstanceOf(FlightNotFoundException.class);
    }

    // ---- PUT /trip/{tripId}/flights/{flightId}/passengers/{passengerId} ----

    @Test
    void updatePassenger_ReturnsOk() throws Exception {
        FlightPassengerDTO dto = new FlightPassengerDTO(null, "3A", SeatClass.BUSINESS, new BigDecimal("4200.00"), FlightPassengerStatus.CONFIRMED);
        FlightPassenger passenger = new FlightPassenger();
        passenger.setId(1L);
        passenger.setGroupMember(groupMember);
        passenger.setSeatNumber("3A");
        passenger.setSeatClass(SeatClass.BUSINESS);
        passenger.setPrice(new BigDecimal("4200.00"));
        passenger.setStatus(FlightPassengerStatus.CONFIRMED);

        when(flightService.updatePassenger(eq(1L), eq(1L), eq(1L), any(FlightPassengerDTO.class))).thenReturn(passenger);

        mockMvc.perform(put("/trip/1/flights/1/passengers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seatNumber").value("3A"))
                .andExpect(jsonPath("$.seatClass").value("BUSINESS"))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void updatePassenger_ThrowsFlightPassengerNotFoundException() {
        when(flightService.updatePassenger(eq(1L), eq(1L), eq(99L), any(FlightPassengerDTO.class)))
                .thenThrow(new FlightPassengerNotFoundException(99L));

        assertThatThrownBy(() -> mockMvc.perform(put("/trip/1/flights/1/passengers/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")))
                .cause()
                .isInstanceOf(FlightPassengerNotFoundException.class);
    }

    // ---- DELETE /trip/{tripId}/flights/{flightId}/passengers/{passengerId} ----

    @Test
    void removePassenger_ReturnsNoContent() throws Exception {
        org.mockito.Mockito.doNothing().when(flightService).removePassenger(1L, 1L, 1L);

        mockMvc.perform(delete("/trip/1/flights/1/passengers/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void removePassenger_ThrowsFlightPassengerNotFoundException() {
        org.mockito.Mockito.doThrow(new FlightPassengerNotFoundException(99L))
                .when(flightService).removePassenger(1L, 1L, 99L);

        assertThatThrownBy(() -> mockMvc.perform(delete("/trip/1/flights/1/passengers/99")))
                .cause()
                .isInstanceOf(FlightPassengerNotFoundException.class);
    }
}
