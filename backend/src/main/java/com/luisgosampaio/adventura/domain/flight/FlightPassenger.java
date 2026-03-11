package com.luisgosampaio.adventura.domain.flight;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.luisgosampaio.adventura.domain.group.GroupMember;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "flight_passenger")
public class FlightPassenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    @JsonBackReference
    private Flight flight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_member_id", nullable = false)
    @JsonIgnoreProperties({"group"})
    private GroupMember groupMember;

    @Size(max = 6)
    @Column(name = "seat_number", length = 6)
    private String seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_class", length = 20)
    private SeatClass seatClass;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private FlightPassengerStatus status = FlightPassengerStatus.UNDECIDED;
}
