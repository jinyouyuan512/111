package com.jiyi.guide.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class GuideRouteVO {
    private Long id;
    private String name;
    private String description;
    private Integer duration;
    private BigDecimal distance;
    private String difficulty;
    private String pathData;
    private List<PoiVO> pois;
}
