package com.jiyi.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户关注关系
 */
@Data
@TableName("user_follow")
public class UserFollow {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 关注者ID
     */
    private Long followerId;
    
    /**
     * 被关注者ID
     */
    private Long followingId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
