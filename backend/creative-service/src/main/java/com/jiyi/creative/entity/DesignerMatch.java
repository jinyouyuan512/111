package com.jiyi.creative.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("designer_match")
public class DesignerMatch {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long requirementId;
    private Long designerId;
    private BigDecimal matchScore;
    private String status; // pending, accepted, rejected
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
