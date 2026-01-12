package com.jiyi.tourism.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AttractionVO {
    private Long id;
    private String name;
    private String category;
    private String description;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer visitDuration;
    private BigDecimal ticketPrice;
    private String openingHours;
    private List<String> images;
    private BigDecimal rating;
    private String status;
    private Integer dayNum;
    private Integer orderNum;
    private String visitTime;
    private String notes;
}
