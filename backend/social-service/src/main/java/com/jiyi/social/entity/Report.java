package com.jiyi.social.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 内容举报实体
 */
@Data
@TableName("report")
public class Report {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 举报者ID
     */
    private Long reporterId;
    
    /**
     * 目标类型: post, comment, user
     */
    private String targetType;
    
    /**
     * 目标ID
     */
    private Long targetId;
    
    /**
     * 举报原因
     */
    private String reason;
    
    /**
     * 详细说明
     */
    private String description;
    
    /**
     * 状态: pending, processing, resolved, rejected
     */
    private String status;
    
    /**
     * 处理人ID
     */
    private Long handlerId;
    
    /**
     * 处理结果
     */
    private String handleResult;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    /**
     * 处理时间
     */
    private LocalDateTime handledAt;
}
