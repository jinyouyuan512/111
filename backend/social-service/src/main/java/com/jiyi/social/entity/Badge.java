package com.jiyi.social.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 成就徽章实体
 */
@Data
@TableName("badge")
public class Badge {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 徽章名称
     */
    private String name;
    
    /**
     * 徽章描述
     */
    private String description;
    
    /**
     * 图标URL
     */
    private String icon;
    
    /**
     * 类型: checkin, post, social, learning
     */
    private String type;
    
    /**
     * 获得条件类型
     */
    private String conditionType;
    
    /**
     * 条件值
     */
    private Integer conditionValue;
    
    /**
     * 获得积分
     */
    private Integer points;
    
    /**
     * 稀有度: common, rare, epic, legendary
     */
    private String rarity;
}
