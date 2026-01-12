package com.jiyi.tourism.controller;

import com.jiyi.common.result.Result;
import com.jiyi.tourism.dto.*;
import com.jiyi.tourism.service.SmartRouteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 智能路线控制器
 * 提供AI行程规划、热门路线推荐等功能
 */
@Slf4j
@RestController
@RequestMapping("/api/tourism/smart-route")
@RequiredArgsConstructor
public class SmartRouteController {
    
    private final SmartRouteService smartRouteService;
    
    /**
     * 获取热门路线
     */
    @GetMapping("/hot")
    public Result<List<HotRouteVO>> getHotRoutes(
            @RequestParam(defaultValue = "10") Integer limit) {
        log.info("Getting hot routes, limit: {}", limit);
        return Result.success(smartRouteService.getHotRoutes(limit));
    }
    
    /**
     * AI生成行程规划
     */
    @PostMapping("/generate")
    public Result<TripPlanVO> generateTripPlan(@RequestBody TripPlanRequest request) {
        log.info("Generating trip plan: {}", request);
        return Result.success(smartRouteService.generateTripPlan(request));
    }
    
    /**
     * 保存行程规划
     */
    @PostMapping("/save")
    public Result<Long> saveTripPlan(
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId,
            @RequestBody TripPlanVO plan) {
        log.info("Saving trip plan for user: {}", userId);
        return Result.success(smartRouteService.saveTripPlan(userId, plan));
    }
    
    /**
     * 获取用户保存的行程
     */
    @GetMapping("/my-plans")
    public Result<List<TripPlanVO>> getMyPlans(
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId) {
        log.info("Getting plans for user: {}", userId);
        return Result.success(smartRouteService.getUserPlans(userId));
    }
    
    /**
     * 路线优化
     */
    @PostMapping("/optimize")
    public Result<TripPlanVO> optimizeRoute(@RequestBody RouteOptimizeRequest request) {
        log.info("Optimizing route: {}", request);
        return Result.success(smartRouteService.optimizeRoute(request));
    }
}
