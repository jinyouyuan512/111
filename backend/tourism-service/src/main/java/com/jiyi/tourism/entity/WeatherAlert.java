package com.jiyi.tourism.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jiyi.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("weather_alert")
public class WeatherAlert extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long attractionId;
    private String alertType;
    private String severity;
    private String title;
    private String content;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
