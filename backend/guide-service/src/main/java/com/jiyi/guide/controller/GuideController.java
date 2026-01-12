package com.jiyi.guide.controller;

import com.jiyi.common.result.Result;
import com.jiyi.guide.dto.*;
import com.jiyi.guide.service.GuideService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guide")
@RequiredArgsConstructor
public class GuideController {
    
    private final GuideService guideService;
    
    /**
     * 检测位置并识别景区
     */
    @PostMapping("/detect-location")
    public Result<ScenicAreaVO> detectLocation(@RequestBody LocationRequest request) {
        ScenicAreaVO scenicArea = guideService.detectLocation(
            request.getLatitude(),
            request.getLongitude()
        );
        
        if (scenicArea == null) {
            return Result.error("未检测到附近的景区");
        }
        
        return Result.success(scenicArea);
    }
    
    /**
     * 获取景点信息
     */
    @GetMapping("/poi/{poiId}")
    public Result<PoiVO> getPoiInfo(@PathVariable Long poiId) {
        PoiVO poi = guideService.getPoiInfo(poiId);
        
        if (poi == null) {
            return Result.error("景点不存在");
        }
        
        return Result.success(poi);
    }
    
    /**
     * 触发语音讲解
     */
    @GetMapping("/audio-guide/{poiId}")
    public Result<AudioGuideVO> triggerAudioGuide(
        @PathVariable Long poiId,
        @RequestParam(required = false, defaultValue = "zh") String language
    ) {
        AudioGuideVO audioGuide = guideService.triggerAudioGuide(poiId, language);
        
        if (audioGuide == null) {
            return Result.error("该景点暂无语音讲解");
        }
        
        return Result.success(audioGuide);
    }
    
    /**
     * 扫描二维码加载AR场景
     */
    @GetMapping("/ar-scene/{qrCode}")
    public Result<ArSceneVO> loadArScene(@PathVariable String qrCode) {
        ArSceneVO arScene = guideService.loadArScene(qrCode);
        
        if (arScene == null) {
            return Result.error("无效的二维码或该位置暂无AR场景");
        }
        
        return Result.success(arScene);
    }
    
    /**
     * 获取导览路线
     */
    @GetMapping("/routes/{scenicAreaId}")
    public Result<List<GuideRouteVO>> getGuideRoutes(@PathVariable Long scenicAreaId) {
        List<GuideRouteVO> routes = guideService.getGuideRoutes(scenicAreaId);
        return Result.success(routes);
    }
    
    /**
     * 规划导航路径
     */
    @PostMapping("/navigation")
    public Result<String> planNavigation(@RequestBody NavigationRequest request) {
        String path = guideService.planNavigation(request);
        
        if (path == null) {
            return Result.error("无法规划路径");
        }
        
        return Result.success(path);
    }
    
    /**
     * 开始游览
     */
    @PostMapping("/visit/start")
    public Result<Long> startVisit(
        @RequestParam Long userId,
        @RequestParam Long scenicAreaId
    ) {
        Long visitId = guideService.startVisit(userId, scenicAreaId);
        return Result.success(visitId);
    }
    
    /**
     * 记录轨迹点
     */
    @PostMapping("/visit/track")
    public Result<Void> recordTrackPoint(@RequestBody TrackPointRequest request) {
        guideService.recordTrackPoint(request);
        return Result.success();
    }
    
    /**
     * 结束游览
     */
    @PostMapping("/visit/end/{visitId}")
    public Result<Void> endVisit(@PathVariable Long visitId) {
        guideService.endVisit(visitId);
        return Result.success();
    }
    
    /**
     * 生成游记
     */
    @PostMapping("/travel-log/generate")
    public Result<TravelLogVO> generateTravelLog(
        @RequestParam Long userId,
        @RequestParam Long visitId
    ) {
        TravelLogVO travelLog = guideService.generateTravelLog(userId, visitId);
        
        if (travelLog == null) {
            return Result.error("生成游记失败");
        }
        
        return Result.success(travelLog);
    }
    
    /**
     * 获取用户游记列表
     */
    @GetMapping("/travel-log/user/{userId}")
    public Result<List<TravelLogVO>> getUserTravelLogs(@PathVariable Long userId) {
        List<TravelLogVO> logs = guideService.getUserTravelLogs(userId);
        return Result.success(logs);
    }
}
