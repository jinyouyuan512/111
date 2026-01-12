package com.jiyi.tourism.dto;

import lombok.Data;
import java.util.List;

/**
 * 行程规划请求
 */
@Data
public class TripPlanRequest {
    private List<String> spots;      // 选择的景点名称
    private String startDate;        // 出发日期
    private Integer duration;        // 行程天数
    private String pace;             // 行程节奏: relaxed, moderate, intensive
    private List<String> interests;  // 兴趣标签
    private Integer budget;          // 预算
}
