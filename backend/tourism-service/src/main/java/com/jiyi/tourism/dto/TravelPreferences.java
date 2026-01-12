package com.jiyi.tourism.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TravelPreferences {
    private String startLocation;
    private Integer days;
    private BigDecimal budget;
    private List<String> interests;
    private String difficulty;
    private String season;
}
