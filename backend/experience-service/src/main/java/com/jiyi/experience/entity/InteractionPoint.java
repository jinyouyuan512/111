package com.jiyi.experience.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jiyi.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 交互点实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("interaction_point")
public class InteractionPoint extends BaseEntity {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 场景ID
     */
    private Long sceneId;
    
    /**
     * 交互点标题
     */
    private String title;
    
    /**
     * 交互点类型 (info, video, story, photo, artifact, character, event)
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
     * Z坐标位置（百分比，用于3D场景）
     */
    private Integer positionZ;
    
    /**
     * 交互点内容描述
     */
    private String content;
    
    /**
     * 媒体资源URL（图片、视频等）
     */
    private String mediaUrl;
    
    /**
     * 排序顺序
     */
    private Integer sortOrder;
}
