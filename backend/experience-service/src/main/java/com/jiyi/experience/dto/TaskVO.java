package com.jiyi.experience.dto;

import lombok.Data;

/**
 * 任务视图对象
 */
@Data
public class TaskVO {
    
    private Long id;
    private Long sceneId;
    private String name;
    private String description;
    private String type;
    private Integer targetValue;
    private Integer rewardPoints;
    private Integer timeLimit;
    
    /** 当前进度 */
    private Integer currentValue;
    
    /** 状态 0进行中 1已完成 2已领取奖励 */
    private Integer status;
    
    /** 进度百分比 */
    private Integer progressPercent;
}
