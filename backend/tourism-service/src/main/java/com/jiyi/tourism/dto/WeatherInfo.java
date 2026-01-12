package com.jiyi.tourism.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class WeatherInfo {
    private LocalDate date;
    private String weather;
    private String temperature;
    private String windDirection;
    private String windPower;
    private String humidity;
    private String suggestion;
}
