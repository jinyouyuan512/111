package com.jiyi.experience.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 任务定义实体
 */
@Data
@TableName("task")
public class Task {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 所属场景ID */
    private Long sceneId;
    
    /** 任务名称 */
    private String name;
    
    /** 任务描述 */
    private String description;
    
    /** 任务类型: quiz/explore/collect/challenge */
    private String type;
    
    /** 目标值 */
    private Integer targetValue;
    
    /** 奖励积分 */
    private Integer rewardPoints;
    
    /** 时间限制（秒） */
    private Integer timeLimit;
    
    /** 排序 */
    private Integer sortOrder;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableLogic
    private Integer deleted;
}
