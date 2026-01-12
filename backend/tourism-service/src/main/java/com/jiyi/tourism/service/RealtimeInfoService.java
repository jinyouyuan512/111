package com.jiyi.tourism.service;

import com.jiyi.tourism.dto.*;
import java.util.List;

/**
 * 实时信息服务接口
 */
public interface RealtimeInfoService {
    
    /**
     * 获取景点天气预报
     */
    List<SpotWeatherVO> getSpotWeather(List<String> spotNames);
    
    /**
     * 获取景点人流量
     */
    List<CrowdInfoVO> getCrowdInfo();
    
    /**
     * 获取出行建议
     */
    List<TravelTipVO> getTravelTips();
    
    /**
     * 获取景区实时状态
     */
    SpotStatusVO getSpotStatus(Long spotId);
    
    /**
     * 获取综合仪表盘数据
     */
    DashboardVO getDashboard();
}
