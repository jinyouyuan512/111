package com.jiyi.tourism.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ItineraryCreateRequest {
    private Long routeId;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private String notes;
}
