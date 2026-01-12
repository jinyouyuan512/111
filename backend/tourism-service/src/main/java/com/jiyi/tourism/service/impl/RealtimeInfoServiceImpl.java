package com.jiyi.tourism.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiyi.tourism.dto.*;
import com.jiyi.tourism.entity.Attraction;
import com.jiyi.tourism.mapper.AttractionMapper;
import com.jiyi.tourism.service.RealtimeInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RealtimeInfoServiceImpl implements RealtimeInfoService {
    
    private final AttractionMapper attractionMapper;
    
    private static final Map<String, String> SPOT_ICONS = new HashMap<>();
    
    static {
        SPOT_ICONS.put("西柏坡纪念馆", "🏛️");
        SPOT_ICONS.put("狼牙山", "⛰️");
        SPOT_ICONS.put("冉庄地道战遗址", "🚇");
        SPOT_ICONS.put("李大钊纪念馆", "📚");
        SPOT_ICONS.put("白求恩柯棣华纪念馆", "🏥");
        SPOT_ICONS.put("华北军区烈士陵园", "🎖️");
        SPOT_ICONS.put("白洋淀雁翎队纪念馆", "🚤");
        SPOT_ICONS.put("塞罕坝展览馆", "🌲");
    }
    
    @Override
    public List<SpotWeatherVO> getSpotWeather(List<String> spotNames) {
        log.info("Getting weather for spots: {}", spotNames);
        
        List<SpotWeatherVO> weatherList = new ArrayList<>();
        
        // 如果没有指定景点，获取所有红色景点
        if (spotNames == null || spotNames.isEmpty()) {
            LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Attraction::getStatus, "open");
            wrapper.eq(Attraction::getCategory, "革命遗址");
            List<Attraction> attractions = attractionMapper.selectList(wrapper);
            spotNames = new ArrayList<>();
            for (Attraction a : attractions) {
                spotNames.add(a.getName());
            }
        }
        
        // 模拟天气数据（实际应调用天气API）
        String[] conditions = {"晴", "多云", "阴", "小雨", "晴"};
        String[] suggestions = {
            "天气晴朗，适合出行",
            "多云天气，温度适宜",
            "阴天，建议携带雨具",
            "有小雨，注意防滑",
            "天气良好，适合游览"
        };
        
        Random random = new Random();
        String today = LocalDate.now().toString();
        
        for (String spotName : spotNames) {
            SpotWeatherVO weather = new SpotWeatherVO();
            weather.setSpotName(spotName);
            weather.setDate(today);
            
            int idx = random.nextInt(conditions.length);
            weather.setCondition(conditions[idx]);
            
            SpotWeatherVO.TemperatureRange temp = new SpotWeatherVO.TemperatureRange();
            temp.setMin(random.nextInt(5) + 3);
            temp.setMax(random.nextInt(8) + 12);
            weather.setTemperature(temp);
            
            weather.setHumidity(random.nextInt(30) + 40);
            weather.setSuggestion(suggestions[idx]);
            weather.setWindDirection("东南风");
            weather.setWindPower("3-4级");
            weather.setAqi(random.nextInt(50) + 30);
            
            weatherList.add(weather);
        }
        
        return weatherList;
    }
    
    @Override
    public List<CrowdInfoVO> getCrowdInfo() {
        log.info("Getting crowd info");
        
        List<CrowdInfoVO> crowdList = new ArrayList<>();
        
        LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attraction::getStatus, "open");
        wrapper.eq(Attraction::getCategory, "革命遗址");
        List<Attraction> attractions = attractionMapper.selectList(wrapper);
        
        Random random = new Random();
        String[] levels = {"low", "medium", "high"};
        String[] levelTexts = {"人少", "适中", "较多"};
        String[] bestTimes = {"全天", "上午", "下午", "14:00-16:00", "8:00-10:00"};
        
        for (Attraction attraction : attractions) {
            CrowdInfoVO crowd = new CrowdInfoVO();
            crowd.setSpotId(attraction.getId());
            crowd.setName(attraction.getName());
            crowd.setIcon(SPOT_ICONS.getOrDefault(attraction.getName(), "🏛️"));
            
            int percent = random.nextInt(70) + 20;
            crowd.setPercent(percent);
            
            int levelIdx;
            if (percent < 40) {
                levelIdx = 0;
            } else if (percent < 70) {
                levelIdx = 1;
            } else {
                levelIdx = 2;
            }
            
            crowd.setLevel(levels[levelIdx]);
            crowd.setLevelText(levelTexts[levelIdx]);
            crowd.setWaitTime(levelIdx == 0 ? 0 : (levelIdx == 1 ? 15 : 30));
            crowd.setBestTime(bestTimes[random.nextInt(bestTimes.length)]);
            crowd.setMaxCapacity(5000);
            crowd.setCurrentVisitors((int) (5000 * percent / 100.0));
            
            crowdList.add(crowd);
        }
        
        return crowdList;
    }
    
    @Override
    public List<TravelTipVO> getTravelTips() {
        log.info("Getting travel tips");
        
        List<TravelTipVO> tips = new ArrayList<>();
        
        TravelTipVO tip1 = new TravelTipVO();
        tip1.setIcon("👔");
        tip1.setTitle("穿衣建议");
        tip1.setContent("今日气温3-12℃，建议穿保暖外套");
        tip1.setType("info");
        tips.add(tip1);
        
        TravelTipVO tip2 = new TravelTipVO();
        tip2.setIcon("🚗");
        tip2.setTitle("出行提示");
        tip2.setContent("西柏坡高速畅通，建议上午出发");
        tip2.setType("success");
        tips.add(tip2);
        
        TravelTipVO tip3 = new TravelTipVO();
        tip3.setIcon("📸");
        tip3.setTitle("摄影推荐");
        tip3.setContent("今日光线充足，适合拍摄");
        tip3.setType("info");
        tips.add(tip3);
        
        TravelTipVO tip4 = new TravelTipVO();
        tip4.setIcon("⚠️");
        tip4.setTitle("注意事项");
        tip4.setContent("部分景区需要提前预约，请提前规划");
        tip4.setType("warning");
        tips.add(tip4);
        
        return tips;
    }
    
    @Override
    public SpotStatusVO getSpotStatus(Long spotId) {
        log.info("Getting status for spot: {}", spotId);
        
        Attraction attraction = attractionMapper.selectById(spotId);
        if (attraction == null) {
            throw new RuntimeException("景点不存在");
        }
        
        SpotStatusVO status = new SpotStatusVO();
        status.setSpotId(spotId);
        status.setName(attraction.getName());
        status.setIsOpen("open".equals(attraction.getStatus()));
        
        String[] hours = attraction.getOpeningHours().split("-");
        status.setOpenTime(hours.length > 0 ? hours[0] : "09:00");
        status.setCloseTime(hours.length > 1 ? hours[1] : "17:00");
        
        Random random = new Random();
        int percent = random.nextInt(70) + 20;
        status.setMaxCapacity(5000);
        status.setCurrentVisitors((int) (5000 * percent / 100.0));
        
        if (percent < 40) {
            status.setCrowdLevel("low");
            status.setWaitTime(0);
        } else if (percent < 70) {
            status.setCrowdLevel("medium");
            status.setWaitTime(15);
        } else {
            status.setCrowdLevel("high");
            status.setWaitTime(30);
        }
        
        status.setNotices(Arrays.asList(
            "请携带身份证件",
            "遵守景区规定，文明游览"
        ));
        
        // 获取天气
        List<SpotWeatherVO> weatherList = getSpotWeather(Arrays.asList(attraction.getName()));
        if (!weatherList.isEmpty()) {
            status.setWeather(weatherList.get(0));
        }
        
        return status;
    }
    
    @Override
    public DashboardVO getDashboard() {
        log.info("Getting dashboard data");
        
        DashboardVO dashboard = new DashboardVO();
        
        Random random = new Random();
        dashboard.setTodayVisitors(random.nextInt(5000) + 8000);
        dashboard.setActiveUsers(random.nextInt(500) + 200);
        dashboard.setTotalBookings(random.nextInt(500) + 200);
        
        // 热门景点
        List<DashboardVO.PopularSpot> popularSpots = new ArrayList<>();
        String[] spotNames = {"西柏坡纪念馆", "白洋淀雁翎队纪念馆", "狼牙山"};
        int[] visits = {3560, 2890, 2340};
        for (int i = 0; i < spotNames.length; i++) {
            DashboardVO.PopularSpot spot = new DashboardVO.PopularSpot();
            spot.setName(spotNames[i]);
            spot.setVisits(visits[i]);
            popularSpots.add(spot);
        }
        dashboard.setPopularSpots(popularSpots);
        
        // 最近提醒
        List<DashboardVO.Alert> alerts = new ArrayList<>();
        DashboardVO.Alert alert = new DashboardVO.Alert();
        alert.setType("weather");
        alert.setMessage("塞罕坝地区明日有降雪");
        alert.setTime("10分钟前");
        alerts.add(alert);
        dashboard.setRecentAlerts(alerts);
        
        // 系统健康
        DashboardVO.SystemHealth health = new DashboardVO.SystemHealth();
        health.setWeatherApi("ok");
        health.setTrafficApi("ok");
        health.setAiService("ok");
        health.setDatabase("ok");
        dashboard.setSystemHealth(health);
        
        return dashboard;
    }
}
