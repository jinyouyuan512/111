package com.jiyi.tourism.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 红色景点VO
 */
@Data
public class RedSpotVO {
    private Long id;
    private String name;
    private String icon;
    private String gradient;
    private String slogan;
    private String location;
    private BigDecimal rating;
    private Boolean isFree;
    private Boolean needReserve;
    private List<String> tags;
    private String introduction;
    private String history;
    private List<String> tips;
    private List<AudioGuideVO> audioGuides;
    private String category;
    private BigDecimal ticketPrice;
    private String openingHours;
    private List<String> images;
}
