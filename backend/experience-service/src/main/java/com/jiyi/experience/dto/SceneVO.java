package com.jiyi.experience.dto;

import lombok.Data;

/**
 * 场景视图对象
 */
@Data
public class SceneVO {
    
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
     * 交互点数量
     */
    private Integer interactionCount;
}
