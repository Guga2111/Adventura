package com.luisgosampaio.adventura.domain.excursion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExcursionMessage {

    private ExcursionAction action;
    private Long excursionId;
    private String name;
    private BigDecimal price;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<Long> presenceIds;
    private String updatedBy;
}
