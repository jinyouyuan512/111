package com.jiyi.guide.service;

import com.jiyi.guide.dto.*;

import java.math.BigDecimal;
import java.util.List;

public interface GuideService {
    
    /**
     * 检测用户位置并识别景区
     */
    ScenicAreaVO detectLocation(BigDecimal latitude, BigDecimal longitude);
    
    /**
     * 获取景点信息
     */
    PoiVO getPoiInfo(Long poiId);
    
    /**
     * 触发语音讲解
     */
    AudioGuideVO triggerAudioGuide(Long poiId, String language);
    
    /**
     * 加载AR场景
     */
    ArSceneVO loadArScene(String qrCode);
    
    /**
     * 获取导览路线
     */
    List<GuideRouteVO> getGuideRoutes(Long scenicAreaId);
    
    /**
     * 规划导航路径
     */
    String planNavigation(NavigationRequest request);
    
    /**
     * 开始游览
     */
    Long startVisit(Long userId, Long scenicAreaId);
    
    /**
     * 记录轨迹点
     */
    void recordTrackPoint(TrackPointRequest request);
    
    /**
     * 结束游览
     */
    void endVisit(Long visitId);
    
    /**
     * 生成游记
     */
    TravelLogVO generateTravelLog(Long userId, Long visitId);
    
    /**
     * 获取用户游记列表
     */
    List<TravelLogVO> getUserTravelLogs(Long userId);
}
