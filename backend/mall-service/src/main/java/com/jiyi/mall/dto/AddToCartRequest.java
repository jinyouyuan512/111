package com.jiyi.mall.dto;

import lombok.Data;

/**
 * 添加到购物车请求
 */
@Data
public class AddToCartRequest {
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 数量
     */
    private Integer quantity;
}
