package com.jiyi.academy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 河北红色新闻实体
 */
@Data
@TableName("red_news")
public class News {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 新闻标题 */
    private String title;
    
    /** 新闻摘要 */
    private String summary;
    
    /** 新闻内容 */
    private String content;
    
    /** 封面图片 */
    private String coverImage;
    
    /** 新闻分类: party-news(党建动态), red-story(红色故事), memorial(纪念活动), education(教育实践) */
    private String category;
    
    /** 新闻来源 */
    private String source;
    
    /** 作者 */
    private String author;
    
    /** 阅读量 */
    private Integer viewCount;
    
    /** 点赞数 */
    private Integer likeCount;
    
    /** 是否置顶 */
    private Boolean isTop;
    
    /** 是否热门 */
    private Boolean isHot;
    
    /** 发布时间 */
    private LocalDateTime publishTime;
    
    /** 外部链接 */
    private String externalUrl;
    
    /** 状态: draft(草稿), published(已发布), archived(已归档) */
    private String status;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @TableLogic
    private Integer deleted;
}
