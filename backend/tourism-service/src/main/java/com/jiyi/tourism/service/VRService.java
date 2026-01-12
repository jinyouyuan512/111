package com.jiyi.tourism.service;

import com.jiyi.tourism.dto.VRSpotVO;
import com.jiyi.tourism.dto.VRSceneVO;

import java.util.List;

/**
 * VR全景服务接口
 */
public interface VRService {
    
    /**
     * 获取所有VR景点
     */
    List<VRSpotVO> getAllVRSpots();
    
    /**
     * 获取VR景点详情
     */
    VRSpotVO getVRSpotDetail(Long spotId);
    
    /**
     * 获取VR场景数据
     */
    VRSceneVO getVRScene(Long spotId);
    
    /**
     * 记录VR浏览
     */
    void recordVRView(Long spotId, Long userId);
    
    /**
     * 获取语音导览
     */
    VRSpotVO.AudioGuide getAudioGuide(Long spotId, String language);
}
