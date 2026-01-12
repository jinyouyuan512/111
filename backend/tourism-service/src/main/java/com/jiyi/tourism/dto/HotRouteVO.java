package com.jiyi.tourism.dto;

import lombok.Data;
import java.util.List;

/**
 * 热门路线VO
 */
@Data
public class HotRouteVO {
    private String id;
    private Integer rank;
    private String name;
    private List<Long> spots;
    private String duration;
    private Integer views;
    private Double rating;
}
