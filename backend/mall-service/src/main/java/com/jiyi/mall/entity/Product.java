package com.jiyi.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类
 */
@Data
@TableName("product")
public class Product {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 商品名称
     */
    private String name;
    
    /**
     * 商品分类
     */
    private String category;
    
    /**
     * 商品描述
     */
    private String description;
    
    /**
     * 文化背景
     */
    private String culturalBackground;
    
    /**
     * 商品图标(emoji)
     */
    private String icon;
    
    /**
     * 商品颜色(渐变色)
     */
    private String color;
    
    /**
     * 商品价格
     */
    private BigDecimal price;
    
    /**
     * 库存数量
     */
    private Integer stock;
    
    /**
     * 销量
     */
    private Integer sales;
    
    /**
     * 设计师名称
     */
    private String designer;
    
    /**
     * 是否有货
     */
    private Boolean inStock;
    
    /**
     * 商品图片列表(JSON数组)
     */
    private String images;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer deleted;
}
