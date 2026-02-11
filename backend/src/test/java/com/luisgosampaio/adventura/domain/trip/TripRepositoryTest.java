package com.luisgosampaio.adventura.domain.trip;

import com.luisgosampaio.adventura.domain.group.Group;
import com.luisgosampaio.adventura.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TripRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TripRepository tripRepository;

    private Group group;
    private Group otherGroup;
    private Trip earlyTrip;
    private Trip lateTrip;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("password123");
        entityManager.persistAndFlush(user);

        group = new Group();
        group.setName("Travel Group");
        group.setDescription("A travel group");
        group.setCreatedBy(user);
        entityManager.persistAndFlush(group);

        otherGroup = new Group();
        otherGroup.setName("Other Group");
        otherGroup.setDescription("Another group");
        otherGroup.setCreatedBy(user);
        entityManager.persistAndFlush(otherGroup);

        earlyTrip = new Trip();
        earlyTrip.setGroup(group);
        earlyTrip.setDestiny("Tokyo");
        earlyTrip.setDescription("Trip to Japan");
        earlyTrip.setStartDate(LocalDate.of(2026, 3, 1));
        earlyTrip.setEndDate(LocalDate.of(2026, 3, 15));
        earlyTrip.setMainCurrency("BRL");
        earlyTrip.setTotalBudget(new BigDecimal("10000.00"));
        earlyTrip.setStatus(TripStatus.PLANNING);
        entityManager.persistAndFlush(earlyTrip);

        lateTrip = new Trip();
        lateTrip.setGroup(group);
        lateTrip.setDestiny("Paris");
        lateTrip.setDescription("Trip to France");
        lateTrip.setStartDate(LocalDate.of(2026, 8, 1));
        lateTrip.setEndDate(LocalDate.of(2026, 8, 15));
        lateTrip.setMainCurrency("EUR");
        lateTrip.setTotalBudget(new BigDecimal("20000.00"));
        lateTrip.setStatus(TripStatus.CONFIRMED);
        entityManager.persistAndFlush(lateTrip);
    }

    @Test
    void findByGroupId_ReturnsTripsList() {
        List<Trip> trips = tripRepository.findByGroupId(group.getId());

        assertThat(trips).hasSize(2);
    }

    @Test
    void findByGroupId_ReturnsEmpty_WhenNoTrips() {
        List<Trip> trips = tripRepository.findByGroupId(otherGroup.getId());

        assertThat(trips).isEmpty();
    }

    @Test
    void findByGroupIdOrderByStartDateDesc_ReturnsOrderedList() {
        List<Trip> trips = tripRepository.findByGroupIdOrderByStartDateDesc(group.getId());

        assertThat(trips).hasSize(2);
        assertThat(trips.get(0).getDestiny()).isEqualTo("Paris");
        assertThat(trips.get(1).getDestiny()).isEqualTo("Tokyo");
    }

    @Test
    void findByGroupIdOrderByStartDateDesc_ReturnsEmpty_WhenNoTrips() {
        List<Trip> trips = tripRepository.findByGroupIdOrderByStartDateDesc(otherGroup.getId());

        assertThat(trips).isEmpty();
    }
}