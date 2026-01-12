package com.jiyi.experience.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jiyi.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 场景实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scene")
public class Scene extends BaseEntity {
    
    @TableId(type = IdType.AUTO)
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
     * 交互点数量
     */
    private Integer interactionCount;
}
