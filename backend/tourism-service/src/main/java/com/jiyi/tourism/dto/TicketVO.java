package com.jiyi.tourism.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 门票VO
 */
@Data
public class TicketVO {
    private Long id;
    private String name;
    private String icon;
    private String gradient;
    private String address;
    private String openTime;
    private Double rating;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String discount;
    private Boolean needReserve;
    private Integer sold;
    private Integer stock;
    private String description;
}
