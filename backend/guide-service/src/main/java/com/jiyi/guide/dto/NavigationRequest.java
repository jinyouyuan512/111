package com.jiyi.guide.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class NavigationRequest {
    private BigDecimal startLatitude;
    private BigDecimal startLongitude;
    private Long targetPoiId;
}
