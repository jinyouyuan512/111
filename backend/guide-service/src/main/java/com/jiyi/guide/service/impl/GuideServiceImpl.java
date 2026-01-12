package com.jiyi.guide.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiyi.guide.dto.*;
import com.jiyi.guide.entity.*;
import com.jiyi.guide.mapper.*;
import com.jiyi.guide.service.GuideService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuideServiceImpl implements GuideService {
    
    private final ScenicAreaMapper scenicAreaMapper;
    private final PoiMapper poiMapper;
    private final AudioGuideMapper audioGuideMapper;
    private final ArSceneMapper arSceneMapper;
    private final GuideRouteMapper guideRouteMapper;
    private final UserVisitMapper userVisitMapper;
    private final VisitTrackMapper visitTrackMapper;
    private final TravelLogMapper travelLogMapper;
    private final ObjectMapper objectMapper;
    
    private static final double EARTH_RADIUS = 6371.0; // 地球半径（公里）
    
    @Override
    public ScenicAreaVO detectLocation(BigDecimal latitude, BigDecimal longitude) {
        // 查询所有景区
        List<ScenicArea> scenicAreas = scenicAreaMapper.selectList(
            new LambdaQueryWrapper<ScenicArea>()
                .eq(ScenicArea::getStatus, "open")
        );
        
        // 找到最近的景区
        ScenicArea nearestArea = null;
        double minDistance = Double.MAX_VALUE;
        
        for (ScenicArea area : scenicAreas) {
            double distance = calculateDistance(
                latitude.doubleValue(), longitude.doubleValue(),
                area.getLatitude().doubleValue(), area.getLongitude().doubleValue()
            );
            
            // 如果距离小于5公里，认为在景区范围内
            if (distance < 5.0 && distance < minDistance) {
                minDistance = distance;
                nearestArea = area;
            }
        }
        
        if (nearestArea == null) {
            return null;
        }
        
        // 转换为VO并加载景点信息
        ScenicAreaVO vo = new ScenicAreaVO();
        BeanUtils.copyProperties(nearestArea, vo);
        
        // 加载景点列表
        List<Poi> pois = poiMapper.selectList(
            new LambdaQueryWrapper<Poi>()
                .eq(Poi::getScenicAreaId, nearestArea.getId())
        );
        
        vo.setPois(pois.stream().map(this::convertToPoiVO).collect(Collectors.toList()));
        
        return vo;
    }
    
    @Override
    public PoiVO getPoiInfo(Long poiId) {
        Poi poi = poiMapper.selectById(poiId);
        if (poi == null) {
            return null;
        }
        return convertToPoiVO(poi);
    }
    
    @Override
    public AudioGuideVO triggerAudioGuide(Long poiId, String language) {
        AudioGuide audioGuide = audioGuideMapper.selectOne(
            new LambdaQueryWrapper<AudioGuide>()
                .eq(AudioGuide::getPoiId, poiId)
                .eq(AudioGuide::getLanguage, language != null ? language : "zh")
                .last("LIMIT 1")
        );
        
        if (audioGuide == null) {
            return null;
        }
        
        AudioGuideVO vo = new AudioGuideVO();
        BeanUtils.copyProperties(audioGuide, vo);
        return vo;
    }
    
    @Override
    public ArSceneVO loadArScene(String qrCode) {
        // 根据二维码查找景点
        Poi poi = poiMapper.selectOne(
            new LambdaQueryWrapper<Poi>()
                .eq(Poi::getQrCode, qrCode)
        );
        
        if (poi == null) {
            return null;
        }
        
        // 查找AR场景
        ArScene arScene = arSceneMapper.selectOne(
            new LambdaQueryWrapper<ArScene>()
                .eq(ArScene::getPoiId, poi.getId())
                .last("LIMIT 1")
        );
        
        if (arScene == null) {
            return null;
        }
        
        ArSceneVO vo = new ArSceneVO();
        BeanUtils.copyProperties(arScene, vo);
        return vo;
    }
    
    @Override
    public List<GuideRouteVO> getGuideRoutes(Long scenicAreaId) {
        List<GuideRoute> routes = guideRouteMapper.selectList(
            new LambdaQueryWrapper<GuideRoute>()
                .eq(GuideRoute::getScenicAreaId, scenicAreaId)
        );
        
        return routes.stream().map(route -> {
            GuideRouteVO vo = new GuideRouteVO();
            BeanUtils.copyProperties(route, vo);
            return vo;
        }).collect(Collectors.toList());
    }
    
    @Override
    public String planNavigation(NavigationRequest request) {
        Poi targetPoi = poiMapper.selectById(request.getTargetPoiId());
        if (targetPoi == null) {
            return null;
        }
        
        // 简单的导航路径（实际应该调用高德地图API）
        return String.format(
            "{\"start\":{\"lat\":%s,\"lng\":%s},\"end\":{\"lat\":%s,\"lng\":%s},\"distance\":%.2f}",
            request.getStartLatitude(),
            request.getStartLongitude(),
            targetPoi.getLatitude(),
            targetPoi.getLongitude(),
            calculateDistance(
                request.getStartLatitude().doubleValue(),
                request.getStartLongitude().doubleValue(),
                targetPoi.getLatitude().doubleValue(),
                targetPoi.getLongitude().doubleValue()
            )
        );
    }
    
    @Override
    @Transactional
    public Long startVisit(Long userId, Long scenicAreaId) {
        UserVisit visit = new UserVisit();
        visit.setUserId(userId);
        visit.setScenicAreaId(scenicAreaId);
        visit.setVisitDate(LocalDate.now());
        visit.setStartTime(LocalDateTime.now());
        visit.setStatus("ongoing");
        visit.setCreatedAt(LocalDateTime.now());
        
        userVisitMapper.insert(visit);
        return visit.getId();
    }
    
    @Override
    @Transactional
    public void recordTrackPoint(TrackPointRequest request) {
        VisitTrack track = new VisitTrack();
        track.setVisitId(request.getVisitId());
        track.setLatitude(request.getLatitude());
        track.setLongitude(request.getLongitude());
        track.setRecordedAt(LocalDateTime.now());
        
        visitTrackMapper.insert(track);
    }
    
    @Override
    @Transactional
    public void endVisit(Long visitId) {
        UserVisit visit = userVisitMapper.selectById(visitId);
        if (visit != null) {
            visit.setEndTime(LocalDateTime.now());
            visit.setStatus("completed");
            userVisitMapper.updateById(visit);
        }
    }
    
    @Override
    @Transactional
    public TravelLogVO generateTravelLog(Long userId, Long visitId) {
        UserVisit visit = userVisitMapper.selectById(visitId);
        if (visit == null || !visit.getUserId().equals(userId)) {
            return null;
        }
        
        // 获取景区信息
        ScenicArea scenicArea = scenicAreaMapper.selectById(visit.getScenicAreaId());
        
        // 获取轨迹点
        List<VisitTrack> tracks = visitTrackMapper.selectList(
            new LambdaQueryWrapper<VisitTrack>()
                .eq(VisitTrack::getVisitId, visitId)
                .orderByAsc(VisitTrack::getRecordedAt)
        );
        
        // 生成游记
        TravelLog travelLog = new TravelLog();
        travelLog.setUserId(userId);
        travelLog.setVisitId(visitId);
        travelLog.setTitle(scenicArea.getName() + "游记");
        travelLog.setContent(generateTravelContent(scenicArea, visit, tracks));
        travelLog.setImages("[]");
        travelLog.setTrackMapUrl(generateTrackMapUrl(tracks));
        travelLog.setCreatedAt(LocalDateTime.now());
        
        travelLogMapper.insert(travelLog);
        
        return convertToTravelLogVO(travelLog);
    }
    
    @Override
    public List<TravelLogVO> getUserTravelLogs(Long userId) {
        List<TravelLog> logs = travelLogMapper.selectList(
            new LambdaQueryWrapper<TravelLog>()
                .eq(TravelLog::getUserId, userId)
                .orderByDesc(TravelLog::getCreatedAt)
        );
        
        return logs.stream().map(this::convertToTravelLogVO).collect(Collectors.toList());
    }
    
    // 辅助方法
    
    private PoiVO convertToPoiVO(Poi poi) {
        PoiVO vo = new PoiVO();
        BeanUtils.copyProperties(poi, vo);
        return vo;
    }
    
    private TravelLogVO convertToTravelLogVO(TravelLog log) {
        TravelLogVO vo = new TravelLogVO();
        BeanUtils.copyProperties(log, vo);
        
        // 解析图片JSON
        try {
            if (log.getImages() != null && !log.getImages().isEmpty()) {
                vo.setImages(objectMapper.readValue(log.getImages(), new TypeReference<List<String>>() {}));
            } else {
                vo.setImages(new ArrayList<>());
            }
        } catch (Exception e) {
            vo.setImages(new ArrayList<>());
        }
        
        return vo;
    }
    
    /**
     * 计算两点之间的距离（公里）
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS * c;
    }
    
    private String generateTravelContent(ScenicArea scenicArea, UserVisit visit, List<VisitTrack> tracks) {
        StringBuilder content = new StringBuilder();
        content.append("今天游览了").append(scenicArea.getName()).append("。\n\n");
        content.append("游览时间：").append(visit.getStartTime()).append(" 至 ").append(visit.getEndTime()).append("\n");
        content.append("游览轨迹：共记录").append(tracks.size()).append("个位置点\n\n");
        content.append(scenicArea.getDescription());
        return content.toString();
    }
    
    private String generateTrackMapUrl(List<VisitTrack> tracks) {
        // 实际应该生成真实的地图URL，这里返回模拟数据
        return "/api/maps/track/" + System.currentTimeMillis();
    }
}
