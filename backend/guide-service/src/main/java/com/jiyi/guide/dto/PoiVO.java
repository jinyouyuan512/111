package com.jiyi.guide.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PoiVO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String type;
    private Integer triggerRadius;
    private String qrCode;
}
