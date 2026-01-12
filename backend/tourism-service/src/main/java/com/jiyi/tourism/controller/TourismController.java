package com.jiyi.tourism.controller;

import com.jiyi.common.result.Result;
import com.jiyi.tourism.dto.*;
import com.jiyi.tourism.entity.WeatherAlert;
import com.jiyi.tourism.service.TourismService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/tourism")
@RequiredArgsConstructor
public class TourismController {
    
    private final TourismService tourismService;
    
    /**
     * 获取所有路线
     */
    @GetMapping("/routes")
    public Result<List<RouteVO>> getAllRoutes() {
        log.info("Getting all routes");
        List<RouteVO> routes = tourismService.getAllRoutes();
        return Result.success(routes);
    }
    
    /**
     * 根据偏好推荐路线
     */
    @PostMapping("/routes/recommend")
    public Result<List<RouteVO>> recommendRoutes(@RequestBody TravelPreferences preferences) {
        log.info("Recommending routes for preferences: {}", preferences);
        List<RouteVO> routes = tourismService.generateRoutes(preferences);
        return Result.success(routes);
    }
    
    /**
     * 获取路线详情
     */
    @GetMapping("/routes/{routeId}")
    public Result<RouteVO> getRouteDetail(@PathVariable Long routeId) {
        log.info("Getting route detail for id: {}", routeId);
        RouteVO route = tourismService.getRouteDetail(routeId);
        return Result.success(route);
    }
    
    /**
     * 获取所有景点
     */
    @GetMapping("/attractions")
    public Result<List<AttractionVO>> getAllAttractions() {
        log.info("Getting all attractions");
        List<AttractionVO> attractions = tourismService.getAllAttractions();
        return Result.success(attractions);
    }
    
    /**
     * 保存用户行程
     */
    @PostMapping("/itineraries")
    public Result<ItineraryVO> saveItinerary(
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId,
            @RequestBody ItineraryCreateRequest request) {
        log.info("Saving itinerary for user: {}, request: {}", userId, request);
        ItineraryVO itinerary = tourismService.saveItinerary(userId, request);
        return Result.success(itinerary);
    }
    
    /**
     * 获取用户行程列表
     */
    @GetMapping("/itineraries")
    public Result<List<ItineraryVO>> getUserItineraries(
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId) {
        log.info("Getting itineraries for user: {}", userId);
        List<ItineraryVO> itineraries = tourismService.getUserItineraries(userId);
        return Result.success(itineraries);
    }
    
    /**
     * 获取行程详情
     */
    @GetMapping("/itineraries/{itineraryId}")
    public Result<ItineraryVO> getItineraryDetail(@PathVariable Long itineraryId) {
        log.info("Getting itinerary detail for id: {}", itineraryId);
        ItineraryVO itinerary = tourismService.getItineraryDetail(itineraryId);
        return Result.success(itinerary);
    }
    
    /**
     * 获取天气预警
     */
    @GetMapping("/routes/{routeId}/alerts")
    public Result<List<WeatherAlert>> getWeatherAlerts(@PathVariable Long routeId) {
        log.info("Getting weather alerts for route: {}", routeId);
        List<WeatherAlert> alerts = tourismService.getWeatherAlerts(routeId);
        return Result.success(alerts);
    }
    
    /**
     * 优化路线
     */
    @PostMapping("/routes/{routeId}/optimize")
    public Result<RouteOptimizationResult> optimizeRoute(
            @PathVariable Long routeId,
            @RequestParam(required = false, defaultValue = "shortest_distance") String strategy) {
        log.info("Optimizing route: {}, strategy: {}", routeId, strategy);
        RouteOptimizationResult result = tourismService.optimizeRoute(routeId, strategy);
        return Result.success(result);
    }
    
    /**
     * 生成电子行程单
     */
    @GetMapping("/itineraries/{itineraryId}/electronic")
    public Result<ElectronicItinerary> generateElectronicItinerary(@PathVariable Long itineraryId) {
        log.info("Generating electronic itinerary for id: {}", itineraryId);
        ElectronicItinerary itinerary = tourismService.generateElectronicItinerary(itineraryId);
        return Result.success(itinerary);
    }
    
    /**
     * 获取天气预报
     */
    @GetMapping("/attractions/{attractionId}/weather")
    public Result<List<WeatherInfo>> getWeatherForecast(
            @PathVariable Long attractionId,
            @RequestParam(required = false, defaultValue = "3") Integer days) {
        log.info("Getting weather forecast for attraction: {}, days: {}", attractionId, days);
        List<WeatherInfo> weatherInfos = tourismService.getWeatherForecast(attractionId, days);
        return Result.success(weatherInfos);
    }
    
    /**
     * 推送行程调整建议
     */
    @PostMapping("/itineraries/{itineraryId}/adjustment")
    public Result<Void> pushItineraryAdjustment(
            @PathVariable Long itineraryId,
            @RequestParam String reason,
            @RequestParam String suggestion) {
        log.info("Pushing itinerary adjustment for id: {}, reason: {}, suggestion: {}", 
                itineraryId, reason, suggestion);
        tourismService.pushItineraryAdjustment(itineraryId, reason, suggestion);
        return Result.success(null);
    }
}
