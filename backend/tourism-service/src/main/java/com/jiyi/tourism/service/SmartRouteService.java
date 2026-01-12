package com.jiyi.tourism.service;

import com.jiyi.tourism.dto.*;
import java.util.List;

/**
 * 智能路线服务接口
 */
public interface SmartRouteService {
    
    /**
     * 获取热门路线
     */
    List<HotRouteVO> getHotRoutes(Integer limit);
    
    /**
     * AI生成行程规划
     */
    TripPlanVO generateTripPlan(TripPlanRequest request);
    
    /**
     * 保存行程规划
     */
    Long saveTripPlan(Long userId, TripPlanVO plan);
    
    /**
     * 获取用户保存的行程
     */
    List<TripPlanVO> getUserPlans(Long userId);
    
    /**
     * 路线优化
     */
    TripPlanVO optimizeRoute(RouteOptimizeRequest request);
}
