package com.jiyi.social.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 动态实体
 */
@Data
@TableName("post")
public class Post {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 动态标题
     */
    private String title;
    
    /**
     * 动态内容
     */
    private String content;
    
    /**
     * 图片列表（JSON数组）
     */
    @TableField(exist = false)
    private String images;
    
    /**
     * 位置名称
     */
    private String locationName;
    
    /**
     * 位置ID
     */
    private Long locationId;
    
    /**
     * 纬度
     */
    private Double latitude;
    
    /**
     * 经度
     */
    private Double longitude;
    
    /**
     * 类型
     */
    private String type;
    
    /**
     * 可见性
     */
    private String visibility;
    
    /**
     * 分类（非数据库字段，用于前端）
     */
    @TableField(exist = false)
    private String category;
    
    /**
     * 点赞数
     */
    private Integer likes;
    
    /**
     * 评论数
     */
    private Integer comments;
    
    /**
     * 分享数
     */
    private Integer shares;
    
    /**
     * 浏览数
     */
    private Integer views;
    
    /**
     * 状态
     */
    private String status;
    
    /**
     * 是否置顶（非数据库字段）
     */
    @TableField(exist = false)
    private Boolean pinned;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    @TableField(value = "deleted", fill = FieldFill.INSERT)
    private Integer deleted;
}
