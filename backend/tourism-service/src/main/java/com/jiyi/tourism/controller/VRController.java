package com.jiyi.tourism.controller;

import com.jiyi.common.result.Result;
import com.jiyi.tourism.dto.VRSpotVO;
import com.jiyi.tourism.dto.VRSceneVO;
import com.jiyi.tourism.service.VRService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * VR全景体验控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/tourism/vr")
@RequiredArgsConstructor
public class VRController {
    
    private final VRService vrService;
    
    /**
     * 获取所有VR景点
     */
    @GetMapping("/spots")
    public Result<List<VRSpotVO>> getAllVRSpots() {
        log.info("Getting all VR spots");
        List<VRSpotVO> spots = vrService.getAllVRSpots();
        return Result.success(spots);
    }
    
    /**
     * 获取VR景点详情
     */
    @GetMapping("/spots/{spotId}")
    public Result<VRSpotVO> getVRSpotDetail(@PathVariable Long spotId) {
        log.info("Getting VR spot detail for id: {}", spotId);
        VRSpotVO spot = vrService.getVRSpotDetail(spotId);
        return Result.success(spot);
    }
    
    /**
     * 获取VR场景数据
     */
    @GetMapping("/spots/{spotId}/scene")
    public Result<VRSceneVO> getVRScene(@PathVariable Long spotId) {
        log.info("Getting VR scene for spot: {}", spotId);
        VRSceneVO scene = vrService.getVRScene(spotId);
        return Result.success(scene);
    }
    
    /**
     * 记录VR浏览
     */
    @PostMapping("/spots/{spotId}/view")
    public Result<Void> recordVRView(
            @PathVariable Long spotId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        log.info("Recording VR view for spot: {}, user: {}", spotId, userId);
        vrService.recordVRView(spotId, userId);
        return Result.success(null);
    }
    
    /**
     * 获取语音导览内容
     */
    @GetMapping("/spots/{spotId}/audio-guide")
    public Result<VRSpotVO.AudioGuide> getAudioGuide(
            @PathVariable Long spotId,
            @RequestParam(defaultValue = "zh") String language) {
        log.info("Getting audio guide for spot: {}, language: {}", spotId, language);
        VRSpotVO.AudioGuide audioGuide = vrService.getAudioGuide(spotId, language);
        return Result.success(audioGuide);
    }
}
