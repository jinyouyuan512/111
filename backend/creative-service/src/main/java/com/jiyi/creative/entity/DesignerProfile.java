package com.jiyi.creative.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("designer_profile")
public class DesignerProfile {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private String realName;
    private String bio;
    private String skills; // JSON array
    private String portfolioUrl;
    private Integer experienceYears;
    private BigDecimal rating;
    private Integer completedProjects;
    private Integer verified;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
