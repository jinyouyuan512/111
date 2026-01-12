package com.jiyi.tourism.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiyi.tourism.dto.AudioGuideVO;
import com.jiyi.tourism.dto.RedSpotVO;
import com.jiyi.tourism.entity.Attraction;
import com.jiyi.tourism.entity.AudioGuide;
import com.jiyi.tourism.mapper.AttractionMapper;
import com.jiyi.tourism.mapper.AudioGuideMapper;
import com.jiyi.tourism.service.SpotGuideService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpotGuideServiceImpl implements SpotGuideService {
    
    private final AttractionMapper attractionMapper;
    private final AudioGuideMapper audioGuideMapper;
    
    // 景点图标和渐变色映射
    private static final Map<String, String> SPOT_ICONS = new HashMap<>();
    private static final Map<String, String> SPOT_GRADIENTS = new HashMap<>();
    private static final Map<String, String> SPOT_SLOGANS = new HashMap<>();
    
    static {
        SPOT_ICONS.put("西柏坡纪念馆", "🏛️");
        SPOT_ICONS.put("狼牙山", "⛰️");
        SPOT_ICONS.put("冉庄地道战遗址", "🚇");
        SPOT_ICONS.put("李大钊纪念馆", "📚");
        SPOT_ICONS.put("白求恩柯棣华纪念馆", "🏥");
        SPOT_ICONS.put("华北军区烈士陵园", "🎖️");
        SPOT_ICONS.put("白洋淀雁翎队纪念馆", "🚤");
        SPOT_ICONS.put("塞罕坝展览馆", "🌲");
        
        SPOT_GRADIENTS.put("西柏坡纪念馆", "linear-gradient(135deg, #c41e3a, #8b0000)");
        SPOT_GRADIENTS.put("狼牙山", "linear-gradient(135deg, #2c5530, #1a3a1c)");
        SPOT_GRADIENTS.put("冉庄地道战遗址", "linear-gradient(135deg, #5d4e37, #3d3225)");
        SPOT_GRADIENTS.put("李大钊纪念馆", "linear-gradient(135deg, #1e3a5f, #0d1f33)");
        SPOT_GRADIENTS.put("白求恩柯棣华纪念馆", "linear-gradient(135deg, #2e7d32, #1b5e20)");
        SPOT_GRADIENTS.put("华北军区烈士陵园", "linear-gradient(135deg, #37474f, #263238)");
        SPOT_GRADIENTS.put("白洋淀雁翎队纪念馆", "linear-gradient(135deg, #1890ff, #096dd9)");
        SPOT_GRADIENTS.put("塞罕坝展览馆", "linear-gradient(135deg, #228b22, #006400)");
        
        SPOT_SLOGANS.put("西柏坡纪念馆", "新中国从这里走来");
        SPOT_SLOGANS.put("狼牙山", "英雄壮举，气壮山河");
        SPOT_SLOGANS.put("冉庄地道战遗址", "地下长城，抗战奇迹");
        SPOT_SLOGANS.put("李大钊纪念馆", "铁肩担道义，妙手著文章");
        SPOT_SLOGANS.put("白求恩柯棣华纪念馆", "国际主义精神永存");
        SPOT_SLOGANS.put("华北军区烈士陵园", "英烈千秋，浩气长存");
        SPOT_SLOGANS.put("白洋淀雁翎队纪念馆", "华北明珠，水上传奇");
        SPOT_SLOGANS.put("塞罕坝展览馆", "荒原变林海的绿色奇迹");
    }
    
    @Override
    public List<RedSpotVO> getRedSpots() {
        log.info("Getting all red spots");
        
        LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attraction::getStatus, "open");
        wrapper.eq(Attraction::getCategory, "革命遗址");
        wrapper.orderByDesc(Attraction::getRating);
        
        List<Attraction> attractions = attractionMapper.selectList(wrapper);
        
        return attractions.stream()
                .map(this::convertToRedSpotVO)
                .collect(Collectors.toList());
    }
    
    @Override
    public RedSpotVO getSpotDetail(Long spotId) {
        log.info("Getting spot detail: {}", spotId);
        
        Attraction attraction = attractionMapper.selectById(spotId);
        if (attraction == null) {
            throw new RuntimeException("景点不存在");
        }
        
        RedSpotVO vo = convertToRedSpotVO(attraction);
        
        // 加载语音讲解
        vo.setAudioGuides(getAudioGuides(spotId));
        
        return vo;
    }
    
    @Override
    public List<AudioGuideVO> getAudioGuides(Long spotId) {
        log.info("Getting audio guides for spot: {}", spotId);
        
        if (audioGuideMapper == null) {
            // 如果没有语音讲解表，返回模拟数据
            return generateMockAudioGuides(spotId);
        }
        
        LambdaQueryWrapper<AudioGuide> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AudioGuide::getSpotId, spotId);
        wrapper.orderByAsc(AudioGuide::getOrderNum);
        
        List<AudioGuide> guides = audioGuideMapper.selectList(wrapper);
        
        if (guides.isEmpty()) {
            return generateMockAudioGuides(spotId);
        }
        
        return guides.stream()
                .map(this::convertToAudioGuideVO)
                .collect(Collectors.toList());
    }
    
    @Override
    public AudioGuideVO getAudioGuideDetail(Long guideId) {
        log.info("Getting audio guide detail: {}", guideId);
        
        if (audioGuideMapper == null) {
            AudioGuideVO vo = new AudioGuideVO();
            vo.setId(guideId);
            vo.setTitle("语音讲解");
            vo.setDuration(180);
            vo.setTranscript("欢迎来到这里...");
            return vo;
        }
        
        AudioGuide guide = audioGuideMapper.selectById(guideId);
        if (guide == null) {
            throw new RuntimeException("语音讲解不存在");
        }
        
        return convertToAudioGuideVO(guide);
    }
    
    @Override
    public List<RedSpotVO> searchSpots(String keyword, String category, Boolean freeOnly) {
        log.info("Searching spots: keyword={}, category={}, freeOnly={}", keyword, category, freeOnly);
        
        LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attraction::getStatus, "open");
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Attraction::getName, keyword)
                    .or().like(Attraction::getDescription, keyword));
        }
        
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Attraction::getCategory, category);
        }
        
        if (Boolean.TRUE.equals(freeOnly)) {
            wrapper.eq(Attraction::getTicketPrice, BigDecimal.ZERO);
        }
        
        wrapper.orderByDesc(Attraction::getRating);
        
        List<Attraction> attractions = attractionMapper.selectList(wrapper);
        
        return attractions.stream()
                .map(this::convertToRedSpotVO)
                .collect(Collectors.toList());
    }
    
    private RedSpotVO convertToRedSpotVO(Attraction attraction) {
        RedSpotVO vo = new RedSpotVO();
        vo.setId(attraction.getId());
        vo.setName(attraction.getName());
        vo.setIcon(SPOT_ICONS.getOrDefault(attraction.getName(), "🏛️"));
        vo.setGradient(SPOT_GRADIENTS.getOrDefault(attraction.getName(), "linear-gradient(135deg, #c41e3a, #8b0000)"));
        vo.setSlogan(SPOT_SLOGANS.getOrDefault(attraction.getName(), "红色旅游胜地"));
        vo.setLocation(attraction.getAddress());
        vo.setRating(attraction.getRating());
        vo.setIsFree(attraction.getTicketPrice().compareTo(BigDecimal.ZERO) == 0);
        vo.setNeedReserve(vo.getIsFree()); // 免费景点通常需要预约
        vo.setTags(Arrays.asList("红色文化", "爱国教育"));
        vo.setIntroduction(attraction.getDescription());
        vo.setHistory(generateHistory(attraction.getName()));
        vo.setTips(generateTips(attraction));
        vo.setCategory(attraction.getCategory());
        vo.setTicketPrice(attraction.getTicketPrice());
        vo.setOpeningHours(attraction.getOpeningHours());
        
        return vo;
    }
    
    private String generateHistory(String name) {
        Map<String, String> histories = new HashMap<>();
        histories.put("西柏坡纪念馆", "1948年5月至1949年3月，中共中央在西柏坡指挥了辽沈、淮海、平津三大战役，召开了七届二中全会，为新中国的诞生奠定了基础。");
        histories.put("狼牙山", "1941年9月25日，八路军五名战士为掩护群众和主力部队撤退，在狼牙山顶峰与日军激战后跳崖，三人壮烈牺牲，两人被树枝挂住幸存。");
        histories.put("冉庄地道战遗址", "抗战时期，冉庄人民挖掘了长达16公里的地道网，创造了地道战这一独特的战斗方式，有力打击了日本侵略者。");
        histories.put("李大钊纪念馆", "李大钊是中国共产党的主要创始人之一，最早在中国传播马克思主义，1927年被军阀杀害，年仅38岁。");
        
        return histories.getOrDefault(name, "这里是一处重要的红色革命遗址，承载着丰富的革命历史和红色文化。");
    }
    
    private List<String> generateTips(Attraction attraction) {
        List<String> tips = new ArrayList<>();
        tips.add("建议游览" + (attraction.getVisitDuration() / 60) + "-" + ((attraction.getVisitDuration() / 60) + 1) + "小时");
        
        if (attraction.getTicketPrice().compareTo(BigDecimal.ZERO) == 0) {
            tips.add("免费参观需预约");
        } else {
            tips.add("门票" + attraction.getTicketPrice().intValue() + "元");
        }
        
        if (attraction.getOpeningHours() != null && attraction.getOpeningHours().contains("09:00")) {
            tips.add("周一闭馆");
        }
        
        return tips;
    }
    
    private List<AudioGuideVO> generateMockAudioGuides(Long spotId) {
        List<AudioGuideVO> guides = new ArrayList<>();
        
        AudioGuideVO guide1 = new AudioGuideVO();
        guide1.setId(spotId * 10 + 1);
        guide1.setSpotId(spotId);
        guide1.setTitle("景点概述");
        guide1.setDuration(180);
        guide1.setTranscript("欢迎来到这里，这是一处具有重要历史意义的红色景点...");
        guide1.setOrderNum(1);
        guides.add(guide1);
        
        AudioGuideVO guide2 = new AudioGuideVO();
        guide2.setId(spotId * 10 + 2);
        guide2.setSpotId(spotId);
        guide2.setTitle("历史故事");
        guide2.setDuration(240);
        guide2.setTranscript("在这里发生过许多感人的革命故事...");
        guide2.setOrderNum(2);
        guides.add(guide2);
        
        return guides;
    }
    
    private AudioGuideVO convertToAudioGuideVO(AudioGuide guide) {
        AudioGuideVO vo = new AudioGuideVO();
        vo.setId(guide.getId());
        vo.setSpotId(guide.getSpotId());
        vo.setTitle(guide.getTitle());
        vo.setDuration(guide.getDuration());
        vo.setTranscript(guide.getTranscript());
        vo.setAudioUrl(guide.getAudioUrl());
        vo.setOrderNum(guide.getOrderNum());
        return vo;
    }
}
