package com.jiyi.tourism.service;

import com.jiyi.tourism.dto.RouteVO;
import com.jiyi.tourism.dto.TravelPreferences;
import com.jiyi.tourism.entity.*;
import com.jiyi.tourism.mapper.*;
import com.jiyi.tourism.service.impl.TourismServiceImpl;
import net.jqwik.api.*;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * 旅游服务属性测试
 * 使用基于属性的测试验证系统的正确性属性
 */
class TourismServicePropertyTest {
    
    /**
     * Feature: jiyi-red-route, Property 9: 路线生成非空性
     * 
     * 属性: 对于任意有效的旅游偏好输入（出发地、天数、预算、兴趣点），系统应该返回至少一条旅游路线
     * 验证需求: 3.1
     * 
     * 这个属性测试验证了路线生成的非空性。无论用户输入什么样的有效旅游偏好，
     * 系统都必须能够生成至少一条旅游路线方案。这确保了系统的可用性和用户体验，
     * 避免用户在输入偏好后得到空结果的情况。
     */
    @Property(tries = 100)
    @Label("路线生成非空性 - 有效的旅游偏好必须生成至少一条路线")
    void routeGenerationNonEmpty(
            @ForAll("validTravelPreferences") TravelPreferences preferences,
            @ForAll("routesForPreferences") List<Route> mockRoutes) {
        // Arrange: 创建mock并设置行为
        RouteMapper routeMapper = Mockito.mock(RouteMapper.class);
        AttractionMapper attractionMapper = Mockito.mock(AttractionMapper.class);
        RouteAttractionMapper routeAttractionMapper = Mockito.mock(RouteAttractionMapper.class);
        TransportationMapper transportationMapper = Mockito.mock(TransportationMapper.class);
        AccommodationMapper accommodationMapper = Mockito.mock(AccommodationMapper.class);
        UserItineraryMapper userItineraryMapper = Mockito.mock(UserItineraryMapper.class);
        TicketBookingMapper ticketBookingMapper = Mockito.mock(TicketBookingMapper.class);
        WeatherAlertMapper weatherAlertMapper = Mockito.mock(WeatherAlertMapper.class);
        
        TourismService tourismService = new TourismServiceImpl(
                routeMapper, attractionMapper, routeAttractionMapper,
                transportationMapper, accommodationMapper, userItineraryMapper,
                ticketBookingMapper, weatherAlertMapper, new com.fasterxml.jackson.databind.ObjectMapper());
        
        // 设置路线查询返回
        when(routeMapper.selectList(any())).thenReturn(mockRoutes);
        
        // Act: 调用服务生成路线
        List<RouteVO> routes = tourismService.generateRoutes(preferences);
        
        // Assert: 验证返回的路线列表非空
        // 对于任意有效的旅游偏好输入，系统应该返回至少一条旅游路线
        assertThat(routes)
                .as("对于有效的旅游偏好（出发地: %s, 天数: %d, 预算: %s, 兴趣: %s），系统必须返回至少一条路线",
                        preferences.getStartLocation(),
                        preferences.getDays(),
                        preferences.getBudget(),
                        preferences.getInterests())
                .isNotNull()
                .isNotEmpty()
                .hasSizeGreaterThanOrEqualTo(1);
        
        // 额外验证：确保返回的路线都是有效的
        for (RouteVO route : routes) {
            assertThat(route.getId())
                    .as("路线ID必须存在")
                    .isNotNull();
            
            assertThat(route.getName())
                    .as("路线名称必须存在且非空")
                    .isNotNull()
                    .isNotBlank();
            
            assertThat(route.getDays())
                    .as("路线天数必须存在且为正数")
                    .isNotNull()
                    .isPositive();
            
            assertThat(route.getEstimatedCost())
                    .as("路线预估费用必须存在且非负")
                    .isNotNull()
                    .isGreaterThanOrEqualTo(BigDecimal.ZERO);
        }
    }
    
    /**
     * 生成有效旅游偏好的提供者
     * 
     * 这个提供者生成各种有效的旅游偏好组合，包括：
     * - 出发地：河北省内的主要城市
     * - 天数：1-7天的合理旅游天数
     * - 预算：500-5000元的合理预算范围
     * - 兴趣点：红色文化相关的兴趣标签
     * - 难度：简单、中等、困难
     * - 季节：春夏秋冬四季
     * 
     * 通过随机生成不同的偏好组合，我们可以验证系统在各种输入下都能生成路线。
     */
    @Provide
    Arbitrary<TravelPreferences> validTravelPreferences() {
        return Combinators.combine(
                // 出发地：河北省内的主要城市
                Arbitraries.of("石家庄", "保定", "唐山", "邯郸", "秦皇岛", "张家口", "承德", "廊坊", "沧州", "衡水", "邢台"),
                
                // 天数：1-7天的合理旅游天数
                Arbitraries.integers().between(1, 7),
                
                // 预算：500-5000元的合理预算范围
                Arbitraries.integers().between(500, 5000)
                        .map(amount -> new BigDecimal(amount)),
                
                // 兴趣点：红色文化相关的兴趣标签（1-3个）
                Arbitraries.of("红色教育", "历史文化", "革命遗址", "英雄人物", "党史学习", "爱国主义")
                        .list().ofMinSize(1).ofMaxSize(3),
                
                // 难度：简单、中等、困难
                Arbitraries.of("easy", "medium", "hard"),
                
                // 季节：春夏秋冬
                Arbitraries.of("春季", "夏季", "秋季", "冬季")
        ).as((startLocation, days, budget, interests, difficulty, season) -> {
            TravelPreferences preferences = new TravelPreferences();
            preferences.setStartLocation(startLocation);
            preferences.setDays(days);
            preferences.setBudget(budget);
            preferences.setInterests(interests);
            preferences.setDifficulty(difficulty);
            preferences.setSeason(season);
            return preferences;
        });
    }
    
    /**
     * 生成路线列表的提供者
     * 
     * 这个提供者生成1到5条路线，每条路线都有完整的必需字段。
     * 通过随机生成不同的路线数据，我们可以验证系统在各种数据库状态下的行为。
     */
    @Provide
    Arbitrary<List<Route>> routesForPreferences() {
        return Arbitraries.integers().between(1, 5)
                .flatMap(size -> {
                    Arbitrary<Route> routeArbitrary = Combinators.combine(
                            Arbitraries.longs().greaterOrEqual(1L),
                            Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(50),
                            Arbitraries.strings().alpha().ofMinLength(10).ofMaxLength(200),
                            Arbitraries.integers().between(1, 7),
                            Arbitraries.integers().between(500, 5000)
                                    .map(amount -> new BigDecimal(amount)),
                            Arbitraries.of("easy", "medium", "hard"),
                            Arbitraries.of("春季", "夏季", "秋季", "冬季"),
                            Arbitraries.of("红色教育,历史文化", "革命遗址,英雄人物", "党史学习,爱国主义")
                    ).as((id, name, description, days, cost, difficulty, season, tags) -> {
                        Route route = new Route();
                        route.setId(id);
                        route.setName(name);
                        route.setDescription(description);
                        route.setDays(days);
                        route.setEstimatedCost(cost);
                        route.setDifficulty(difficulty);
                        route.setSeason(season);
                        route.setTags(tags);
                        route.setCoverImage("https://example.com/image.jpg");
                        route.setStatus("active");
                        route.setViewCount(0);
                        route.setBookingCount(0);
                        route.setAttractions(new ArrayList<>());
                        route.setTransportations(new ArrayList<>());
                        route.setAccommodations(new ArrayList<>());
                        return route;
                    });
                    
                    return routeArbitrary.list().ofSize(size);
                });
    }
}
