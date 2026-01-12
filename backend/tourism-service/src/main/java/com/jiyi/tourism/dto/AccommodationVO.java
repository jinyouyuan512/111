package com.jiyi.tourism.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccommodationVO {
    private Long id;
    private Integer dayNum;
    private String name;
    private String type;
    private String address;
    private String priceRange;
    private BigDecimal rating;
    private String contact;
}
