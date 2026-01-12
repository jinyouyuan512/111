package com.jiyi.social.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 话题实体
 */
@Data
@TableName("topic")
public class Topic {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 话题名称
     */
    private String name;
    
    /**
     * 话题描述
     */
    private String description;
    
    /**
     * 封面图片
     */
    private String coverImage;
    
    /**
     * 动态数量
     */
    private Integer postCount;
    
    /**
     * 关注数
     */
    private Integer followCount;
    
    /**
     * 浏览数/热度
     */
    private Integer viewCount;
    
    /**
     * 状态: active, inactive
     */
    private String status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
