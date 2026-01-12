package com.jiyi.social.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 私信实体
 */
@Data
@TableName("private_message")
public class PrivateMessage {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 发送者ID
     */
    private Long fromUserId;
    
    /**
     * 接收者ID
     */
    private Long toUserId;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 类型: text, image, video
     */
    private String type;
    
    /**
     * 媒体URL
     */
    private String mediaUrl;
    
    /**
     * 已读状态: 0-未读, 1-已读
     */
    private Integer readStatus;
    
    /**
     * 阅读时间
     */
    private LocalDateTime readAt;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
