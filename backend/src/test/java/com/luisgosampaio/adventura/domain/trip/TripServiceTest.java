package com.luisgosampaio.adventura.domain.trip;

import com.luisgosampaio.adventura.domain.exceptions.GroupNotFoundException;
import com.luisgosampaio.adventura.domain.exceptions.TripNotFoundException;
import com.luisgosampaio.adventura.domain.group.Group;
import com.luisgosampaio.adventura.domain.group.GroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TripServiceTest {

    @Mock
    private TripRepository tripRepository;

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private TripService tripService;

    private Trip trip;
    private Group group;

    @BeforeEach
    void setUp() {
        group = new Group();
        group.setId(1L);
        group.setName("Travel Group");

        trip = new Trip();
        trip.setId(1L);
        trip.setGroup(group);
        trip.setDestiny("Tokyo");
        trip.setDescription("Trip to Japan");
        trip.setStartDate(LocalDate.of(2026, 6, 1));
        trip.setEndDate(LocalDate.of(2026, 6, 15));
        trip.setMainCurrency("BRL");
        trip.setTotalBudget(new BigDecimal("15000.00"));
        trip.setStatus(TripStatus.PLANNING);
    }

    @Test
    void getTrip_ReturnsTrip() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));

        Trip result = tripService.getTrip(1L);

        assertThat(result).isEqualTo(trip);
        assertThat(result.getDestiny()).isEqualTo("Tokyo");
        verify(tripRepository).findById(1L);
    }

    @Test
    void getTrip_ThrowsTripNotFoundException() {
        when(tripRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tripService.getTrip(99L))
                .isInstanceOf(TripNotFoundException.class);
    }

    @Test
    void getTripsByGroup_ReturnsList() {
        when(tripRepository.findByGroupIdOrderByStartDateDesc(1L)).thenReturn(List.of(trip));

        List<Trip> result = tripService.getTripsByGroup(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDestiny()).isEqualTo("Tokyo");
    }

    @Test
    void getAllTrips_ReturnsList() {
        when(tripRepository.findAll()).thenReturn(List.of(trip));

        List<Trip> result = tripService.getAllTrips();

        assertThat(result).hasSize(1);
    }

    @Test
    void createTrip_Success() {
        Trip newTrip = new Trip();
        newTrip.setDestiny("Paris");

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(tripRepository.save(any(Trip.class))).thenReturn(trip);

        Trip result = tripService.createTrip(newTrip, 1L);

        assertThat(result).isNotNull();
        assertThat(newTrip.getGroup()).isEqualTo(group);
        verify(tripRepository).save(newTrip);
    }

    @Test
    void createTrip_ThrowsGroupNotFoundException() {
        when(groupRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tripService.createTrip(trip, 99L))
                .isInstanceOf(GroupNotFoundException.class);

        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    void updateTrip_AllFields() {
        TripDTO dto = new TripDTO();
        dto.setDestiny("Kyoto");
        dto.setDescription("Updated description");
        dto.setStartDate(LocalDate.of(2026, 7, 1));
        dto.setEndDate(LocalDate.of(2026, 7, 10));
        dto.setMainCurrency("USD");
        dto.setTotalBudget(new BigDecimal("20000.00"));
        dto.setStatus(TripStatus.CONFIRMED);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(tripRepository.save(any(Trip.class))).thenReturn(trip);

        Trip result = tripService.updateTrip(dto, 1L);

        assertThat(trip.getDestiny()).isEqualTo("Kyoto");
        assertThat(trip.getDescription()).isEqualTo("Updated description");
        assertThat(trip.getStartDate()).isEqualTo(LocalDate.of(2026, 7, 1));
        assertThat(trip.getEndDate()).isEqualTo(LocalDate.of(2026, 7, 10));
        assertThat(trip.getMainCurrency()).isEqualTo("USD");
        assertThat(trip.getTotalBudget()).isEqualByComparingTo(new BigDecimal("20000.00"));
        assertThat(trip.getStatus()).isEqualTo(TripStatus.CONFIRMED);
        verify(tripRepository).save(trip);
    }

    @Test
    void updateTrip_PartialUpdate() {
        TripDTO dto = new TripDTO();
        dto.setDestiny("Kyoto");

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(tripRepository.save(any(Trip.class))).thenReturn(trip);

        tripService.updateTrip(dto, 1L);

        assertThat(trip.getDestiny()).isEqualTo("Kyoto");
        assertThat(trip.getDescription()).isEqualTo("Trip to Japan");
        assertThat(trip.getMainCurrency()).isEqualTo("BRL");
        assertThat(trip.getStatus()).isEqualTo(TripStatus.PLANNING);
    }

    @Test
    void updateTrip_ThrowsTripNotFoundException() {
        TripDTO dto = new TripDTO();
        when(tripRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tripService.updateTrip(dto, 99L))
                .isInstanceOf(TripNotFoundException.class);
    }

    @Test
    void deleteTrip_Success() {
        tripService.deleteTrip(1L);

        verify(tripRepository).deleteById(1L);
    }
}