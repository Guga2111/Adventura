package com.luisgosampaio.adventura.domain.trip;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class TripDTO {

    private List<String> destinations;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String mainCurrency;
    private BigDecimal totalBudget;
    private TripStatus status;

}