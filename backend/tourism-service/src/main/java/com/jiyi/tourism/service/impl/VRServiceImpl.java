package com.jiyi.tourism.service.impl;

import com.jiyi.tourism.dto.VRSpotVO;
import com.jiyi.tourism.dto.VRSceneVO;
import com.jiyi.tourism.service.VRService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * VR全景服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VRServiceImpl implements VRService {
    
    @Override
    public List<VRSpotVO> getAllVRSpots() {
        List<VRSpotVO> spots = new ArrayList<>();
        
        // 西柏坡纪念馆
        VRSpotVO xibaipo = new VRSpotVO();
        xibaipo.setId(1L);
        xibaipo.setName("西柏坡纪念馆");
        xibaipo.setIcon("🏛️");
        xibaipo.setGradient("linear-gradient(135deg, #c41e3a 0%, #8b0000 100%)");
        xibaipo.setDescription("新中国从这里走来");
        xibaipo.setHasVR(true);
        xibaipo.setHas360(true);
        xibaipo.setViews(125800L);
        xibaipo.setRating(4.9);
        xibaipo.setIntroduction("西柏坡纪念馆位于河北省石家庄市平山县西柏坡镇，是解放战争时期中央工委、中共中央和解放军总部的所在地。");
        xibaipo.setHistory("1948年5月至1949年3月，中共中央在西柏坡指挥了辽沈、淮海、平津三大战役，召开了具有伟大历史意义的七届二中全会。");
        xibaipo.setTips(Arrays.asList("建议游览时间：2-3小时", "免费参观，需提前预约", "周一闭馆"));
        xibaipo.setAudioTranscript("欢迎来到西柏坡纪念馆。西柏坡是中国革命圣地之一，被誉为'新中国从这里走来'...");
        
        List<VRSpotVO.Hotspot> xibaipoHotspots = new ArrayList<>();
        VRSpotVO.Hotspot h1 = new VRSpotVO.Hotspot();
        h1.setId(1L);
        h1.setName("七届二中全会会址");
        h1.setIcon("🏠");
        h1.setX(30.0);
        h1.setY(40.0);
        h1.setInfo("1949年3月5日至13日，中国共产党第七届中央委员会第二次全体会议在此召开");
        xibaipoHotspots.add(h1);
        
        VRSpotVO.Hotspot h2 = new VRSpotVO.Hotspot();
        h2.setId(2L);
        h2.setName("毛泽东旧居");
        h2.setIcon("🏡");
        h2.setX(60.0);
        h2.setY(35.0);
        h2.setInfo("毛泽东同志在西柏坡期间的居住和办公场所");
        xibaipoHotspots.add(h2);
        
        xibaipo.setHotspots(xibaipoHotspots);
        spots.add(xibaipo);
        
        // 狼牙山
        VRSpotVO langyashan = new VRSpotVO();
        langyashan.setId(2L);
        langyashan.setName("狼牙山五壮士纪念地");
        langyashan.setIcon("⛰️");
        langyashan.setGradient("linear-gradient(135deg, #2c5530 0%, #1a3a1c 100%)");
        langyashan.setDescription("英雄壮举，气壮山河");
        langyashan.setHasVR(true);
        langyashan.setHas360(true);
        langyashan.setViews(89200L);
        langyashan.setRating(4.8);
        langyashan.setIntroduction("狼牙山位于河北省保定市易县西部的太行山东麓，因其奇峰林立、峥嵘险峻、状若狼牙而得名。");
        langyashan.setHistory("1941年9月25日，八路军五名战士为掩护群众和主力撤退，在狼牙山顶峰与日伪军激战后跳崖。");
        langyashan.setTips(Arrays.asList("建议游览时间：3-4小时", "山路较陡，注意安全", "建议携带饮用水"));
        langyashan.setAudioTranscript("狼牙山五壮士的故事是中国抗日战争中最悲壮的篇章之一...");
        spots.add(langyashan);
        
        // 白洋淀
        VRSpotVO baiyangdian = new VRSpotVO();
        baiyangdian.setId(3L);
        baiyangdian.setName("白洋淀雁翎队纪念馆");
        baiyangdian.setIcon("🚤");
        baiyangdian.setGradient("linear-gradient(135deg, #1890ff 0%, #096dd9 100%)");
        baiyangdian.setDescription("华北明珠，水上传奇");
        baiyangdian.setHasVR(true);
        baiyangdian.setHas360(true);
        baiyangdian.setViews(156700L);
        baiyangdian.setRating(4.7);
        baiyangdian.setIntroduction("白洋淀是华北平原最大的淡水湖泊，素有'华北明珠'之称。");
        baiyangdian.setHistory("雁翎队成立于1939年，因队员们在枪上插上雁翎作为标志而得名。");
        baiyangdian.setTips(Arrays.asList("建议游览时间：半天", "可乘船游览", "夏季荷花盛开最美"));
        baiyangdian.setAudioTranscript("白洋淀，华北平原上的一颗明珠...");
        spots.add(baiyangdian);
        
        // 塞罕坝
        VRSpotVO saihanba = new VRSpotVO();
        saihanba.setId(4L);
        saihanba.setName("塞罕坝展览馆");
        saihanba.setIcon("🌲");
        saihanba.setGradient("linear-gradient(135deg, #228b22 0%, #006400 100%)");
        saihanba.setDescription("荒原变林海的绿色奇迹");
        saihanba.setHasVR(true);
        saihanba.setHas360(true);
        saihanba.setViews(78900L);
        saihanba.setRating(4.9);
        saihanba.setIntroduction("塞罕坝位于河北省承德市围场满族蒙古族自治县境内，是世界上面积最大的人工林场。");
        saihanba.setHistory("1962年，369名创业者来到塞罕坝，开始了艰苦卓绝的造林事业。");
        saihanba.setTips(Arrays.asList("建议游览时间：1-2天", "夏季凉爽，是避暑胜地", "秋季色彩斑斓最美"));
        saihanba.setAudioTranscript("塞罕坝，蒙古语意为'美丽的高岭'...");
        spots.add(saihanba);
        
        return spots;
    }
    
    @Override
    public VRSpotVO getVRSpotDetail(Long spotId) {
        List<VRSpotVO> allSpots = getAllVRSpots();
        return allSpots.stream()
                .filter(s -> s.getId().equals(spotId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("VR景点不存在"));
    }
    
    @Override
    public VRSceneVO getVRScene(Long spotId) {
        VRSpotVO spot = getVRSpotDetail(spotId);
        
        VRSceneVO scene = new VRSceneVO();
        scene.setSpotId(spotId);
        scene.setSpotName(spot.getName());
        scene.setSceneType("panorama");
        scene.setPanoramaUrl("/vr/panorama/" + spotId + ".jpg");
        scene.setInitialYaw(0.0);
        scene.setInitialPitch(0.0);
        scene.setInitialFov(90.0);
        scene.setHotspots(spot.getHotspots());
        
        VRSceneVO.SceneSettings settings = new VRSceneVO.SceneSettings();
        settings.setAutoRotate(true);
        settings.setAutoRotateSpeed(0.5);
        settings.setShowCompass(true);
        settings.setEnableGyroscope(true);
        settings.setEnableVRMode(true);
        scene.setSettings(settings);
        
        return scene;
    }
    
    @Override
    public void recordVRView(Long spotId, Long userId) {
        log.info("Recording VR view for spot: {}, user: {}", spotId, userId);
        // 实际应用中应该保存到数据库
    }
    
    @Override
    public VRSpotVO.AudioGuide getAudioGuide(Long spotId, String language) {
        VRSpotVO spot = getVRSpotDetail(spotId);
        
        VRSpotVO.AudioGuide guide = new VRSpotVO.AudioGuide();
        guide.setSpotId(spotId);
        guide.setLanguage(language);
        guide.setAudioUrl("/audio/guide/" + spotId + "_" + language + ".mp3");
        guide.setTranscript(spot.getAudioTranscript());
        guide.setDuration(180);
        
        List<VRSpotVO.AudioChapter> chapters = new ArrayList<>();
        VRSpotVO.AudioChapter chapter1 = new VRSpotVO.AudioChapter();
        chapter1.setTitle("景点概述");
        chapter1.setStartTime(0);
        chapter1.setEndTime(60);
        chapter1.setContent(spot.getIntroduction());
        chapters.add(chapter1);
        
        VRSpotVO.AudioChapter chapter2 = new VRSpotVO.AudioChapter();
        chapter2.setTitle("历史故事");
        chapter2.setStartTime(60);
        chapter2.setEndTime(150);
        chapter2.setContent(spot.getHistory());
        chapters.add(chapter2);
        
        guide.setChapters(chapters);
        
        return guide;
    }
}
