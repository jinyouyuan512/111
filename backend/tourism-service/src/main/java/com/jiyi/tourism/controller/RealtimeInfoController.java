package com.jiyi.tourism.controller;

import com.jiyi.common.result.Result;
import com.jiyi.tourism.dto.*;
import com.jiyi.tourism.service.RealtimeInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 实时信息控制器
 * 提供天气预报、人流量监控等实时数据
 */
@Slf4j
@RestController
@RequestMapping("/api/tourism/realtime")
@RequiredArgsConstructor
public class RealtimeInfoController {
    
    private final RealtimeInfoService realtimeInfoService;
    
    /**
     * 获取景点天气预报
     */
    @GetMapping("/weather")
    public Result<List<SpotWeatherVO>> getWeather(
            @RequestParam(required = false) List<String> spotNames) {
        log.info("Getting weather for spots: {}", spotNames);
        return Result.success(realtimeInfoService.getSpotWeather(spotNames));
    }
    
    /**
     * 获取景点人流量
     */
    @GetMapping("/crowd")
    public Result<List<CrowdInfoVO>> getCrowdInfo() {
        log.info("Getting crowd info");
        return Result.success(realtimeInfoService.getCrowdInfo());
    }
    
    /**
     * 获取出行建议
     */
    @GetMapping("/tips")
    public Result<List<TravelTipVO>> getTravelTips() {
        log.info("Getting travel tips");
        return Result.success(realtimeInfoService.getTravelTips());
    }
    
    /**
     * 获取景区实时状态
     */
    @GetMapping("/status/{spotId}")
    public Result<SpotStatusVO> getSpotStatus(@PathVariable Long spotId) {
        log.info("Getting status for spot: {}", spotId);
        return Result.success(realtimeInfoService.getSpotStatus(spotId));
    }
    
    /**
     * 获取综合仪表盘数据
     */
    @GetMapping("/dashboard")
    public Result<DashboardVO> getDashboard() {
        log.info("Getting dashboard data");
        return Result.success(realtimeInfoService.getDashboard());
    }
}
