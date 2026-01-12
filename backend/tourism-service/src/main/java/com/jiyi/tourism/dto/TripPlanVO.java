package com.jiyi.tourism.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 行程规划VO
 */
@Data
public class TripPlanVO {
    private Long id;
    private String title;
    private String description;
    private List<DayPlan> days;
    private BigDecimal totalDistance;
    private BigDecimal estimatedCost;
    private List<String> tips;
    
    /**
     * 原始行程数据 (JSON格式，用于前端解析)
     */
    private String planData;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    @Data
    public static class DayPlan {
        private Integer day;
        private String date;
        private List<SpotPlan> spots;
        private List<String> meals;
        private String accommodation;
    }
    
    @Data
    public static class SpotPlan {
        private Integer order;
        private String name;
        private String duration;
        private String tips;
        private String arrivalTime;
        private String departureTime;
    }
}
