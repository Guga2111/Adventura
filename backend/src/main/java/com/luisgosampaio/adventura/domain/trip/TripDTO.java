package com.luisgosampaio.adventura.domain.trip;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TripDTO {

    private String destiny;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String mainCurrency;
    private BigDecimal totalBudget;
    private TripStatus status;

}