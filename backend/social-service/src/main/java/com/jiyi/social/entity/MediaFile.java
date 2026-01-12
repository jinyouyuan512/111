package com.jiyi.social.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 媒体文件实体
 */
@Data
@TableName("media_file")
public class MediaFile {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 动态ID
     */
    private Long postId;
    
    /**
     * 类型: image, video
     */
    private String type;
    
    /**
     * 文件URL
     */
    private String url;
    
    /**
     * 缩略图URL
     */
    private String thumbnail;
    
    /**
     * 宽度
     */
    private Integer width;
    
    /**
     * 高度
     */
    private Integer height;
    
    /**
     * 时长(秒)
     */
    private Integer duration;
    
    /**
     * 文件大小(字节)
     */
    private Long fileSize;
    
    /**
     * 排序
     */
    private Integer orderNum;
}
