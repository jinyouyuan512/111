package com.jiyi.tourism.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class ElectronicItinerary {
    private String itineraryNo;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalDays;
    private BigDecimal totalCost;
    private List<DailyPlan> dailyPlans;
    private List<String> importantNotes;
    private String qrCode;
    
    @Data
    public static class DailyPlan {
        private Integer dayNum;
        private LocalDate date;
        private List<ActivityItem> activities;
        private String accommodation;
        private BigDecimal dailyCost;
    }
    
    @Data
    public static class ActivityItem {
        private String time;
        private String type; // attraction, meal, transportation
        private String name;
        private String location;
        private BigDecimal cost;
        private String notes;
    }
}
