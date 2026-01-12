package com.jiyi.tourism.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户行程规划实体（n8n保存的AI行程）
 */
@Data
@TableName("user_trip_plan")
public class UserTripPlan {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String title;
    
    private String description;
    
    /**
     * 行程详细数据 (JSON格式)
     */
    private String planData;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private BigDecimal totalDistance;
    
    private BigDecimal estimatedCost;
    
    private String status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
