package com.jiyi.tourism.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiyi.tourism.dto.*;
import com.jiyi.tourism.entity.Attraction;
import com.jiyi.tourism.entity.Route;
import com.jiyi.tourism.entity.UserItinerary;
import com.jiyi.tourism.entity.UserTripPlan;
import com.jiyi.tourism.mapper.AttractionMapper;
import com.jiyi.tourism.mapper.RouteMapper;
import com.jiyi.tourism.mapper.UserItineraryMapper;
import com.jiyi.tourism.mapper.UserTripPlanMapper;
import com.jiyi.tourism.service.SmartRouteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmartRouteServiceImpl implements SmartRouteService {
    
    private final RouteMapper routeMapper;
    private final AttractionMapper attractionMapper;
    private final UserItineraryMapper userItineraryMapper;
    private final UserTripPlanMapper userTripPlanMapper;
    
    @Override
    public List<HotRouteVO> getHotRoutes(Integer limit) {
        log.info("Getting hot routes, limit: {}", limit);
        
        LambdaQueryWrapper<Route> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Route::getStatus, "active");
        wrapper.orderByDesc(Route::getBookingCount);
        wrapper.last("LIMIT " + (limit != null ? limit : 10));
        
        List<Route> routes = routeMapper.selectList(wrapper);
        
        List<HotRouteVO> hotRoutes = new ArrayList<>();
        int rank = 1;
        for (Route route : routes) {
            HotRouteVO vo = new HotRouteVO();
            vo.setId(route.getId().toString());
            vo.setRank(rank++);
            vo.setName(route.getName());
            vo.setDuration(route.getDays() + "天");
            vo.setViews(route.getViewCount());
            vo.setRating(4.8);
            vo.setSpots(Arrays.asList(1L, 2L)); // 简化处理
            hotRoutes.add(vo);
        }
        
        return hotRoutes;
    }
    
    @Override
    public TripPlanVO generateTripPlan(TripPlanRequest request) {
        log.info("Generating trip plan: {}", request);
        
        List<String> spotNames = request.getSpots();
        Integer duration = request.getDuration() != null ? request.getDuration() : 1;
        String startDate = request.getStartDate() != null ? request.getStartDate() : LocalDate.now().toString();
        
        // 查找对应的景点
        List<Attraction> attractions = new ArrayList<>();
        for (String name : spotNames) {
            LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<>();
            wrapper.like(Attraction::getName, name);
            List<Attraction> found = attractionMapper.selectList(wrapper);
            if (!found.isEmpty()) {
                attractions.add(found.get(0));
            }
        }
        
        // 生成行程规划
        TripPlanVO plan = new TripPlanVO();
        plan.setTitle(spotNames.get(0) + "等" + spotNames.size() + "景点游");
        plan.setDescription("为您精心规划的河北红色之旅");
        
        // 按天分配景点
        List<TripPlanVO.DayPlan> days = new ArrayList<>();
        int spotsPerDay = (int) Math.ceil((double) spotNames.size() / duration);
        
        for (int d = 0; d < duration; d++) {
            TripPlanVO.DayPlan dayPlan = new TripPlanVO.DayPlan();
            dayPlan.setDay(d + 1);
            dayPlan.setDate(LocalDate.parse(startDate).plusDays(d).toString());
            
            List<TripPlanVO.SpotPlan> spotPlans = new ArrayList<>();
            int startIdx = d * spotsPerDay;
            int endIdx = Math.min(startIdx + spotsPerDay, spotNames.size());
            
            for (int i = startIdx; i < endIdx; i++) {
                TripPlanVO.SpotPlan spotPlan = new TripPlanVO.SpotPlan();
                spotPlan.setOrder(i - startIdx + 1);
                spotPlan.setName(spotNames.get(i));
                spotPlan.setDuration("2-3小时");
                spotPlan.setTips("建议提前预约");
                
                int hour = 9 + (i - startIdx) * 3;
                spotPlan.setArrivalTime(String.format("%02d:00", hour));
                spotPlan.setDepartureTime(String.format("%02d:00", hour + 2));
                
                spotPlans.add(spotPlan);
            }
            
            dayPlan.setSpots(spotPlans);
            dayPlan.setMeals(Arrays.asList("午餐：当地特色餐厅", "晚餐：酒店餐厅"));
            if (d < duration - 1) {
                dayPlan.setAccommodation("当地三星级酒店");
            }
            
            days.add(dayPlan);
        }
        
        plan.setDays(days);
        plan.setTotalDistance(BigDecimal.valueOf(spotNames.size() * 30));
        plan.setEstimatedCost(BigDecimal.valueOf(spotNames.size() * 150 + duration * 300));
        plan.setTips(Arrays.asList(
            "携带身份证件",
            "穿舒适的鞋子",
            "提前预约免费景点",
            "注意天气变化"
        ));
        
        return plan;
    }
    
    @Override
    public Long saveTripPlan(Long userId, TripPlanVO plan) {
        log.info("Saving trip plan for user: {}", userId);
        
        UserItinerary itinerary = new UserItinerary();
        itinerary.setUserId(userId);
        itinerary.setTitle(plan.getTitle());
        itinerary.setStatus("planned");
        
        if (plan.getDays() != null && !plan.getDays().isEmpty()) {
            itinerary.setStartDate(LocalDate.parse(plan.getDays().get(0).getDate()));
            itinerary.setEndDate(LocalDate.parse(plan.getDays().get(plan.getDays().size() - 1).getDate()));
        }
        
        userItineraryMapper.insert(itinerary);
        
        return itinerary.getId();
    }
    
    @Override
    public List<TripPlanVO> getUserPlans(Long userId) {
        log.info("Getting plans for user: {}", userId);
        
        List<TripPlanVO> result = new ArrayList<>();
        
        try {
            // 从 user_trip_plan 表查询
            LambdaQueryWrapper<UserTripPlan> tripPlanWrapper = new LambdaQueryWrapper<>();
            tripPlanWrapper.eq(UserTripPlan::getUserId, userId);
            tripPlanWrapper.orderByDesc(UserTripPlan::getCreatedAt);
            
            List<UserTripPlan> tripPlans = userTripPlanMapper.selectList(tripPlanWrapper);
            log.info("Found {} trip plans for user {}", tripPlans.size(), userId);
            
            for (UserTripPlan plan : tripPlans) {
                TripPlanVO vo = new TripPlanVO();
                vo.setId(plan.getId());
                vo.setTitle(plan.getTitle() != null ? plan.getTitle() : "AI生成行程");
                vo.setDescription("AI智能规划的红色之旅");
                vo.setEstimatedCost(plan.getEstimatedCost() != null ? plan.getEstimatedCost() : BigDecimal.valueOf(200));
                vo.setPlanData(plan.getPlanData());
                vo.setCreatedAt(plan.getCreatedAt());
                result.add(vo);
            }
        } catch (Exception e) {
            log.error("Error getting user plans", e);
        }
        
        return result;
    }
    
    @Override
    public TripPlanVO optimizeRoute(RouteOptimizeRequest request) {
        log.info("Optimizing route: {}", request);
        
        // 简单的路线优化：按距离排序
        List<String> spots = request.getSpots();
        
        // 这里可以实现更复杂的优化算法
        // 目前简单返回原顺序
        
        TripPlanRequest planRequest = new TripPlanRequest();
        planRequest.setSpots(spots);
        planRequest.setDuration(1);
        
        return generateTripPlan(planRequest);
    }
    
    private TripPlanVO convertToTripPlanVO(UserItinerary itinerary) {
        TripPlanVO vo = new TripPlanVO();
        vo.setId(itinerary.getId());
        vo.setTitle(itinerary.getTitle());
        vo.setDescription("用户保存的行程");
        return vo;
    }
}
