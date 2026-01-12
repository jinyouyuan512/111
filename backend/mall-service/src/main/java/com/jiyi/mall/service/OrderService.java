package com.jiyi.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiyi.mall.entity.Order;
import com.jiyi.mall.dto.CreateOrderRequest;

/**
 * 订单服务接口
 */
public interface OrderService {
    
    /**
     * 分页查询订单列表
     */
    Page<Order> getOrderList(int page, int size, Long userId, String status);
    
    /**
     * 根据ID获取订单详情
     */
    Order getOrderById(Long id);
    
    /**
     * 创建订单
     */
    Order createOrder(CreateOrderRequest request);
    
    /**
     * 更新订单状态
     */
    Order updateOrderStatus(Long id, String status);
    
    /**
     * 取消订单
     */
    void cancelOrder(Long id);
    
    /**
     * 支付订单
     */
    Order payOrder(Long id, String paymentMethod);
    
    /**
     * 确认收货
     */
    Order confirmOrder(Long id);
}
