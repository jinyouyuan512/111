package com.jiyi.academy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 红色读物实体
 */
@Data
@TableName("red_book")
public class Book {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 书名 */
    private String title;
    
    /** 作者 */
    private String author;
    
    /** 出版社 */
    private String publisher;
    
    /** ISBN */
    private String isbn;
    
    /** 封面图片 */
    private String coverImage;
    
    /** 书籍简介 */
    private String description;
    
    /** 书籍分类: classic(红色经典), history(党史著作), biography(人物传记), documentary(纪实文学), youth(青少年读物) */
    private String category;
    
    /** 页数 */
    private Integer pageCount;
    
    /** 出版日期 */
    private String publishDate;
    
    /** 评分 */
    private BigDecimal rating;
    
    /** 评分人数 */
    private Integer ratingCount;
    
    /** 阅读量 */
    private Integer readCount;
    
    /** 收藏数 */
    private Integer favoriteCount;
    
    /** 是否推荐 */
    private Boolean isRecommended;
    
    /** 是否有电子版 */
    private Boolean hasEbook;
    
    /** 电子书链接 */
    private String ebookUrl;
    
    /** 试读章节数 */
    private Integer previewChapters;
    
    /** 标签，逗号分隔 */
    private String tags;
    
    /** 外部链接（购买/阅读） */
    private String externalUrl;
    
    /** 状态 */
    private String status;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @TableLogic
    private Integer deleted;
}
