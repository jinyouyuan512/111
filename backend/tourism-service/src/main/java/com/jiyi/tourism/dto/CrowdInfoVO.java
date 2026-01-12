package com.jiyi.tourism.dto;

import lombok.Data;

/**
 * 人流量信息VO
 */
@Data
public class CrowdInfoVO {
    private Long spotId;
    private String name;
    private String icon;
    private Integer percent;
    private String level;      // low, medium, high
    private String levelText;  // 人少, 适中, 较多
    private Integer waitTime;  // 等待时间(分钟)
    private String bestTime;   // 最佳游览时间
    private Integer currentVisitors;
    private Integer maxCapacity;
}
