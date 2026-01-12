package com.jiyi.experience.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiyi.experience.dto.*;
import com.jiyi.experience.entity.*;
import com.jiyi.experience.mapper.*;
import com.jiyi.experience.service.ExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 体验馆服务实现
 */
@Service
@RequiredArgsConstructor
public class ExperienceServiceImpl implements ExperienceService {
    
    private final SceneMapper sceneMapper;
    private final InteractionPointMapper interactionPointMapper;
    private final UserProgressMapper userProgressMapper;
    private final CertificateMapper certificateMapper;
    
    @Override
    public List<SceneVO> getSceneList() {
        List<Scene> scenes = sceneMapper.selectList(null);
        
        return scenes.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    @Override
    public SceneDetailVO getSceneDetail(Long sceneId, Long userId) {
        // 获取场景信息
        Scene scene = sceneMapper.selectById(sceneId);
        if (scene == null) {
            throw new RuntimeException("场景不存在");
        }
        
        // 获取交互点列表
        LambdaQueryWrapper<InteractionPoint> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InteractionPoint::getSceneId, sceneId)
               .orderByAsc(InteractionPoint::getSortOrder);
        List<InteractionPoint> interactionPoints = interactionPointMapper.selectList(wrapper);
        
        // 转换为VO
        SceneDetailVO detailVO = new SceneDetailVO();
        BeanUtils.copyProperties(scene, detailVO);
        
        List<InteractionPointVO> pointVOs = interactionPoints.stream()
                .map(this::convertToInteractionPointVO)
                .collect(Collectors.toList());
        detailVO.setInteractionPoints(pointVOs);
        
        // 如果提供了用户ID，获取用户进度
        if (userId != null) {
            UserProgress progress = getUserProgress(userId, sceneId);
            detailVO.setProgress(progress != null ? progress.getProgress() : 0);
        } else {
            detailVO.setProgress(0);
        }
        
        return detailVO;
    }
    
    @Override
    @Transactional
    public Integer updateProgress(ProgressUpdateRequest request) {
        Long userId = request.getUserId();
        Long sceneId = request.getSceneId();
        Long interactionPointId = request.getInteractionPointId();
        
        // 获取或创建用户进度记录
        UserProgress progress = getUserProgress(userId, sceneId);
        if (progress == null) {
            progress = new UserProgress();
            progress.setUserId(userId);
            progress.setSceneId(sceneId);
            progress.setProgress(0);
            progress.setCompletedInteractions("");
            progress.setStartTime(LocalDateTime.now());
            progress.setCompleted(false);
        }
        
        // 更新已完成的交互点
        String completedStr = progress.getCompletedInteractions();
        List<String> completedList = completedStr.isEmpty() ? 
                new java.util.ArrayList<>() : 
                new java.util.ArrayList<>(Arrays.asList(completedStr.split(",")));
        
        String pointIdStr = String.valueOf(interactionPointId);
        if (!completedList.contains(pointIdStr)) {
            completedList.add(pointIdStr);
            progress.setCompletedInteractions(String.join(",", completedList));
        }
        
        // 计算进度百分比
        LambdaQueryWrapper<InteractionPoint> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InteractionPoint::getSceneId, sceneId);
        long totalPoints = interactionPointMapper.selectCount(wrapper);
        
        int newProgress = totalPoints > 0 ? 
                (int) Math.round((completedList.size() * 100.0) / totalPoints) : 0;
        progress.setProgress(newProgress);
        progress.setLastUpdateTime(LocalDateTime.now());
        
        // 如果进度达到100%，标记为已完成
        if (newProgress >= 100) {
            progress.setCompleted(true);
        }
        
        // 保存或更新进度
        if (progress.getId() == null) {
            userProgressMapper.insert(progress);
        } else {
            userProgressMapper.updateById(progress);
        }
        
        return newProgress;
    }
    
    @Override
    @Transactional
    public CertificateVO generateCertificate(Long userId, Long sceneId) {
        // 检查用户是否完成了该场景
        UserProgress progress = getUserProgress(userId, sceneId);
        if (progress == null || !progress.getCompleted()) {
            throw new RuntimeException("尚未完成该场景体验，无法生成证书");
        }
        
        // 检查是否已经生成过证书
        LambdaQueryWrapper<Certificate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Certificate::getUserId, userId)
               .eq(Certificate::getSceneId, sceneId);
        Certificate existingCert = certificateMapper.selectOne(wrapper);
        
        if (existingCert != null) {
            return convertToCertificateVO(existingCert);
        }
        
        // 生成新证书
        Scene scene = sceneMapper.selectById(sceneId);
        Certificate certificate = new Certificate();
        certificate.setUserId(userId);
        certificate.setSceneId(sceneId);
        certificate.setCertificateNo(generateCertificateNo(userId, sceneId));
        certificate.setIssueDate(LocalDate.now());
        certificate.setCertificateUrl(""); // 实际应用中应生成证书图片
        
        certificateMapper.insert(certificate);
        
        return convertToCertificateVO(certificate);
    }
    
    @Override
    public List<CertificateVO> getUserCertificates(Long userId) {
        LambdaQueryWrapper<Certificate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Certificate::getUserId, userId)
               .orderByDesc(Certificate::getIssueDate);
        List<Certificate> certificates = certificateMapper.selectList(wrapper);
        
        return certificates.stream()
                .map(this::convertToCertificateVO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取用户进度
     */
    private UserProgress getUserProgress(Long userId, Long sceneId) {
        LambdaQueryWrapper<UserProgress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserProgress::getUserId, userId)
               .eq(UserProgress::getSceneId, sceneId);
        return userProgressMapper.selectOne(wrapper);
    }
    
    /**
     * 生成证书编号
     */
    private String generateCertificateNo(Long userId, Long sceneId) {
        return String.format("JYHT-%d-%d-%d", 
                sceneId, userId, System.currentTimeMillis());
    }
    
    /**
     * 将Scene实体转换为SceneVO
     */
    private SceneVO convertToVO(Scene scene) {
        SceneVO vo = new SceneVO();
        BeanUtils.copyProperties(scene, vo);
        return vo;
    }
    
    /**
     * 将InteractionPoint实体转换为InteractionPointVO
     */
    private InteractionPointVO convertToInteractionPointVO(InteractionPoint point) {
        InteractionPointVO vo = new InteractionPointVO();
        BeanUtils.copyProperties(point, vo);
        return vo;
    }
    
    /**
     * 将Certificate实体转换为CertificateVO
     */
    private CertificateVO convertToCertificateVO(Certificate certificate) {
        CertificateVO vo = new CertificateVO();
        BeanUtils.copyProperties(certificate, vo);
        
        // 获取场景名称
        Scene scene = sceneMapper.selectById(certificate.getSceneId());
        if (scene != null) {
            vo.setSceneName(scene.getName());
        }
        
        return vo;
    }
}
