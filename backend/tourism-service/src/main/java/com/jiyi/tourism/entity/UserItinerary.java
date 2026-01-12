package com.jiyi.tourism.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jiyi.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_itinerary")
public class UserItinerary extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private Long routeId;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String notes;
    
    @TableField(exist = false)
    private Route route;
}
