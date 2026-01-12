package com.jiyi.mall.dto;

import lombok.Data;
import java.util.List;

/**
 * 创建订单请求DTO
 */
@Data
public class CreateOrderRequest {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 订单项列表
     */
    private List<OrderItemDTO> items;
    
    /**
     * 收货地址
     */
    private String shippingAddress;
    
    /**
     * 支付方式
     */
    private String paymentMethod;
    
    @Data
    public static class OrderItemDTO {
        /**
         * 商品ID
         */
        private Long productId;
        
        /**
         * 购买数量
         */
        private Integer quantity;
    }
}
