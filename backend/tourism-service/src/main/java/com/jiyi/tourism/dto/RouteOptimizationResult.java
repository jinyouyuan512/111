package com.jiyi.tourism.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class RouteOptimizationResult {
    private List<Long> optimizedAttractionOrder;
    private BigDecimal totalDistance;
    private Integer totalDuration;
    private BigDecimal totalCost;
    private String optimizationStrategy;
}
