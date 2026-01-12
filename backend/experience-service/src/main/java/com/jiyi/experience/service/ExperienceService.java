package com.jiyi.experience.service;

import com.jiyi.experience.dto.*;
import java.util.List;

/**
 * 体验馆服务接口
 */
public interface ExperienceService {
    
    /**
     * 获取场景列表
     * 
     * @return 场景列表
     */
    List<SceneVO> getSceneList();
    
    /**
     * 获取场景详情（包含交互点）
     * 
     * @param sceneId 场景ID
     * @param userId 用户ID（可选，用于获取进度）
     * @return 场景详情
     */
    SceneDetailVO getSceneDetail(Long sceneId, Long userId);
    
    /**
     * 更新用户体验进度
     * 
     * @param request 进度更新请求
     * @return 更新后的进度百分比
     */
    Integer updateProgress(ProgressUpdateRequest request);
    
    /**
     * 生成体验证书
     * 
     * @param userId 用户ID
     * @param sceneId 场景ID
     * @return 证书信息
     */
    CertificateVO generateCertificate(Long userId, Long sceneId);
    
    /**
     * 获取用户的所有证书
     * 
     * @param userId 用户ID
     * @return 证书列表
     */
    List<CertificateVO> getUserCertificates(Long userId);
}
