package com.jiyi.experience.dto;

import lombok.Data;

/**
 * 交互点视图对象
 */
@Data
public class InteractionPointVO {
    
    private Long id;
    
    /**
     * 交互点标题
     */
    private String title;
    
    /**
     * 交互点类型
     */
    private String type;
    
    /**
     * X坐标位置（百分比）
     */
    private Integer positionX;
    
    /**
     * Y坐标位置（百分比）
     */
    private Integer positionY;
    
    /**
     * Z坐标位置（百分比）
     */
    private Integer positionZ;
    
    /**
     * 交互点内容描述
     */
    private String content;
    
    /**
     * 媒体资源URL
     */
    private String mediaUrl;
}
