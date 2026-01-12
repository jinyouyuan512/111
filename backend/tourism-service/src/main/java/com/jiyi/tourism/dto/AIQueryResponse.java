package com.jiyi.tourism.dto;

import lombok.Data;
import java.util.List;

/**
 * AI查询响应
 */
@Data
public class AIQueryResponse {
    private String answer;
    private String type; // text, route, spot_info, weather, food
    private List<String> suggestions; // 后续推荐问题
    private RouteRecommendation routeRecommendation;
    private List<SpotInfo> relatedSpots;
    private String sessionId;
    
    @Data
    public static class RouteRecommendation {
        private String title;
        private String description;
        private List<String> spots;
        private String duration;
        private Integer estimatedCost;
        private List<String> highlights;
    }
    
    @Data
    public static class SpotInfo {
        private Long id;
        private String name;
        private String description;
        private String imageUrl;
        private Double rating;
    }
}
