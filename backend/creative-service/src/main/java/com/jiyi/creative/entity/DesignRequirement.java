package com.jiyi.creative.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("design_requirement")
public class DesignRequirement {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long enterpriseId;
    private String title;
    private String description;
    private String requirements;
    private BigDecimal budget;
    private LocalDateTime deadline;
    private String status; // open, matched, in_progress, completed, cancelled
    private Long matchedDesignerId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
