package com.jiyi.creative.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("design")
public class Design {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long designerId;
    private Long contestId;
    private Long callId;
    private String title;
    private Integer categoryType;
    private String description;
    private String designConcept;
    private String files; // JSON array of file URLs
    private String coverImage;
    private String copyrightStatement;
    private String tags;
    private String status; // pending, approved, rejected, published
    private String rejectReason;
    private Integer votes;
    private Integer views;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    private Integer deleted;
}
