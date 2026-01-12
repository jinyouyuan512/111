package com.jiyi.social.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户徽章实体
 */
@Data
@TableName("user_badge")
public class UserBadge {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 徽章ID
     */
    private Long badgeId;
    
    /**
     * 获得时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime obtainedAt;
}
