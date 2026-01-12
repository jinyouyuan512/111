package com.jiyi.guide.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ScenicAreaVO {
    private Long id;
    private String name;
    private String description;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String mapData;
    private String status;
    private List<PoiVO> pois;
}
