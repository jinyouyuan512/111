package com.jiyi.experience.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jiyi.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户体验进度实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_progress")
public class UserProgress extends BaseEntity {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 场景ID
     */
    private Long sceneId;
    
    /**
     * 完成进度（0-100）
     */
    private Integer progress;
    
    /**
     * 已完成的交互点ID列表（逗号分隔）
     */
    private String completedInteractions;
    
    /**
     * 体验开始时间
     */
    private java.time.LocalDateTime startTime;
    
    /**
     * 最后更新时间
     */
    private java.time.LocalDateTime lastUpdateTime;
    
    /**
     * 是否已完成
     */
    private Boolean completed;
}
