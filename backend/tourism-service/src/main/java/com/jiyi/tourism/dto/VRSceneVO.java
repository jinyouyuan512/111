package com.jiyi.tourism.dto;

import lombok.Data;
import java.util.List;

/**
 * VR场景VO
 */
@Data
public class VRSceneVO {
    private Long spotId;
    private String spotName;
    private String sceneType; // panorama, 3d_model
    private String panoramaUrl;
    private String modelUrl;
    private Double initialYaw;
    private Double initialPitch;
    private Double initialFov;
    private List<VRSpotVO.Hotspot> hotspots;
    private List<SceneLink> linkedScenes;
    private SceneSettings settings;
    
    @Data
    public static class SceneLink {
        private String sceneId;
        private String sceneName;
        private String thumbnailUrl;
        private Double positionX;
        private Double positionY;
    }
    
    @Data
    public static class SceneSettings {
        private Boolean autoRotate;
        private Double autoRotateSpeed;
        private Boolean showCompass;
        private Boolean enableGyroscope;
        private Boolean enableVRMode;
    }
}
