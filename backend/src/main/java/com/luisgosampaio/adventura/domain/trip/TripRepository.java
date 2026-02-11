package com.luisgosampaio.adventura.domain.trip;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findByGroupId (Long groupId);

    List<Trip> findByGroupIdOrderByStartDateDesc (Long groupId);
}
