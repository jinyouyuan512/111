package com.jiyi.guide.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TrackPointRequest {
    private Long visitId;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
