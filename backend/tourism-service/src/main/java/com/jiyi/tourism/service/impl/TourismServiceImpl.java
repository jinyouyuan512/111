package com.jiyi.tourism.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiyi.tourism.dto.*;
import com.jiyi.tourism.entity.*;
import com.jiyi.tourism.mapper.*;
import com.jiyi.tourism.service.TourismService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TourismServiceImpl implements TourismService {
    
    private final RouteMapper routeMapper;
    private final AttractionMapper attractionMapper;
    private final RouteAttractionMapper routeAttractionMapper;
    private final TransportationMapper transportationMapper;
    private final AccommodationMapper accommodationMapper;
    private final UserItineraryMapper userItineraryMapper;
    private final TicketBookingMapper ticketBookingMapper;
    private final WeatherAlertMapper weatherAlertMapper;
    private final ObjectMapper objectMapper;
    
    @Override
    public List<RouteVO> generateRoutes(TravelPreferences preferences) {
        log.info("Generating routes for preferences: {}", preferences);
        
        // 构建查询条件
        LambdaQueryWrapper<Route> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Route::getStatus, "active");
        
        // 根据天数筛选
        if (preferences.getDays() != null) {
            wrapper.le(Route::getDays, preferences.getDays() + 1);
            wrapper.ge(Route::getDays, Math.max(1, preferences.getDays() - 1));
        }
        
        // 根据预算筛选
        if (preferences.getBudget() != null) {
            wrapper.le(Route::getEstimatedCost, preferences.getBudget().multiply(new BigDecimal("1.2")));
        }
        
        // 根据难度筛选
        if (preferences.getDifficulty() != null && !preferences.getDifficulty().isEmpty()) {
            wrapper.eq(Route::getDifficulty, preferences.getDifficulty());
        }
        
        // 根据季节筛选
        if (preferences.getSeason() != null && !preferences.getSeason().isEmpty()) {
            wrapper.like(Route::getSeason, preferences.getSeason());
        }
        
        // 获取所有符合条件的路线
        List<Route> routes = routeMapper.selectList(wrapper);
        
        // 智能推荐算法：基于用户偏好计算匹配度
        List<RouteVO> routeVOs = routes.stream()
                .map(this::convertToRouteVO)
                .collect(Collectors.toList());
        
        // 计算每条路线的推荐分数
        for (RouteVO routeVO : routeVOs) {
            double score = calculateRecommendationScore(routeVO, preferences);
            routeVO.setRecommendationScore(score);
        }
        
        // 按推荐分数排序，返回前10条
        return routeVOs.stream()
                .sorted(Comparator.comparingDouble(RouteVO::getRecommendationScore).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }
    
    /**
     * 计算路线推荐分数
     * 综合考虑：兴趣匹配度、预算匹配度、热度、评分等因素
     */
    private double calculateRecommendationScore(RouteVO route, TravelPreferences preferences) {
        double score = 0.0;
        
        // 1. 兴趣匹配度 (40%)
        if (preferences.getInterests() != null && !preferences.getInterests().isEmpty() 
                && route.getTags() != null && !route.getTags().isEmpty()) {
            long matchCount = preferences.getInterests().stream()
                    .filter(interest -> route.getTags().stream()
                            .anyMatch(tag -> tag.contains(interest) || interest.contains(tag)))
                    .count();
            double interestScore = (double) matchCount / preferences.getInterests().size();
            score += interestScore * 40;
        }
        
        // 2. 预算匹配度 (30%)
        if (preferences.getBudget() != null && route.getEstimatedCost() != null) {
            BigDecimal budgetDiff = preferences.getBudget().subtract(route.getEstimatedCost()).abs();
            BigDecimal budgetRatio = budgetDiff.divide(preferences.getBudget(), 4, RoundingMode.HALF_UP);
            double budgetScore = Math.max(0, 1 - budgetRatio.doubleValue());
            score += budgetScore * 30;
        }
        
        // 3. 热度分数 (20%)
        if (route.getBookingCount() != null) {
            double popularityScore = Math.min(1.0, route.getBookingCount() / 100.0);
            score += popularityScore * 20;
        }
        
        // 4. 天数匹配度 (10%)
        if (preferences.getDays() != null && route.getDays() != null) {
            int daysDiff = Math.abs(preferences.getDays() - route.getDays());
            double daysScore = Math.max(0, 1 - daysDiff / 5.0);
            score += daysScore * 10;
        }
        
        return score;
    }
    
    @Override
    public RouteVO getRouteDetail(Long routeId) {
        log.info("Getting route detail for id: {}", routeId);
        
        Route route = routeMapper.selectById(routeId);
        if (route == null) {
            throw new RuntimeException("路线不存在");
        }
        
        // 增加浏览次数
        route.setViewCount(route.getViewCount() + 1);
        routeMapper.updateById(route);
        
        return convertToRouteVO(route);
    }
    
    @Override
    public List<RouteVO> getAllRoutes() {
        log.info("Getting all routes");
        
        LambdaQueryWrapper<Route> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Route::getStatus, "active");
        wrapper.orderByDesc(Route::getBookingCount);
        
        List<Route> routes = routeMapper.selectList(wrapper);
        
        return routes.stream()
                .map(this::convertToRouteVO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public ItineraryVO saveItinerary(Long userId, ItineraryCreateRequest request) {
        log.info("Saving itinerary for user: {}, request: {}", userId, request);
        
        // 验证路线是否存在
        Route route = routeMapper.selectById(request.getRouteId());
        if (route == null) {
            throw new RuntimeException("路线不存在");
        }
        
        // 创建行程
        UserItinerary itinerary = new UserItinerary();
        itinerary.setUserId(userId);
        itinerary.setRouteId(request.getRouteId());
        itinerary.setTitle(request.getTitle());
        itinerary.setStartDate(request.getStartDate());
        itinerary.setEndDate(request.getEndDate());
        itinerary.setStatus("planned");
        itinerary.setNotes(request.getNotes());
        
        userItineraryMapper.insert(itinerary);
        
        // 增加路线预订次数
        route.setBookingCount(route.getBookingCount() + 1);
        routeMapper.updateById(route);
        
        return convertToItineraryVO(itinerary);
    }
    
    @Override
    public List<ItineraryVO> getUserItineraries(Long userId) {
        log.info("Getting itineraries for user: {}", userId);
        
        LambdaQueryWrapper<UserItinerary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserItinerary::getUserId, userId);
        wrapper.orderByDesc(UserItinerary::getCreatedAt);
        
        List<UserItinerary> itineraries = userItineraryMapper.selectList(wrapper);
        
        return itineraries.stream()
                .map(this::convertToItineraryVO)
                .collect(Collectors.toList());
    }
    
    @Override
    public ItineraryVO getItineraryDetail(Long itineraryId) {
        log.info("Getting itinerary detail for id: {}", itineraryId);
        
        UserItinerary itinerary = userItineraryMapper.selectById(itineraryId);
        if (itinerary == null) {
            throw new RuntimeException("行程不存在");
        }
        
        return convertToItineraryVO(itinerary);
    }
    
    @Override
    @Transactional
    public List<Long> bookTickets(Long userId, TicketBookingRequest request) {
        log.info("Booking tickets for user: {}, request: {}", userId, request);
        
        List<Long> bookingIds = new ArrayList<>();
        
        for (TicketBookingRequest.TicketItem item : request.getTickets()) {
            Attraction attraction = attractionMapper.selectById(item.getAttractionId());
            if (attraction == null) {
                throw new RuntimeException("景点不存在: " + item.getAttractionId());
            }
            
            TicketBooking booking = new TicketBooking();
            booking.setUserId(userId);
            booking.setItineraryId(request.getItineraryId());
            booking.setAttractionId(item.getAttractionId());
            booking.setVisitDate(item.getVisitDate());
            booking.setQuantity(item.getQuantity());
            booking.setTotalPrice(attraction.getTicketPrice().multiply(new BigDecimal(item.getQuantity())));
            booking.setStatus("confirmed");
            booking.setBookingNo(generateBookingNo());
            
            ticketBookingMapper.insert(booking);
            bookingIds.add(booking.getId());
        }
        
        return bookingIds;
    }
    
    @Override
    public List<WeatherAlert> getWeatherAlerts(Long routeId) {
        log.info("Getting weather alerts for route: {}", routeId);
        
        // 获取路线的所有景点
        LambdaQueryWrapper<RouteAttraction> raWrapper = new LambdaQueryWrapper<>();
        raWrapper.eq(RouteAttraction::getRouteId, routeId);
        List<RouteAttraction> routeAttractions = routeAttractionMapper.selectList(raWrapper);
        
        if (routeAttractions.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<Long> attractionIds = routeAttractions.stream()
                .map(RouteAttraction::getAttractionId)
                .collect(Collectors.toList());
        
        // 查询这些景点的预警信息
        LambdaQueryWrapper<WeatherAlert> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(WeatherAlert::getAttractionId, attractionIds);
        wrapper.ge(WeatherAlert::getEndTime, LocalDateTime.now());
        wrapper.orderByDesc(WeatherAlert::getSeverity);
        
        return weatherAlertMapper.selectList(wrapper);
    }
    
    @Override
    public List<AttractionVO> getAllAttractions() {
        log.info("Getting all attractions");
        
        LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attraction::getStatus, "open");
        wrapper.orderByDesc(Attraction::getRating);
        
        List<Attraction> attractions = attractionMapper.selectList(wrapper);
        
        return attractions.stream()
                .map(this::convertToAttractionVO)
                .collect(Collectors.toList());
    }
    
    // 辅助方法
    
    private RouteVO convertToRouteVO(Route route) {
        RouteVO vo = new RouteVO();
        BeanUtils.copyProperties(route, vo);
        
        // 解析tags
        if (route.getTags() != null && !route.getTags().isEmpty()) {
            try {
                vo.setTags(objectMapper.readValue(route.getTags(), new TypeReference<List<String>>() {}));
            } catch (Exception e) {
                log.error("Failed to parse tags", e);
                vo.setTags(Collections.emptyList());
            }
        }
        
        // 加载景点信息
        LambdaQueryWrapper<RouteAttraction> raWrapper = new LambdaQueryWrapper<>();
        raWrapper.eq(RouteAttraction::getRouteId, route.getId());
        raWrapper.orderByAsc(RouteAttraction::getDayNum, RouteAttraction::getOrderNum);
        List<RouteAttraction> routeAttractions = routeAttractionMapper.selectList(raWrapper);
        
        List<AttractionVO> attractionVOs = new ArrayList<>();
        for (RouteAttraction ra : routeAttractions) {
            Attraction attraction = attractionMapper.selectById(ra.getAttractionId());
            if (attraction != null) {
                AttractionVO attractionVO = convertToAttractionVO(attraction);
                attractionVO.setDayNum(ra.getDayNum());
                attractionVO.setOrderNum(ra.getOrderNum());
                attractionVO.setVisitTime(ra.getVisitTime());
                attractionVO.setNotes(ra.getNotes());
                attractionVOs.add(attractionVO);
            }
        }
        vo.setAttractions(attractionVOs);
        
        // 加载交通信息
        LambdaQueryWrapper<Transportation> tWrapper = new LambdaQueryWrapper<>();
        tWrapper.eq(Transportation::getRouteId, route.getId());
        List<Transportation> transportations = transportationMapper.selectList(tWrapper);
        
        List<TransportationVO> transportationVOs = transportations.stream()
                .map(this::convertToTransportationVO)
                .collect(Collectors.toList());
        vo.setTransportations(transportationVOs);
        
        // 加载住宿信息
        LambdaQueryWrapper<Accommodation> aWrapper = new LambdaQueryWrapper<>();
        aWrapper.eq(Accommodation::getRouteId, route.getId());
        aWrapper.orderByAsc(Accommodation::getDayNum);
        List<Accommodation> accommodations = accommodationMapper.selectList(aWrapper);
        
        List<AccommodationVO> accommodationVOs = accommodations.stream()
                .map(this::convertToAccommodationVO)
                .collect(Collectors.toList());
        vo.setAccommodations(accommodationVOs);
        
        return vo;
    }
    
    private AttractionVO convertToAttractionVO(Attraction attraction) {
        AttractionVO vo = new AttractionVO();
        BeanUtils.copyProperties(attraction, vo);
        
        // 解析images
        if (attraction.getImages() != null && !attraction.getImages().isEmpty()) {
            try {
                vo.setImages(objectMapper.readValue(attraction.getImages(), new TypeReference<List<String>>() {}));
            } catch (Exception e) {
                log.error("Failed to parse images", e);
                vo.setImages(Collections.emptyList());
            }
        }
        
        return vo;
    }
    
    private TransportationVO convertToTransportationVO(Transportation transportation) {
        TransportationVO vo = new TransportationVO();
        BeanUtils.copyProperties(transportation, vo);
        return vo;
    }
    
    private AccommodationVO convertToAccommodationVO(Accommodation accommodation) {
        AccommodationVO vo = new AccommodationVO();
        BeanUtils.copyProperties(accommodation, vo);
        return vo;
    }
    
    private ItineraryVO convertToItineraryVO(UserItinerary itinerary) {
        ItineraryVO vo = new ItineraryVO();
        BeanUtils.copyProperties(itinerary, vo);
        
        // 加载路线信息
        if (itinerary.getRouteId() != null) {
            Route route = routeMapper.selectById(itinerary.getRouteId());
            if (route != null) {
                vo.setRoute(convertToRouteVO(route));
            }
        }
        
        return vo;
    }
    
    private String generateBookingNo() {
        return "TB" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }
    
    @Override
    public RouteOptimizationResult optimizeRoute(Long routeId, String strategy) {
        log.info("Optimizing route: {}, strategy: {}", routeId, strategy);
        
        Route route = routeMapper.selectById(routeId);
        if (route == null) {
            throw new RuntimeException("路线不存在");
        }
        
        // 获取路线的所有景点
        LambdaQueryWrapper<RouteAttraction> raWrapper = new LambdaQueryWrapper<>();
        raWrapper.eq(RouteAttraction::getRouteId, routeId);
        raWrapper.orderByAsc(RouteAttraction::getDayNum, RouteAttraction::getOrderNum);
        List<RouteAttraction> routeAttractions = routeAttractionMapper.selectList(raWrapper);
        
        List<Attraction> attractions = new ArrayList<>();
        for (RouteAttraction ra : routeAttractions) {
            Attraction attraction = attractionMapper.selectById(ra.getAttractionId());
            if (attraction != null) {
                attractions.add(attraction);
            }
        }
        
        RouteOptimizationResult result = new RouteOptimizationResult();
        result.setOptimizationStrategy(strategy != null ? strategy : "shortest_distance");
        
        // 根据策略优化路线
        if ("shortest_distance".equals(result.getOptimizationStrategy())) {
            // 最短距离策略：使用贪心算法
            result.setOptimizedAttractionOrder(optimizeByShortestDistance(attractions));
        } else if ("lowest_cost".equals(result.getOptimizationStrategy())) {
            // 最低费用策略
            result.setOptimizedAttractionOrder(optimizeByLowestCost(attractions));
        } else {
            // 默认按原顺序
            result.setOptimizedAttractionOrder(attractions.stream()
                    .map(Attraction::getId)
                    .collect(Collectors.toList()));
        }
        
        // 计算总距离、时长和费用
        calculateRouteMetrics(result, attractions);
        
        return result;
    }
    
    /**
     * 按最短距离优化路线（贪心算法）
     */
    private List<Long> optimizeByShortestDistance(List<Attraction> attractions) {
        if (attractions.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<Long> optimizedOrder = new ArrayList<>();
        List<Attraction> remaining = new ArrayList<>(attractions);
        
        // 从第一个景点开始
        Attraction current = remaining.remove(0);
        optimizedOrder.add(current.getId());
        
        // 贪心选择最近的下一个景点
        while (!remaining.isEmpty()) {
            Attraction nearest = findNearestAttraction(current, remaining);
            optimizedOrder.add(nearest.getId());
            remaining.remove(nearest);
            current = nearest;
        }
        
        return optimizedOrder;
    }
    
    /**
     * 按最低费用优化路线
     */
    private List<Long> optimizeByLowestCost(List<Attraction> attractions) {
        // 按门票价格排序
        return attractions.stream()
                .sorted(Comparator.comparing(Attraction::getTicketPrice))
                .map(Attraction::getId)
                .collect(Collectors.toList());
    }
    
    /**
     * 找到距离当前景点最近的景点
     */
    private Attraction findNearestAttraction(Attraction current, List<Attraction> candidates) {
        Attraction nearest = candidates.get(0);
        double minDistance = calculateDistance(current, nearest);
        
        for (int i = 1; i < candidates.size(); i++) {
            Attraction candidate = candidates.get(i);
            double distance = calculateDistance(current, candidate);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = candidate;
            }
        }
        
        return nearest;
    }
    
    /**
     * 计算两个景点之间的距离（使用Haversine公式）
     */
    private double calculateDistance(Attraction a1, Attraction a2) {
        if (a1.getLatitude() == null || a1.getLongitude() == null 
                || a2.getLatitude() == null || a2.getLongitude() == null) {
            return 100.0; // 默认距离
        }
        
        double lat1 = a1.getLatitude().doubleValue();
        double lon1 = a1.getLongitude().doubleValue();
        double lat2 = a2.getLatitude().doubleValue();
        double lon2 = a2.getLongitude().doubleValue();
        
        double R = 6371; // 地球半径（公里）
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
    
    /**
     * 计算路线指标
     */
    private void calculateRouteMetrics(RouteOptimizationResult result, List<Attraction> attractions) {
        BigDecimal totalCost = BigDecimal.ZERO;
        int totalDuration = 0;
        double totalDistance = 0.0;
        
        Map<Long, Attraction> attractionMap = attractions.stream()
                .collect(Collectors.toMap(Attraction::getId, a -> a));
        
        List<Long> order = result.getOptimizedAttractionOrder();
        for (int i = 0; i < order.size(); i++) {
            Attraction attraction = attractionMap.get(order.get(i));
            if (attraction != null) {
                totalCost = totalCost.add(attraction.getTicketPrice());
                totalDuration += attraction.getVisitDuration();
                
                if (i > 0) {
                    Attraction prev = attractionMap.get(order.get(i - 1));
                    if (prev != null) {
                        totalDistance += calculateDistance(prev, attraction);
                    }
                }
            }
        }
        
        result.setTotalCost(totalCost);
        result.setTotalDuration(totalDuration);
        result.setTotalDistance(BigDecimal.valueOf(totalDistance).setScale(2, RoundingMode.HALF_UP));
    }
    
    @Override
    public ElectronicItinerary generateElectronicItinerary(Long itineraryId) {
        log.info("Generating electronic itinerary for id: {}", itineraryId);
        
        UserItinerary itinerary = userItineraryMapper.selectById(itineraryId);
        if (itinerary == null) {
            throw new RuntimeException("行程不存在");
        }
        
        Route route = routeMapper.selectById(itinerary.getRouteId());
        if (route == null) {
            throw new RuntimeException("路线不存在");
        }
        
        ElectronicItinerary electronicItinerary = new ElectronicItinerary();
        electronicItinerary.setItineraryNo("IT" + itineraryId + System.currentTimeMillis());
        electronicItinerary.setTitle(itinerary.getTitle());
        electronicItinerary.setStartDate(itinerary.getStartDate());
        electronicItinerary.setEndDate(itinerary.getEndDate());
        electronicItinerary.setTotalDays(route.getDays());
        electronicItinerary.setTotalCost(route.getEstimatedCost());
        
        // 生成每日计划
        List<ElectronicItinerary.DailyPlan> dailyPlans = generateDailyPlans(route, itinerary);
        electronicItinerary.setDailyPlans(dailyPlans);
        
        // 添加重要提示
        List<String> notes = Arrays.asList(
                "请提前预约景区门票，避免排队等候",
                "建议携带身份证件，部分景区需要实名登记",
                "注意天气变化，做好防晒或防雨准备",
                "遵守景区规定，文明游览",
                "保管好个人财物，注意安全"
        );
        electronicItinerary.setImportantNotes(notes);
        
        // 生成二维码（实际应用中应该生成真实的二维码）
        electronicItinerary.setQrCode("QR_" + electronicItinerary.getItineraryNo());
        
        return electronicItinerary;
    }
    
    /**
     * 生成每日计划
     */
    private List<ElectronicItinerary.DailyPlan> generateDailyPlans(Route route, UserItinerary itinerary) {
        List<ElectronicItinerary.DailyPlan> dailyPlans = new ArrayList<>();
        
        // 获取路线的景点
        LambdaQueryWrapper<RouteAttraction> raWrapper = new LambdaQueryWrapper<>();
        raWrapper.eq(RouteAttraction::getRouteId, route.getId());
        raWrapper.orderByAsc(RouteAttraction::getDayNum, RouteAttraction::getOrderNum);
        List<RouteAttraction> routeAttractions = routeAttractionMapper.selectList(raWrapper);
        
        // 获取住宿信息
        LambdaQueryWrapper<Accommodation> accWrapper = new LambdaQueryWrapper<>();
        accWrapper.eq(Accommodation::getRouteId, route.getId());
        accWrapper.orderByAsc(Accommodation::getDayNum);
        List<Accommodation> accommodations = accommodationMapper.selectList(accWrapper);
        Map<Integer, Accommodation> accommodationMap = accommodations.stream()
                .collect(Collectors.toMap(Accommodation::getDayNum, a -> a));
        
        // 按天分组
        Map<Integer, List<RouteAttraction>> dayGroups = routeAttractions.stream()
                .collect(Collectors.groupingBy(RouteAttraction::getDayNum));
        
        for (int day = 1; day <= route.getDays(); day++) {
            ElectronicItinerary.DailyPlan dailyPlan = new ElectronicItinerary.DailyPlan();
            dailyPlan.setDayNum(day);
            dailyPlan.setDate(itinerary.getStartDate().plusDays(day - 1));
            
            List<ElectronicItinerary.ActivityItem> activities = new ArrayList<>();
            BigDecimal dailyCost = BigDecimal.ZERO;
            
            // 添加景点活动
            List<RouteAttraction> dayAttractions = dayGroups.get(day);
            if (dayAttractions != null) {
                for (RouteAttraction ra : dayAttractions) {
                    Attraction attraction = attractionMapper.selectById(ra.getAttractionId());
                    if (attraction != null) {
                        ElectronicItinerary.ActivityItem activity = new ElectronicItinerary.ActivityItem();
                        activity.setTime(ra.getVisitTime() != null ? ra.getVisitTime() : "全天");
                        activity.setType("attraction");
                        activity.setName(attraction.getName());
                        activity.setLocation(attraction.getAddress());
                        activity.setCost(attraction.getTicketPrice());
                        activity.setNotes(ra.getNotes());
                        activities.add(activity);
                        dailyCost = dailyCost.add(attraction.getTicketPrice());
                    }
                }
            }
            
            // 添加用餐建议
            ElectronicItinerary.ActivityItem lunch = new ElectronicItinerary.ActivityItem();
            lunch.setTime("12:00-13:00");
            lunch.setType("meal");
            lunch.setName("午餐");
            lunch.setLocation("景区附近餐厅");
            lunch.setCost(new BigDecimal("50"));
            lunch.setNotes("建议品尝当地特色美食");
            activities.add(lunch);
            dailyCost = dailyCost.add(lunch.getCost());
            
            ElectronicItinerary.ActivityItem dinner = new ElectronicItinerary.ActivityItem();
            dinner.setTime("18:00-19:00");
            dinner.setType("meal");
            dinner.setName("晚餐");
            dinner.setLocation("酒店附近餐厅");
            dinner.setCost(new BigDecimal("60"));
            dinner.setNotes("建议提前预订");
            activities.add(dinner);
            dailyCost = dailyCost.add(dinner.getCost());
            
            dailyPlan.setActivities(activities);
            dailyPlan.setDailyCost(dailyCost);
            
            // 设置住宿
            Accommodation accommodation = accommodationMap.get(day);
            if (accommodation != null) {
                dailyPlan.setAccommodation(accommodation.getName() + " - " + accommodation.getPriceRange());
            }
            
            dailyPlans.add(dailyPlan);
        }
        
        return dailyPlans;
    }
    
    @Override
    public List<WeatherInfo> getWeatherForecast(Long attractionId, Integer days) {
        log.info("Getting weather forecast for attraction: {}, days: {}", attractionId, days);
        
        Attraction attraction = attractionMapper.selectById(attractionId);
        if (attraction == null) {
            throw new RuntimeException("景点不存在");
        }
        
        // 模拟天气数据（实际应用中应该调用真实的天气API）
        List<WeatherInfo> weatherInfos = new ArrayList<>();
        String[] weathers = {"晴", "多云", "阴", "小雨", "晴"};
        String[] temperatures = {"18-25℃", "16-23℃", "15-22℃", "14-20℃", "17-24℃"};
        
        for (int i = 0; i < (days != null ? days : 3); i++) {
            WeatherInfo info = new WeatherInfo();
            info.setDate(LocalDate.now().plusDays(i));
            info.setWeather(weathers[i % weathers.length]);
            info.setTemperature(temperatures[i % temperatures.length]);
            info.setWindDirection("东南风");
            info.setWindPower("3-4级");
            info.setHumidity("60%");
            
            if (info.getWeather().contains("雨")) {
                info.setSuggestion("建议携带雨具，注意防滑");
            } else {
                info.setSuggestion("天气适宜游览，注意防晒");
            }
            
            weatherInfos.add(info);
        }
        
        return weatherInfos;
    }
    
    @Override
    public void pushItineraryAdjustment(Long itineraryId, String reason, String suggestion) {
        log.info("Pushing itinerary adjustment for id: {}, reason: {}, suggestion: {}", 
                itineraryId, reason, suggestion);
        
        UserItinerary itinerary = userItineraryMapper.selectById(itineraryId);
        if (itinerary == null) {
            throw new RuntimeException("行程不存在");
        }
        
        // 创建预警记录
        Route route = routeMapper.selectById(itinerary.getRouteId());
        if (route != null) {
            LambdaQueryWrapper<RouteAttraction> raWrapper = new LambdaQueryWrapper<>();
            raWrapper.eq(RouteAttraction::getRouteId, route.getId());
            List<RouteAttraction> routeAttractions = routeAttractionMapper.selectList(raWrapper);
            
            for (RouteAttraction ra : routeAttractions) {
                WeatherAlert alert = new WeatherAlert();
                alert.setAttractionId(ra.getAttractionId());
                alert.setAlertType("event");
                alert.setSeverity("medium");
                alert.setTitle("行程调整建议");
                alert.setContent(reason + " - " + suggestion);
                alert.setStartTime(LocalDateTime.now());
                alert.setEndTime(LocalDateTime.now().plusDays(7));
                weatherAlertMapper.insert(alert);
            }
        }
        
        // 实际应用中应该通过推送服务发送通知给用户
        log.info("Itinerary adjustment notification sent to user: {}", itinerary.getUserId());
    }
}
