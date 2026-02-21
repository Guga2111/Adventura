package com.luisgosampaio.adventura.domain.trip;

import com.luisgosampaio.adventura.domain.exceptions.GroupNotFoundException;
import com.luisgosampaio.adventura.domain.exceptions.MemberNotFoundException;
import com.luisgosampaio.adventura.domain.exceptions.TripNotFoundException;
import com.luisgosampaio.adventura.domain.exceptions.UnauthorizedGroupActionException;
import com.luisgosampaio.adventura.domain.group.Group;
import com.luisgosampaio.adventura.domain.group.GroupMember;
import com.luisgosampaio.adventura.domain.group.GroupMemberRepository;
import com.luisgosampaio.adventura.domain.group.GroupRepository;
import com.luisgosampaio.adventura.domain.group.GroupRole;
import com.luisgosampaio.adventura.domain.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
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

    @Mock
    private GroupMemberRepository memberRepository;

    @Mock
    private UnsplashService unsplashService;

    @InjectMocks
    private TripService tripService;

    private Trip trip;
    private Group group;
    private User user;
    private GroupMember adminMember;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John Doe");

        group = new Group();
        group.setId(1L);
        group.setName("Travel Group");

        adminMember = new GroupMember();
        adminMember.setId(1L);
        adminMember.setGroup(group);
        adminMember.setUser(user);
        adminMember.setRole(GroupRole.ADMIN);

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

    // ---- getTrip ----

    @Test
    void getTrip_ReturnsTrip() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));

        Trip result = tripService.getTrip(1L);

        assertThat(result).isEqualTo(trip);
        assertThat(result.getDestinations()).containsExactly("Tokyo");
        verify(tripRepository).findById(1L);
    }

    @Test
    void getTrip_ThrowsTripNotFoundException() {
        when(tripRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tripService.getTrip(99L))
                .isInstanceOf(TripNotFoundException.class);
    }

    // ---- getTripsByGroup ----

    @Test
    void getTripsByGroup_ReturnsList() {
        when(tripRepository.findByGroupIdOrderByStartDateDesc(1L)).thenReturn(List.of(trip));

        List<Trip> result = tripService.getTripsByGroup(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDestinations()).containsExactly("Tokyo");
    }

    // ---- getAllTrips ----

    @Test
    void getAllTrips_ReturnsList() {
        when(tripRepository.findAll()).thenReturn(List.of(trip));

        List<Trip> result = tripService.getAllTrips();

        assertThat(result).hasSize(1);
    }

    // ---- createTrip ----

    @Test
    void createTrip_Success() {
        setUpSecurityContext(1L);
        Trip newTrip = new Trip();
        newTrip.setDestinations(List.of("Paris"));

        when(memberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(adminMember));
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(unsplashService.fetchCoverPhoto("Paris")).thenReturn(
                new UnsplashPhotoData("https://unsplash.com/paris.jpg", "John Photo", "https://unsplash.com/@johnphoto", "https://api.unsplash.com/photos/1/download"));
        when(tripRepository.save(any(Trip.class))).thenReturn(trip);

        Trip result = tripService.createTrip(newTrip, 1L, 1L);

        assertThat(result).isNotNull();
        assertThat(newTrip.getGroup()).isEqualTo(group);
        verify(tripRepository).save(newTrip);
    }

    @Test
    void createTrip_ThrowsGroupNotFoundException() {
        setUpSecurityContext(1L);
        when(memberRepository.findByUserIdAndGroupId(1L, 99L)).thenReturn(Optional.of(adminMember));
        when(groupRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tripService.createTrip(trip, 99L, 1L))
                .isInstanceOf(GroupNotFoundException.class);

        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    void createTrip_ThrowsUnauthorizedWhenNotAdmin() {
        setUpSecurityContext(2L);
        GroupMember regularMember = new GroupMember();
        regularMember.setRole(GroupRole.MEMBER);

        when(memberRepository.findByUserIdAndGroupId(2L, 1L)).thenReturn(Optional.of(regularMember));

        assertThatThrownBy(() -> tripService.createTrip(trip, 1L, 2L))
                .isInstanceOf(UnauthorizedGroupActionException.class);

        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    void createTrip_ThrowsAccessDeniedWhenUserIdMismatch() {
        setUpSecurityContext(10L);

        assertThatThrownBy(() -> tripService.createTrip(trip, 1L, 1L))
                .isInstanceOf(AccessDeniedException.class);

        verify(tripRepository, never()).save(any(Trip.class));
    }

    // ---- updateTrip ----

    @Test
    void updateTrip_AllFields() {
        setUpSecurityContext(1L);
        TripDTO dto = new TripDTO();
        dto.setDestinations(List.of("Kyoto"));
        dto.setDescription("Updated description");
        dto.setStartDate(LocalDate.of(2026, 7, 1));
        dto.setEndDate(LocalDate.of(2026, 7, 10));
        dto.setMainCurrency("USD");
        dto.setTotalBudget(new BigDecimal("20000.00"));
        dto.setStatus(TripStatus.CONFIRMED);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(memberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(adminMember));
        when(unsplashService.fetchCoverPhoto("Kyoto")).thenReturn(
                new UnsplashPhotoData("https://unsplash.com/kyoto.jpg", "Jane Photo", "https://unsplash.com/@janephoto", "https://api.unsplash.com/photos/2/download"));
        when(tripRepository.save(any(Trip.class))).thenReturn(trip);

        Trip result = tripService.updateTrip(dto, 1L, 1L);

        assertThat(trip.getDestinations()).containsExactly("Kyoto");
        assertThat(trip.getDescription()).isEqualTo("Updated description");
        assertThat(trip.getStartDate()).isEqualTo(LocalDate.of(2026, 7, 1));
        assertThat(trip.getEndDate()).isEqualTo(LocalDate.of(2026, 7, 10));
        assertThat(trip.getMainCurrency()).isEqualTo("USD");
        assertThat(trip.getTotalBudget()).isEqualByComparingTo(new BigDecimal("20000.00"));
        assertThat(trip.getStatus()).isEqualTo(TripStatus.CONFIRMED);
        assertThat(trip.getCoverImageUrl()).isEqualTo("https://unsplash.com/kyoto.jpg");
        assertThat(trip.getCoverImageAuthor()).isEqualTo("Jane Photo");
        assertThat(trip.getCoverImageAuthorUrl()).isEqualTo("https://unsplash.com/@janephoto");
        verify(unsplashService).fetchCoverPhoto("Kyoto");
        verify(tripRepository).save(trip);
    }

    @Test
    void updateTrip_PartialUpdate() {
        setUpSecurityContext(1L);
        TripDTO dto = new TripDTO();
        dto.setDestinations(List.of("Kyoto"));

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(memberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(adminMember));
        when(unsplashService.fetchCoverPhoto("Kyoto")).thenReturn(
                new UnsplashPhotoData("https://unsplash.com/kyoto.jpg", "Jane Photo", "https://unsplash.com/@janephoto", "https://api.unsplash.com/photos/2/download"));
        when(tripRepository.save(any(Trip.class))).thenReturn(trip);

        tripService.updateTrip(dto, 1L, 1L);

        assertThat(trip.getDestinations()).containsExactly("Kyoto");
        assertThat(trip.getDescription()).isEqualTo("Trip to Japan");
        assertThat(trip.getMainCurrency()).isEqualTo("BRL");
        assertThat(trip.getStatus()).isEqualTo(TripStatus.PLANNING);
    }

    @Test
    void updateTrip_DoesNotRefreshCoverWhenDestinationsNull() {
        setUpSecurityContext(1L);
        TripDTO dto = new TripDTO();
        dto.setDescription("Only updating description");

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(memberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(adminMember));
        when(tripRepository.save(any(Trip.class))).thenReturn(trip);

        tripService.updateTrip(dto, 1L, 1L);

        verify(unsplashService, never()).fetchCoverPhoto(any());
    }

    @Test
    void updateTrip_ThrowsTripNotFoundException() {
        setUpSecurityContext(1L);
        TripDTO dto = new TripDTO();
        when(tripRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tripService.updateTrip(dto, 99L, 1L))
                .isInstanceOf(TripNotFoundException.class);
    }

    @Test
    void updateTrip_ThrowsUnauthorizedWhenNotAdmin() {
        setUpSecurityContext(2L);
        GroupMember regularMember = new GroupMember();
        regularMember.setRole(GroupRole.MEMBER);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(memberRepository.findByUserIdAndGroupId(2L, 1L)).thenReturn(Optional.of(regularMember));

        TripDTO dto = new TripDTO();
        assertThatThrownBy(() -> tripService.updateTrip(dto, 1L, 2L))
                .isInstanceOf(UnauthorizedGroupActionException.class);

        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    void updateTrip_ThrowsAccessDeniedWhenUserIdMismatch() {
        setUpSecurityContext(10L);

        TripDTO dto = new TripDTO();
        assertThatThrownBy(() -> tripService.updateTrip(dto, 1L, 1L))
                .isInstanceOf(AccessDeniedException.class);

        verify(tripRepository, never()).save(any(Trip.class));
    }

    // ---- deleteTrip ----

    @Test
    void deleteTrip_Success() {
        setUpSecurityContext(1L);
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(memberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(adminMember));

        tripService.deleteTrip(1L, 1L);

        verify(tripRepository).delete(trip);
    }

    @Test
    void deleteTrip_ThrowsTripNotFoundException() {
        setUpSecurityContext(1L);
        when(tripRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tripService.deleteTrip(99L, 1L))
                .isInstanceOf(TripNotFoundException.class);

        verify(tripRepository, never()).delete(any(Trip.class));
    }

    @Test
    void deleteTrip_ThrowsUnauthorizedWhenNotAdmin() {
        setUpSecurityContext(2L);
        GroupMember regularMember = new GroupMember();
        regularMember.setRole(GroupRole.MEMBER);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(memberRepository.findByUserIdAndGroupId(2L, 1L)).thenReturn(Optional.of(regularMember));

        assertThatThrownBy(() -> tripService.deleteTrip(1L, 2L))
                .isInstanceOf(UnauthorizedGroupActionException.class);

        verify(tripRepository, never()).delete(any(Trip.class));
    }

    @Test
    void deleteTrip_ThrowsAccessDeniedWhenUserIdMismatch() {
        setUpSecurityContext(10L);

        assertThatThrownBy(() -> tripService.deleteTrip(1L, 1L))
                .isInstanceOf(AccessDeniedException.class);

        verify(tripRepository, never()).delete(any(Trip.class));
    }
}
