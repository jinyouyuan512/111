package com.jiyi.experience.controller;

import com.jiyi.common.result.Result;
import com.jiyi.experience.dto.*;
import com.jiyi.experience.service.ExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 体验馆控制器
 */
@RestController
@RequestMapping("/api/experience")
@RequiredArgsConstructor
public class ExperienceController {
    
    private final ExperienceService experienceService;
    
    /**
     * 获取场景列表
     */
    @GetMapping("/scenes")
    public Result<List<SceneVO>> getSceneList() {
        List<SceneVO> scenes = experienceService.getSceneList();
        return Result.success(scenes);
    }
    
    /**
     * 获取场景详情
     */
    @GetMapping("/scenes/{sceneId}")
    public Result<SceneDetailVO> getSceneDetail(
            @PathVariable Long sceneId,
            @RequestParam(required = false) Long userId) {
        SceneDetailVO detail = experienceService.getSceneDetail(sceneId, userId);
        return Result.success(detail);
    }
    
    /**
     * 更新用户体验进度
     */
    @PostMapping("/progress")
    public Result<Integer> updateProgress(@RequestBody ProgressUpdateRequest request) {
        Integer progress = experienceService.updateProgress(request);
        return Result.success(progress);
    }
    
    /**
     * 生成体验证书
     */
    @PostMapping("/certificate")
    public Result<CertificateVO> generateCertificate(
            @RequestParam Long userId,
            @RequestParam Long sceneId) {
        CertificateVO certificate = experienceService.generateCertificate(userId, sceneId);
        return Result.success(certificate);
    }
    
    /**
     * 获取用户的所有证书
     */
    @GetMapping("/certificates/{userId}")
    public Result<List<CertificateVO>> getUserCertificates(@PathVariable Long userId) {
        List<CertificateVO> certificates = experienceService.getUserCertificates(userId);
        return Result.success(certificates);
    }
}
