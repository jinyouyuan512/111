package com.jiyi.tourism.controller;

import com.jiyi.common.result.Result;
import com.jiyi.tourism.dto.*;
import com.jiyi.tourism.service.SpotGuideService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 景点导览控制器
 * 提供红色景点信息、语音讲解等功能
 */
@Slf4j
@RestController
@RequestMapping("/api/tourism/spots")
@RequiredArgsConstructor
public class SpotGuideController {
    
    private final SpotGuideService spotGuideService;
    
    /**
     * 获取所有红色景点
     */
    @GetMapping("/red")
    public Result<List<RedSpotVO>> getRedSpots() {
        log.info("Getting all red spots");
        return Result.success(spotGuideService.getRedSpots());
    }
    
    /**
     * 获取景点详情
     */
    @GetMapping("/{spotId}")
    public Result<RedSpotVO> getSpotDetail(@PathVariable Long spotId) {
        log.info("Getting spot detail: {}", spotId);
        return Result.success(spotGuideService.getSpotDetail(spotId));
    }
    
    /**
     * 获取景点语音讲解列表
     */
    @GetMapping("/{spotId}/audio-guides")
    public Result<List<AudioGuideVO>> getAudioGuides(@PathVariable Long spotId) {
        log.info("Getting audio guides for spot: {}", spotId);
        return Result.success(spotGuideService.getAudioGuides(spotId));
    }
    
    /**
     * 获取语音讲解详情
     */
    @GetMapping("/audio-guides/{guideId}")
    public Result<AudioGuideVO> getAudioGuideDetail(@PathVariable Long guideId) {
        log.info("Getting audio guide detail: {}", guideId);
        return Result.success(spotGuideService.getAudioGuideDetail(guideId));
    }
    
    /**
     * 搜索景点
     */
    @GetMapping("/search")
    public Result<List<RedSpotVO>> searchSpots(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean freeOnly) {
        log.info("Searching spots: keyword={}, category={}, freeOnly={}", keyword, category, freeOnly);
        return Result.success(spotGuideService.searchSpots(keyword, category, freeOnly));
    }
}
