package com.jiyi.creative.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("creative_call")
public class CreativeCall {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title;
    private String description;
    private String requirements;
    private BigDecimal budget;
    private LocalDateTime deadline;
    private String status; // open, closed, completed
    private Long publisherId;
    private String publisherType; // platform, enterprise
    private Integer submissionCount;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    private Integer deleted;
}
