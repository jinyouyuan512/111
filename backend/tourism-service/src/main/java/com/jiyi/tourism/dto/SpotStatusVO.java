package com.jiyi.tourism.dto;

import lombok.Data;
import java.util.List;

/**
 * 景区实时状态VO
 */
@Data
public class SpotStatusVO {
    private Long spotId;
    private String name;
    private Boolean isOpen;
    private String openTime;
    private String closeTime;
    private Integer currentVisitors;
    private Integer maxCapacity;
    private String crowdLevel;
    private Integer waitTime;
    private List<String> notices;
    private SpotWeatherVO weather;
}
