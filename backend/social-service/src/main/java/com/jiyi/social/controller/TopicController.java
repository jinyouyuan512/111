package com.jiyi.social.controller;

import com.jiyi.common.result.Result;
import com.jiyi.social.dto.TopicVO;
import com.jiyi.social.dto.UserStatsVO;
import com.jiyi.social.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "话题管理")
@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicController {
    private final TopicService topicService;
    
    @Operation(summary = "获取热门话题")
    @GetMapping("/hot")
    public Result<List<TopicVO>> getHotTopics(@RequestParam(defaultValue = "10") Integer limit) {
        List<TopicVO> topics = topicService.getHotTopics(limit);
        return Result.success(topics);
    }
    
    @Operation(summary = "获取活跃用户")
    @GetMapping("/users/active")
    public Result<List<UserStatsVO>> getActiveUsers(@RequestParam(defaultValue = "10") Integer limit) {
        List<UserStatsVO> users = topicService.getActiveUsers(limit);
        return Result.success(users);
    }
    
    @Operation(summary = "搜索话题")
    @GetMapping("/search")
    public Result<List<TopicVO>> searchTopics(@RequestParam String keyword) {
        List<TopicVO> topics = topicService.searchTopics(keyword);
        return Result.success(topics);
    }
}
