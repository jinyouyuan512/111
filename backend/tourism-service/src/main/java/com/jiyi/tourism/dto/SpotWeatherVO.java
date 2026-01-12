package com.jiyi.tourism.dto;

import lombok.Data;

/**
 * 景点天气VO
 */
@Data
public class SpotWeatherVO {
    private String spotName;
    private String date;
    private String condition;
    private TemperatureRange temperature;
    private Integer humidity;
    private String suggestion;
    private String windDirection;
    private String windPower;
    private Integer aqi;
    
    @Data
    public static class TemperatureRange {
        private Integer min;
        private Integer max;
    }
}
