package com.jiyi.creative.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 商城上架申请请求
 */
@Data
public class MallApplicationRequest {
    /**
     * 设计作品ID
     */
    private Long designId;
    
    /**
     * 商品名称
     */
    private String productName;
    
    /**
     * 商品分类
     */
    private String category;
    
    /**
     * 商品描述
     */
    private String description;
    
    /**
     * 建议价格
     */
    private BigDecimal suggestedPrice;
    
    /**
     * 初始库存
     */
    private Integer initialStock;
    
    /**
     * 商品图标(emoji)
     */
    private String icon;
}
