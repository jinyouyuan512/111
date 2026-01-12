package com.jiyi.tourism.service;

import com.jiyi.tourism.dto.*;
import com.jiyi.tourism.entity.WeatherAlert;

import java.util.List;

public interface TourismService {
    /**
     * 根据用户偏好生成推荐路线
     */
    List<RouteVO> generateRoutes(TravelPreferences preferences);
    
    /**
     * 获取路线详情
     */
    RouteVO getRouteDetail(Long routeId);
    
    /**
     * 获取所有路线列表
     */
    List<RouteVO> getAllRoutes();
    
    /**
     * 保存用户行程
     */
    ItineraryVO saveItinerary(Long userId, ItineraryCreateRequest request);
    
    /**
     * 获取用户行程列表
     */
    List<ItineraryVO> getUserItineraries(Long userId);
    
    /**
     * 获取行程详情
     */
    ItineraryVO getItineraryDetail(Long itineraryId);
    
    /**
     * 预订门票
     */
    List<Long> bookTickets(Long userId, TicketBookingRequest request);
    
    /**
     * 获取天气预警
     */
    List<WeatherAlert> getWeatherAlerts(Long routeId);
    
    /**
     * 获取景点列表
     */
    List<AttractionVO> getAllAttractions();
    
    /**
     * 优化路线路径和费用
     */
    RouteOptimizationResult optimizeRoute(Long routeId, String strategy);
    
    /**
     * 生成电子行程单
     */
    ElectronicItinerary generateElectronicItinerary(Long itineraryId);
    
    /**
     * 获取天气信息
     */
    List<WeatherInfo> getWeatherForecast(Long attractionId, Integer days);
    
    /**
     * 推送行程调整建议
     */
    void pushItineraryAdjustment(Long itineraryId, String reason, String suggestion);
}
