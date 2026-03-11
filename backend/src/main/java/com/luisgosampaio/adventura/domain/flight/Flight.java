package com.luisgosampaio.adventura.domain.flight;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.luisgosampaio.adventura.domain.trip.Trip;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "flight")
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    @JsonBackReference
    private Trip trip;

    @Column(name = "flight_number")
    private String flightNumber;

    @Column(name = "airline")
    private String airline;

    @Column(name = "origin_airport", length = 3)
    private String originAirport;

    @Column(name = "destination_airport", length = 3)
    private String destinationAirport;

    @Column(name = "departure_local_time", nullable = false)
    private LocalDateTime departureLocalTime;

    @Column(name = "departure_timezone", nullable = false)
    private String departureTimezone;

    @Column(name = "arrival_local_time", nullable = false)
    private LocalDateTime arrivalLocalTime;

    @Column(name = "arrival_timezone", nullable = false)
    private String arrivalTimezone;

    @Column(name = "booking_reference")
    private String bookingReference;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<FlightPassenger> passengers = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
