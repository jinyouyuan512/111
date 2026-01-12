package com.jiyi.tourism.dto;

import lombok.Data;
import java.util.List;

/**
 * 路线优化请求
 */
@Data
public class RouteOptimizeRequest {
    private List<String> spots;
    private String startPoint;
    private String optimizeFor;  // distance, time, scenic
}
