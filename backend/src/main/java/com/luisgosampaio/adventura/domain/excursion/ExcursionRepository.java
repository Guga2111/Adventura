package com.luisgosampaio.adventura.domain.excursion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExcursionRepository extends JpaRepository<Excursion, Long> {

    List<Excursion> findByTripIdOrderByStartDateAsc(Long tripId);

    int countByTripId(Long tripId);
}
