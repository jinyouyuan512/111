package com.jiyi.experience.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiyi.experience.dto.*;
import com.jiyi.experience.entity.*;
import com.jiyi.experience.mapper.*;
import com.jiyi.experience.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
    
    private final UserExperienceProfileMapper profileMapper;
    private final AchievementMapper achievementMapper;
    private final UserAchievementMapper userAchievementMapper;
    private final TaskMapper taskMapper;
    private final UserTaskMapper userTaskMapper;
    private final QuizMapper quizMapper;
    private final ObjectMapper objectMapper;
    
    // 等级经验值配置
    private static final int[] LEVEL_EXP = {0, 100, 300, 600, 1000, 1500, 2100, 2800, 3600, 4500, 5500};
    private static final String[] LEVEL_NAMES = {"新手探索者", "初级学员", "红色传承者", "历史研究员", 
            "文化守护者", "红途先锋", "革命精神传人", "红色文化大使", "历史见证者", "红途大师", "传奇探索家"};
    
    @Override
    public UserProfileVO getUserProfile(Long userId) {
        UserExperienceProfile profile = getOrCreateProfile(userId);
        
        UserProfileVO vo = new UserProfileVO();
        vo.setUserId(userId);
        vo.setTotalPoints(profile.getTotalPoints());
        vo.setLevel(profile.getLevel());
        vo.setLevelName(getLevelName(profile.getLevel()));
        vo.setExp(profile.getExp());
        vo.setExpToNextLevel(getExpToNextLevel(profile.getLevel()));
        vo.setScenesCompleted(profile.getScenesCompleted());
        vo.setTasksCompleted(profile.getTasksCompleted());
        vo.setQuizzesCorrect(profile.getQuizzesCorrect());
        vo.setTotalTimeSpent(profile.getTotalTimeSpent());
        vo.setStreakDays(profile.getStreakDays());
        vo.setLastCheckinDate(profile.getLastCheckinDate());
        vo.setCheckedInToday(LocalDate.now().equals(profile.getLastCheckinDate()));
        
        // 获取成就数量
        LambdaQueryWrapper<UserAchievement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAchievement::getUserId, userId);
        vo.setAchievementCount(userAchievementMapper.selectCount(wrapper).intValue());
        
        // 获取最近成就
        wrapper.orderByDesc(UserAchievement::getUnlockTime).last("LIMIT 3");
        List<UserAchievement> recentUnlocks = userAchievementMapper.selectList(wrapper);
        List<AchievementVO> recentAchievements = recentUnlocks.stream()
                .map(ua -> {
                    Achievement a = achievementMapper.selectById(ua.getAchievementId());
                    AchievementVO avo = new AchievementVO();
                    BeanUtils.copyProperties(a, avo);
                    avo.setUnlocked(true);
                    avo.setUnlockTime(ua.getUnlockTime());
                    return avo;
                })
                .collect(Collectors.toList());
        vo.setRecentAchievements(recentAchievements);
        
        return vo;
    }
    
    @Override
    @Transactional
    public UserProfileVO checkin(Long userId) {
        UserExperienceProfile profile = getOrCreateProfile(userId);
        LocalDate today = LocalDate.now();
        
        if (today.equals(profile.getLastCheckinDate())) {
            // 今天已打卡
            return getUserProfile(userId);
        }
        
        // 计算连续打卡
        if (profile.getLastCheckinDate() != null && 
            profile.getLastCheckinDate().plusDays(1).equals(today)) {
            profile.setStreakDays(profile.getStreakDays() + 1);
        } else {
            profile.setStreakDays(1);
        }
        
        profile.setLastCheckinDate(today);
        profileMapper.updateById(profile);
        
        // 打卡奖励积分
        int bonusPoints = Math.min(10 + profile.getStreakDays() * 2, 50);
        addPoints(userId, bonusPoints, "checkin", null, "每日打卡奖励");
        
        // 检查打卡成就
        checkAndUnlockAchievements(userId);
        
        return getUserProfile(userId);
    }
    
    @Override
    public List<AchievementVO> getAchievements(Long userId) {
        List<Achievement> achievements = achievementMapper.selectList(
                new LambdaQueryWrapper<Achievement>().orderByAsc(Achievement::getSortOrder));
        
        // 获取用户已解锁的成就
        Set<Long> unlockedIds = new HashSet<>();
        Map<Long, LocalDateTime> unlockTimes = new HashMap<>();
        if (userId != null) {
            LambdaQueryWrapper<UserAchievement> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserAchievement::getUserId, userId);
            userAchievementMapper.selectList(wrapper).forEach(ua -> {
                unlockedIds.add(ua.getAchievementId());
                unlockTimes.put(ua.getAchievementId(), ua.getUnlockTime());
            });
        }
        
        UserExperienceProfile profile = userId != null ? getOrCreateProfile(userId) : null;
        
        return achievements.stream().map(a -> {
            AchievementVO vo = new AchievementVO();
            BeanUtils.copyProperties(a, vo);
            vo.setUnlocked(unlockedIds.contains(a.getId()));
            vo.setUnlockTime(unlockTimes.get(a.getId()));
            vo.setTargetProgress(a.getConditionValue());
            vo.setCurrentProgress(profile != null ? getCurrentProgress(profile, a.getConditionType()) : 0);
            return vo;
        }).collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public List<AchievementVO> checkAndUnlockAchievements(Long userId) {
        UserExperienceProfile profile = getOrCreateProfile(userId);
        List<Achievement> allAchievements = achievementMapper.selectList(null);
        
        // 获取已解锁的成就ID
        Set<Long> unlockedIds = userAchievementMapper.selectList(
                new LambdaQueryWrapper<UserAchievement>().eq(UserAchievement::getUserId, userId))
                .stream().map(UserAchievement::getAchievementId).collect(Collectors.toSet());
        
        List<AchievementVO> newlyUnlocked = new ArrayList<>();
        
        for (Achievement a : allAchievements) {
            if (unlockedIds.contains(a.getId())) continue;
            
            int currentProgress = getCurrentProgress(profile, a.getConditionType());
            if (currentProgress >= a.getConditionValue()) {
                // 解锁成就
                UserAchievement ua = new UserAchievement();
                ua.setUserId(userId);
                ua.setAchievementId(a.getId());
                ua.setUnlockTime(LocalDateTime.now());
                userAchievementMapper.insert(ua);
                
                // 奖励积分
                if (a.getRewardPoints() > 0) {
                    addPoints(userId, a.getRewardPoints(), "achievement", a.getId(), "解锁成就: " + a.getName());
                }
                
                AchievementVO vo = new AchievementVO();
                BeanUtils.copyProperties(a, vo);
                vo.setUnlocked(true);
                vo.setUnlockTime(ua.getUnlockTime());
                newlyUnlocked.add(vo);
            }
        }
        
        return newlyUnlocked;
    }
    
    @Override
    public List<TaskVO> getSceneTasks(Long sceneId, Long userId) {
        List<Task> tasks = taskMapper.selectList(
                new LambdaQueryWrapper<Task>()
                        .eq(Task::getSceneId, sceneId)
                        .orderByAsc(Task::getSortOrder));
        
        Map<Long, UserTask> userTaskMap = new HashMap<>();
        if (userId != null) {
            List<Long> taskIds = tasks.stream().map(Task::getId).collect(Collectors.toList());
            if (!taskIds.isEmpty()) {
                userTaskMapper.selectList(
                        new LambdaQueryWrapper<UserTask>()
                                .eq(UserTask::getUserId, userId)
                                .in(UserTask::getTaskId, taskIds))
                        .forEach(ut -> userTaskMap.put(ut.getTaskId(), ut));
            }
        }
        
        return tasks.stream().map(t -> {
            TaskVO vo = new TaskVO();
            BeanUtils.copyProperties(t, vo);
            
            UserTask ut = userTaskMap.get(t.getId());
            if (ut != null) {
                vo.setCurrentValue(ut.getCurrentValue());
                vo.setStatus(ut.getStatus());
            } else {
                vo.setCurrentValue(0);
                vo.setStatus(0);
            }
            vo.setProgressPercent(t.getTargetValue() > 0 ? 
                    Math.min(100, vo.getCurrentValue() * 100 / t.getTargetValue()) : 0);
            
            return vo;
        }).collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public TaskVO updateTaskProgress(Long userId, Long taskId, Integer progressDelta) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) throw new RuntimeException("任务不存在");
        
        UserTask userTask = userTaskMapper.selectOne(
                new LambdaQueryWrapper<UserTask>()
                        .eq(UserTask::getUserId, userId)
                        .eq(UserTask::getTaskId, taskId));
        
        if (userTask == null) {
            userTask = new UserTask();
            userTask.setUserId(userId);
            userTask.setTaskId(taskId);
            userTask.setCurrentValue(0);
            userTask.setStatus(0);
            userTask.setStartTime(LocalDateTime.now());
            userTaskMapper.insert(userTask);
        }
        
        if (userTask.getStatus() >= 1) {
            // 任务已完成
            TaskVO vo = new TaskVO();
            BeanUtils.copyProperties(task, vo);
            vo.setCurrentValue(userTask.getCurrentValue());
            vo.setStatus(userTask.getStatus());
            vo.setProgressPercent(100);
            return vo;
        }
        
        userTask.setCurrentValue(userTask.getCurrentValue() + progressDelta);
        
        if (userTask.getCurrentValue() >= task.getTargetValue()) {
            userTask.setCurrentValue(task.getTargetValue());
            userTask.setStatus(1);
            userTask.setCompleteTime(LocalDateTime.now());
        }
        
        userTaskMapper.updateById(userTask);
        
        TaskVO vo = new TaskVO();
        BeanUtils.copyProperties(task, vo);
        vo.setCurrentValue(userTask.getCurrentValue());
        vo.setStatus(userTask.getStatus());
        vo.setProgressPercent(task.getTargetValue() > 0 ? 
                Math.min(100, userTask.getCurrentValue() * 100 / task.getTargetValue()) : 0);
        
        return vo;
    }
    
    @Override
    @Transactional
    public Integer claimTaskReward(Long userId, Long taskId) {
        Task task = taskMapper.selectById(taskId);
        UserTask userTask = userTaskMapper.selectOne(
                new LambdaQueryWrapper<UserTask>()
                        .eq(UserTask::getUserId, userId)
                        .eq(UserTask::getTaskId, taskId));
        
        if (userTask == null || userTask.getStatus() != 1) {
            throw new RuntimeException("任务未完成或奖励已领取");
        }
        
        userTask.setStatus(2);
        userTaskMapper.updateById(userTask);
        
        // 发放奖励
        addPoints(userId, task.getRewardPoints(), "task", taskId, "完成任务: " + task.getName());
        
        // 更新用户档案
        UserExperienceProfile profile = getOrCreateProfile(userId);
        profile.setTasksCompleted(profile.getTasksCompleted() + 1);
        profileMapper.updateById(profile);
        
        // 检查成就
        checkAndUnlockAchievements(userId);
        
        return task.getRewardPoints();
    }
    
    @Override
    public List<QuizVO> getSceneQuizzes(Long sceneId) {
        List<Quiz> quizzes = quizMapper.selectList(
                new LambdaQueryWrapper<Quiz>()
                        .eq(Quiz::getSceneId, sceneId)
                        .orderByAsc(Quiz::getSortOrder));
        
        return quizzes.stream().map(q -> {
            QuizVO vo = new QuizVO();
            vo.setId(q.getId());
            vo.setSceneId(q.getSceneId());
            vo.setQuestion(q.getQuestion());
            vo.setType(q.getType());
            vo.setPoints(q.getPoints());
            vo.setDifficulty(q.getDifficulty());
            
            // 解析选项
            try {
                vo.setOptions(objectMapper.readValue(q.getOptions(), new TypeReference<List<String>>() {}));
            } catch (Exception e) {
                vo.setOptions(Collections.emptyList());
            }
            
            return vo;
        }).collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public QuizVO answerQuiz(AnswerQuizRequest request) {
        Quiz quiz = quizMapper.selectById(request.getQuizId());
        if (quiz == null) throw new RuntimeException("题目不存在");
        
        boolean isCorrect = quiz.getAnswer().equalsIgnoreCase(request.getAnswer());
        
        QuizVO vo = new QuizVO();
        vo.setId(quiz.getId());
        vo.setSceneId(quiz.getSceneId());
        vo.setQuestion(quiz.getQuestion());
        vo.setType(quiz.getType());
        vo.setPoints(quiz.getPoints());
        vo.setUserAnswer(request.getAnswer());
        vo.setCorrectAnswer(quiz.getAnswer());
        vo.setIsCorrect(isCorrect);
        vo.setExplanation(quiz.getExplanation());
        
        try {
            vo.setOptions(objectMapper.readValue(quiz.getOptions(), new TypeReference<List<String>>() {}));
        } catch (Exception e) {
            vo.setOptions(Collections.emptyList());
        }
        
        if (isCorrect && request.getUserId() != null) {
            // 答对奖励积分
            addPoints(request.getUserId(), quiz.getPoints(), "quiz", quiz.getId(), "答对题目");
            
            // 更新用户档案
            UserExperienceProfile profile = getOrCreateProfile(request.getUserId());
            profile.setQuizzesCorrect(profile.getQuizzesCorrect() + 1);
            profileMapper.updateById(profile);
            
            // 检查成就
            checkAndUnlockAchievements(request.getUserId());
        }
        
        return vo;
    }
    
    @Override
    public LeaderboardVO getLeaderboard(String period, Long currentUserId) {
        // 简化实现：直接从用户档案表获取排行
        List<UserExperienceProfile> profiles = profileMapper.selectList(
                new LambdaQueryWrapper<UserExperienceProfile>()
                        .orderByDesc(UserExperienceProfile::getTotalPoints)
                        .last("LIMIT 50"));
        
        LeaderboardVO vo = new LeaderboardVO();
        vo.setPeriod(period);
        
        List<LeaderboardVO.RankItem> rankings = new ArrayList<>();
        int rank = 1;
        for (UserExperienceProfile p : profiles) {
            LeaderboardVO.RankItem item = new LeaderboardVO.RankItem();
            item.setRank(rank++);
            item.setUserId(p.getUserId());
            item.setUsername("用户" + p.getUserId());
            item.setAvatar(null);
            item.setTotalPoints(p.getTotalPoints());
            item.setLevel(p.getLevel());
            item.setLevelName(getLevelName(p.getLevel()));
            rankings.add(item);
            
            if (p.getUserId().equals(currentUserId)) {
                vo.setCurrentUserRank(item);
            }
        }
        vo.setRankings(rankings);
        
        return vo;
    }
    
    @Override
    @Transactional
    public void addPoints(Long userId, Integer points, String type, Long sourceId, String description) {
        UserExperienceProfile profile = getOrCreateProfile(userId);
        profile.setTotalPoints(profile.getTotalPoints() + points);
        profile.setExp(profile.getExp() + points);
        
        // 检查升级
        while (profile.getLevel() < LEVEL_EXP.length - 1 && 
               profile.getExp() >= LEVEL_EXP[profile.getLevel()]) {
            profile.setExp(profile.getExp() - LEVEL_EXP[profile.getLevel()]);
            profile.setLevel(profile.getLevel() + 1);
        }
        
        profileMapper.updateById(profile);
    }
    
    // ==================== 辅助方法 ====================
    
    private UserExperienceProfile getOrCreateProfile(Long userId) {
        UserExperienceProfile profile = profileMapper.selectOne(
                new LambdaQueryWrapper<UserExperienceProfile>().eq(UserExperienceProfile::getUserId, userId));
        
        if (profile == null) {
            profile = new UserExperienceProfile();
            profile.setUserId(userId);
            profile.setTotalPoints(0);
            profile.setLevel(1);
            profile.setExp(0);
            profile.setScenesCompleted(0);
            profile.setTasksCompleted(0);
            profile.setQuizzesCorrect(0);
            profile.setTotalTimeSpent(0);
            profile.setStreakDays(0);
            profileMapper.insert(profile);
        }
        
        return profile;
    }
    
    private String getLevelName(int level) {
        return level < LEVEL_NAMES.length ? LEVEL_NAMES[level - 1] : LEVEL_NAMES[LEVEL_NAMES.length - 1];
    }
    
    private int getExpToNextLevel(int level) {
        return level < LEVEL_EXP.length ? LEVEL_EXP[level] : LEVEL_EXP[LEVEL_EXP.length - 1];
    }
    
    private int getCurrentProgress(UserExperienceProfile profile, String conditionType) {
        switch (conditionType) {
            case "scenes_completed": return profile.getScenesCompleted();
            case "tasks_completed": return profile.getTasksCompleted();
            case "quizzes_correct": return profile.getQuizzesCorrect();
            case "total_points": return profile.getTotalPoints();
            case "streak_days": return profile.getStreakDays();
            default: return 0;
        }
    }
}
