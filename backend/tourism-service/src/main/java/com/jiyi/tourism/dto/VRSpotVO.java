package com.jiyi.tourism.dto;

import lombok.Data;
import java.util.List;

/**
 * VR景点VO
 */
@Data
public class VRSpotVO {
    private Long id;
    private String name;
    private String icon;
    private String gradient;
    private String description;
    private Boolean hasVR;
    private Boolean has360;
    private Long views;
    private Double rating;
    private String introduction;
    private String history;
    private List<String> tips;
    private String audioTranscript;
    private List<Hotspot> hotspots;
    private String panoramaUrl;
    private String vrModelUrl;
    
    @Data
    public static class Hotspot {
        private Long id;
        private String name;
        private String icon;
        private Double x;
        private Double y;
        private String info;
        private String linkedSceneId;
    }
    
    @Data
    public static class AudioGuide {
        private Long spotId;
        private String language;
        private String audioUrl;
        private String transcript;
        private Integer duration; // 秒
        private List<AudioChapter> chapters;
    }
    
    @Data
    public static class AudioChapter {
        private String title;
        private Integer startTime;
        private Integer endTime;
        private String content;
    }
}
