package com.jiyi.tourism.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ItineraryVO {
    private Long id;
    private Long userId;
    private Long routeId;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String notes;
    private LocalDateTime createdAt;
    private RouteVO route;
}
