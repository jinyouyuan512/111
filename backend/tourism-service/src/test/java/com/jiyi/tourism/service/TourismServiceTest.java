package com.jiyi.tourism.service;

import com.jiyi.tourism.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TourismServiceTest {
    
    @Autowired
    private TourismService tourismService;
    
    @Test
    public void testGenerateRoutesWithRecommendation() {
        // 测试智能推荐算法
        TravelPreferences preferences = new TravelPreferences();
        preferences.setDays(3);
        preferences.setBudget(new BigDecimal("1500"));
        preferences.setInterests(Arrays.asList("红色教育", "历史文化"));
        preferences.setDifficulty("easy");
        preferences.setSeason("春季");
        
        List<RouteVO> routes = tourismService.generateRoutes(preferences);
        
        assertNotNull(routes);
        assertFalse(routes.isEmpty());
        
        // 验证推荐分数已计算
        for (RouteVO route : routes) {
            assertNotNull(route.getRecommendationScore());
            assertTrue(route.getRecommendationScore() >= 0);
        }
        
        // 验证按推荐分数排序
        for (int i = 0; i < routes.size() - 1; i++) {
            assertTrue(routes.get(i).getRecommendationScore() >= routes.get(i + 1).getRecommendationScore());
        }
        
        System.out.println("✓ 智能推荐算法测试通过");
    }
    
    @Test
    public void testOptimizeRoute() {
        // 测试路线优化
        Long routeId = 1L;
        
        // 测试最短距离策略
        RouteOptimizationResult result1 = tourismService.optimizeRoute(routeId, "shortest_distance");
        assertNotNull(result1);
        assertNotNull(result1.getOptimizedAttractionOrder());
        assertNotNull(result1.getTotalDistance());
        assertNotNull(result1.getTotalCost());
        assertEquals("shortest_distance", result1.getOptimizationStrategy());
        
        System.out.println("优化结果 (最短距离):");
        System.out.println("  景点顺序: " + result1.getOptimizedAttractionOrder());
        System.out.println("  总距离: " + result1.getTotalDistance() + " km");
        System.out.println("  总时长: " + result1.getTotalDuration() + " 分钟");
        System.out.println("  总费用: " + result1.getTotalCost() + " 元");
        
        // 测试最低费用策略
        RouteOptimizationResult result2 = tourismService.optimizeRoute(routeId, "lowest_cost");
        assertNotNull(result2);
        assertEquals("lowest_cost", result2.getOptimizationStrategy());
        
        System.out.println("✓ 路线优化测试通过");
    }
    
    @Test
    public void testGenerateElectronicItinerary() {
        // 先创建一个行程
        ItineraryCreateRequest request = new ItineraryCreateRequest();
        request.setRouteId(1L);
        request.setTitle("我的红色之旅");
        request.setStartDate(LocalDate.now().plusDays(7));
        request.setEndDate(LocalDate.now().plusDays(9));
        request.setNotes("期待这次旅行");
        
        ItineraryVO itinerary = tourismService.saveItinerary(1L, request);
        assertNotNull(itinerary);
        
        // 生成电子行程单
        ElectronicItinerary electronicItinerary = tourismService.generateElectronicItinerary(itinerary.getId());
        
        assertNotNull(electronicItinerary);
        assertNotNull(electronicItinerary.getItineraryNo());
        assertNotNull(electronicItinerary.getTitle());
        assertNotNull(electronicItinerary.getDailyPlans());
        assertFalse(electronicItinerary.getDailyPlans().isEmpty());
        assertNotNull(electronicItinerary.getImportantNotes());
        assertNotNull(electronicItinerary.getQrCode());
        
        System.out.println("电子行程单:");
        System.out.println("  行程编号: " + electronicItinerary.getItineraryNo());
        System.out.println("  标题: " + electronicItinerary.getTitle());
        System.out.println("  总天数: " + electronicItinerary.getTotalDays());
        System.out.println("  总费用: " + electronicItinerary.getTotalCost());
        System.out.println("  每日计划数: " + electronicItinerary.getDailyPlans().size());
        
        // 验证每日计划
        for (ElectronicItinerary.DailyPlan plan : electronicItinerary.getDailyPlans()) {
            assertNotNull(plan.getDate());
            assertNotNull(plan.getActivities());
            assertFalse(plan.getActivities().isEmpty());
            assertTrue(plan.getDailyCost().compareTo(BigDecimal.ZERO) > 0);
        }
        
        System.out.println("✓ 电子行程单生成测试通过");
    }
    
    @Test
    public void testGetWeatherForecast() {
        // 测试天气预报
        Long attractionId = 1L;
        Integer days = 5;
        
        List<WeatherInfo> weatherInfos = tourismService.getWeatherForecast(attractionId, days);
        
        assertNotNull(weatherInfos);
        assertEquals(days, weatherInfos.size());
        
        for (WeatherInfo info : weatherInfos) {
            assertNotNull(info.getDate());
            assertNotNull(info.getWeather());
            assertNotNull(info.getTemperature());
            assertNotNull(info.getSuggestion());
        }
        
        System.out.println("天气预报:");
        for (WeatherInfo info : weatherInfos) {
            System.out.println("  " + info.getDate() + ": " + info.getWeather() + ", " + info.getTemperature());
        }
        
        System.out.println("✓ 天气预报测试通过");
    }
    
    @Test
    public void testPushItineraryAdjustment() {
        // 先创建一个行程
        ItineraryCreateRequest request = new ItineraryCreateRequest();
        request.setRouteId(1L);
        request.setTitle("测试行程");
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(3));
        
        ItineraryVO itinerary = tourismService.saveItinerary(1L, request);
        
        // 推送调整建议
        String reason = "明天有大雨预警";
        String suggestion = "建议将行程推迟一天，或调整为室内景点";
        
        assertDoesNotThrow(() -> {
            tourismService.pushItineraryAdjustment(itinerary.getId(), reason, suggestion);
        });
        
        System.out.println("✓ 行程调整推送测试通过");
    }
}
