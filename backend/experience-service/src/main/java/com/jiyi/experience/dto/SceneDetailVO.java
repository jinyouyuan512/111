package com.jiyi.experience.dto;

import lombok.Data;
import java.util.List;

/**
 * 场景详情视图对象
 */
@Data
public class SceneDetailVO {
    
    private Long id;
    
    /**
     * 场景名称
     */
    private String name;
    
    /**
     * 场景描述
     */
    private String description;
    
    /**
     * 时代背景
     */
    private String era;
    
    /**
     * 体验时长（分钟）
     */
    private Integer duration;
    
    /**
     * 预览图URL
     */
    private String thumbnail;
    
    /**
     * 3D模型URL
     */
    private String modelUrl;
    
    /**
     * 交互点列表
     */
    private List<InteractionPointVO> interactionPoints;
    
    /**
     * 用户进度（0-100）
     */
    private Integer progress;
}
