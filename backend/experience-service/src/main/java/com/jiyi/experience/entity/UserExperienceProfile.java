package com.jiyi.experience.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户体验馆数据实体
 */
@Data
@TableName("user_experience_profile")
public class UserExperienceProfile {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 用户ID */
    private Long userId;
    
    /** 总积分 */
    private Integer totalPoints;
    
    /** 等级 */
    private Integer level;
    
    /** 当前经验值 */
    private Integer exp;
    
    /** 完成场景数 */
    private Integer scenesCompleted;
    
    /** 完成任务数 */
    private Integer tasksCompleted;
    
    /** 答对题目数 */
    private Integer quizzesCorrect;
    
    /** 总体验时长（分钟） */
    private Integer totalTimeSpent;
    
    /** 连续打卡天数 */
    private Integer streakDays;
    
    /** 最后打卡日期 */
    private LocalDate lastCheckinDate;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
