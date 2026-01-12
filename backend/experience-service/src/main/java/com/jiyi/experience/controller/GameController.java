package com.jiyi.experience.controller;

import com.jiyi.experience.dto.*;
import com.jiyi.experience.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 游戏化功能控制器
 */
@RestController
@RequestMapping("/api/experience/game")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GameController {
    
    private final GameService gameService;
    
    // ==================== 用户档案 ====================
    
    /**
     * 获取用户体验馆档案
     */
    @GetMapping("/profile/{userId}")
    public ResponseEntity<UserProfileVO> getUserProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(gameService.getUserProfile(userId));
    }
    
    /**
     * 用户打卡
     */
    @PostMapping("/checkin/{userId}")
    public ResponseEntity<UserProfileVO> checkin(@PathVariable Long userId) {
        return ResponseEntity.ok(gameService.checkin(userId));
    }
    
    // ==================== 成就系统 ====================
    
    /**
     * 获取成就列表
     */
    @GetMapping("/achievements")
    public ResponseEntity<List<AchievementVO>> getAchievements(
            @RequestParam(required = false) Long userId) {
        return ResponseEntity.ok(gameService.getAchievements(userId));
    }
    
    /**
     * 检查并解锁成就
     */
    @PostMapping("/achievements/check/{userId}")
    public ResponseEntity<List<AchievementVO>> checkAchievements(@PathVariable Long userId) {
        return ResponseEntity.ok(gameService.checkAndUnlockAchievements(userId));
    }
    
    // ==================== 任务系统 ====================
    
    /**
     * 获取场景任务列表
     */
    @GetMapping("/tasks/scene/{sceneId}")
    public ResponseEntity<List<TaskVO>> getSceneTasks(
            @PathVariable Long sceneId,
            @RequestParam(required = false) Long userId) {
        return ResponseEntity.ok(gameService.getSceneTasks(sceneId, userId));
    }
    
    /**
     * 更新任务进度
     */
    @PostMapping("/tasks/{taskId}/progress")
    public ResponseEntity<TaskVO> updateTaskProgress(
            @PathVariable Long taskId,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") Integer delta) {
        return ResponseEntity.ok(gameService.updateTaskProgress(userId, taskId, delta));
    }
    
    /**
     * 领取任务奖励
     */
    @PostMapping("/tasks/{taskId}/claim")
    public ResponseEntity<Map<String, Object>> claimTaskReward(
            @PathVariable Long taskId,
            @RequestParam Long userId) {
        Integer points = gameService.claimTaskReward(userId, taskId);
        return ResponseEntity.ok(Map.of("success", true, "points", points));
    }
    
    // ==================== 答题系统 ====================
    
    /**
     * 获取场景题目列表
     */
    @GetMapping("/quizzes/scene/{sceneId}")
    public ResponseEntity<List<QuizVO>> getSceneQuizzes(@PathVariable Long sceneId) {
        return ResponseEntity.ok(gameService.getSceneQuizzes(sceneId));
    }
    
    /**
     * 提交答案
     */
    @PostMapping("/quizzes/answer")
    public ResponseEntity<QuizVO> answerQuiz(@RequestBody AnswerQuizRequest request) {
        return ResponseEntity.ok(gameService.answerQuiz(request));
    }
    
    // ==================== 排行榜 ====================
    
    /**
     * 获取排行榜
     */
    @GetMapping("/leaderboard")
    public ResponseEntity<LeaderboardVO> getLeaderboard(
            @RequestParam(defaultValue = "all") String period,
            @RequestParam(required = false) Long userId) {
        return ResponseEntity.ok(gameService.getLeaderboard(period, userId));
    }
}
