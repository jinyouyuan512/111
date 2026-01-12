package com.jiyi.experience.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户任务进度实体
 */
@Data
@TableName("user_task")
public class UserTask {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 用户ID */
    private Long userId;
    
    /** 任务ID */
    private Long taskId;
    
    /** 当前进度值 */
    private Integer currentValue;
    
    /** 状态 0进行中 1已完成 2已领取奖励 */
    private Integer status;
    
    /** 开始时间 */
    private LocalDateTime startTime;
    
    /** 完成时间 */
    private LocalDateTime completeTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
