package com.jiyi.tourism.service;

import com.jiyi.tourism.dto.AudioGuideVO;
import com.jiyi.tourism.dto.RedSpotVO;
import java.util.List;

/**
 * 景点导览服务接口
 */
public interface SpotGuideService {
    
    /**
     * 获取所有红色景点
     */
    List<RedSpotVO> getRedSpots();
    
    /**
     * 获取景点详情
     */
    RedSpotVO getSpotDetail(Long spotId);
    
    /**
     * 获取景点语音讲解列表
     */
    List<AudioGuideVO> getAudioGuides(Long spotId);
    
    /**
     * 获取语音讲解详情
     */
    AudioGuideVO getAudioGuideDetail(Long guideId);
    
    /**
     * 搜索景点
     */
    List<RedSpotVO> searchSpots(String keyword, String category, Boolean freeOnly);
}
