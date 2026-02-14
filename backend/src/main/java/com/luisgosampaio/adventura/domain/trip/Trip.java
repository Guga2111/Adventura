package com.luisgosampaio.adventura.domain.trip;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.luisgosampaio.adventura.domain.group.Group;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table (name = "trip")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    @JsonIgnoreProperties({"members"})
    private Group group;

    @ElementCollection
    @CollectionTable(name = "trip_destinations", joinColumns = @JoinColumn(name = "trip_id"))
    @Column(name = "destination")
    private List<String> destinations = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "trip_country_codes", joinColumns = @JoinColumn(name = "trip_id"))
    @Column(name = "country_code")
    private List<String> countryCodes = new ArrayList<>();

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "excursions_count")
    private int excursionCount;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "main_currency",length = 3)
    private String mainCurrency = "BRL";

    @Column(name = "total_budget", precision = 12, scale = 2)
    private BigDecimal totalBudget;

    @Enumerated(EnumType.STRING)
    @Column(name = "status",length = 20)
    private TripStatus status = TripStatus.PLANNING;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate () {
        updatedAt = LocalDateTime.now();
    }
}
