package com.jiyi.experience.service;

import com.jiyi.experience.dto.*;
import java.util.List;

/**
 * 游戏化服务接口
 */
public interface GameService {
    
    // ==================== 用户档案 ====================
    
    /**
     * 获取用户体验馆档案
     */
    UserProfileVO getUserProfile(Long userId);
    
    /**
     * 用户打卡
     */
    UserProfileVO checkin(Long userId);
    
    // ==================== 成就系统 ====================
    
    /**
     * 获取所有成就列表（含用户解锁状态）
     */
    List<AchievementVO> getAchievements(Long userId);
    
    /**
     * 检查并解锁成就
     */
    List<AchievementVO> checkAndUnlockAchievements(Long userId);
    
    // ==================== 任务系统 ====================
    
    /**
     * 获取场景任务列表
     */
    List<TaskVO> getSceneTasks(Long sceneId, Long userId);
    
    /**
     * 更新任务进度
     */
    TaskVO updateTaskProgress(Long userId, Long taskId, Integer progressDelta);
    
    /**
     * 领取任务奖励
     */
    Integer claimTaskReward(Long userId, Long taskId);
    
    // ==================== 答题系统 ====================
    
    /**
     * 获取场景题目列表
     */
    List<QuizVO> getSceneQuizzes(Long sceneId);
    
    /**
     * 提交答案
     */
    QuizVO answerQuiz(AnswerQuizRequest request);
    
    // ==================== 排行榜 ====================
    
    /**
     * 获取排行榜
     */
    LeaderboardVO getLeaderboard(String period, Long currentUserId);
    
    // ==================== 积分操作 ====================
    
    /**
     * 增加积分
     */
    void addPoints(Long userId, Integer points, String type, Long sourceId, String description);
}
