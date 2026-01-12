package com.jiyi.tourism.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RouteVO {
    private Long id;
    private String name;
    private String description;
    private Integer days;
    private BigDecimal estimatedCost;
    private String difficulty;
    private String season;
    private List<String> tags;
    private String coverImage;
    private Integer viewCount;
    private Integer bookingCount;
    private LocalDateTime createdAt;
    private List<AttractionVO> attractions;
    private List<TransportationVO> transportations;
    private List<AccommodationVO> accommodations;
    private Double recommendationScore; // 推荐分数
}
