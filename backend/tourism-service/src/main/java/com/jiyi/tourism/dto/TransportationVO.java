package com.jiyi.tourism.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransportationVO {
    private Long id;
    private Long fromAttractionId;
    private Long toAttractionId;
    private String type;
    private Integer duration;
    private BigDecimal cost;
    private String description;
}
