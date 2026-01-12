package com.jiyi.experience.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 成就视图对象
 */
@Data
public class AchievementVO {
    
    private Long id;
    private String code;
    private String name;
    private String description;
    private String icon;
    private String category;
    private Integer rewardPoints;
    private Integer rarity;
    
    /** 是否已解锁 */
    private Boolean unlocked;
    
    /** 解锁时间 */
    private LocalDateTime unlockTime;
    
    /** 当前进度 */
    private Integer currentProgress;
    
    /** 目标进度 */
    private Integer targetProgress;
}
