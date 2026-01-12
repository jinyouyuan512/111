package com.jiyi.mall.dto;

import lombok.Data;

/**
 * 更新购物车请求
 */
@Data
public class UpdateCartRequest {
    
    /**
     * 数量
     */
    private Integer quantity;
    
    /**
     * 是否选中
     */
    private Boolean selected;
}
