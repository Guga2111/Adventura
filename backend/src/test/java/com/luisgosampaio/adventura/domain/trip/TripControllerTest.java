package com.luisgosampaio.adventura.domain.trip;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.luisgosampaio.adventura.domain.exceptions.GroupNotFoundException;
import com.luisgosampaio.adventura.domain.exceptions.TripNotFoundException;
import com.luisgosampaio.adventura.domain.group.Group;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TripControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock
    private TripService tripService;

    @InjectMocks
    private TripController tripController;

    private Trip trip;
    private Group group;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tripController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        group = new Group();
        group.setId(1L);
        group.setName("Travel Group");

        trip = new Trip();
        trip.setId(1L);
        trip.setGroup(group);
        trip.setDestinations(List.of("Tokyo"));
        trip.setDescription("Trip to Japan");
        trip.setStartDate(LocalDate.of(2026, 6, 1));
        trip.setEndDate(LocalDate.of(2026, 6, 15));
        trip.setMainCurrency("BRL");
        trip.setTotalBudget(new BigDecimal("15000.00"));
        trip.setStatus(TripStatus.PLANNING);
    }

    @Test
    void getTrip_ReturnsOk() throws Exception {
        when(tripService.getTrip(1L)).thenReturn(trip);

        mockMvc.perform(get("/trip/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.destinations[0]").value("Tokyo"))
                .andExpect(jsonPath("$.status").value("PLANNING"));
    }

    @Test
    void getTrip_ThrowsTripNotFoundException() {
        when(tripService.getTrip(99L)).thenThrow(new TripNotFoundException(99L));

        assertThatThrownBy(() -> mockMvc.perform(get("/trip/99")))
                .cause()
                .isInstanceOf(TripNotFoundException.class);
    }

    @Test
    void getTripsByGroup_ReturnsOk() throws Exception {
        when(tripService.getTripsByGroup(1L)).thenReturn(List.of(trip));

        mockMvc.perform(get("/trip/group/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].destinations[0]").value("Tokyo"));
    }

    @Test
    void getAllTrips_ReturnsOk() throws Exception {
        when(tripService.getAllTrips()).thenReturn(List.of(trip));

        mockMvc.perform(get("/trip"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].destinations[0]").value("Tokyo"));
    }

    @Test
    void createTrip_ReturnsCreated() throws Exception {
        when(tripService.createTrip(any(Trip.class), eq(1L), eq(1L))).thenReturn(trip);

        mockMvc.perform(post("/trip/group/1/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trip)))
                .andExpect(status().isCreated());
    }

    @Test
    void createTrip_ThrowsGroupNotFoundException() {
        when(tripService.createTrip(any(Trip.class), eq(99L), eq(1L))).thenThrow(new GroupNotFoundException(99L));

        assertThatThrownBy(() -> mockMvc.perform(post("/trip/group/99/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trip))))
                .cause()
                .isInstanceOf(GroupNotFoundException.class);
    }

    @Test
    void updateTrip_ReturnsOk() throws Exception {
        when(tripService.updateTrip(any(TripDTO.class), eq(1L), eq(1L))).thenReturn(trip);

        TripDTO dto = new TripDTO();
        dto.setDescription("Updated");
        mockMvc.perform(put("/trip/1/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateTrip_ThrowsTripNotFoundException() {
        when(tripService.updateTrip(any(TripDTO.class), eq(99L), eq(1L))).thenThrow(new TripNotFoundException(99L));

        TripDTO dto = new TripDTO();
        assertThatThrownBy(() -> mockMvc.perform(put("/trip/99/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))))
                .cause()
                .isInstanceOf(TripNotFoundException.class);
    }

    @Test
    void deleteTrip_ReturnsNoContent() throws Exception {
        doNothing().when(tripService).deleteTrip(1L, 1L);

        mockMvc.perform(delete("/trip/1/user/1"))
                .andExpect(status().isNoContent());
    }
}
