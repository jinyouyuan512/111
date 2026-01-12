package com.jiyi.experience.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户成就记录实体
 */
@Data
@TableName("user_achievement")
public class UserAchievement {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 用户ID */
    private Long userId;
    
    /** 成就ID */
    private Long achievementId;
    
    /** 解锁时间 */
    private LocalDateTime unlockTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
