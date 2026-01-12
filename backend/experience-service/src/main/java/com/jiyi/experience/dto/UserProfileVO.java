package com.jiyi.experience.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

/**
 * 用户体验馆档案视图对象
 */
@Data
public class UserProfileVO {
    
    private Long userId;
    
    /** 总积分 */
    private Integer totalPoints;
    
    /** 等级 */
    private Integer level;
    
    /** 等级名称 */
    private String levelName;
    
    /** 当前经验值 */
    private Integer exp;
    
    /** 升级所需经验值 */
    private Integer expToNextLevel;
    
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
    
    /** 今日是否已打卡 */
    private Boolean checkedInToday;
    
    /** 获得的成就数量 */
    private Integer achievementCount;
    
    /** 最近获得的成就 */
    private List<AchievementVO> recentAchievements;
}
