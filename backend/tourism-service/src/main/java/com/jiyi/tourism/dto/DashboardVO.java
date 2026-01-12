package com.jiyi.tourism.dto;

import lombok.Data;
import java.util.List;

/**
 * 仪表盘数据VO
 */
@Data
public class DashboardVO {
    private Integer todayVisitors;
    private Integer activeUsers;
    private Integer totalBookings;
    private List<PopularSpot> popularSpots;
    private List<Alert> recentAlerts;
    private SystemHealth systemHealth;
    
    @Data
    public static class PopularSpot {
        private String name;
        private Integer visits;
    }
    
    @Data
    public static class Alert {
        private String type;
        private String message;
        private String time;
    }
    
    @Data
    public static class SystemHealth {
        private String weatherApi;
        private String trafficApi;
        private String aiService;
        private String database;
    }
}
