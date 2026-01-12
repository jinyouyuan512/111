package com.jiyi.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiyi.mall.dto.CreateOrderRequest;
import com.jiyi.mall.entity.Order;
import com.jiyi.mall.entity.OrderItem;
import com.jiyi.mall.entity.Product;
import com.jiyi.mall.mapper.OrderItemMapper;
import com.jiyi.mall.mapper.OrderMapper;
import com.jiyi.mall.service.OrderService;
import com.jiyi.mall.service.ProductService;
import com.jiyi.mall.util.OrderNumberGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单服务实现类
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderItemMapper orderItemMapper;
    
    @Autowired
    private ProductService productService;
    
    @Override
    public Page<Order> getOrderList(int page, int size, Long userId, String status) {
        Page<Order> orderPage = new Page<>(page, size);
        
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        
        if (userId != null) {
            wrapper.eq(Order::getUserId, userId);
        }
        
        if (status != null && !status.isEmpty() && !"全部".equals(status)) {
            wrapper.eq(Order::getStatus, status);
        }
        
        wrapper.orderByDesc(Order::getCreateTime);
        
        return orderMapper.selectPage(orderPage, wrapper);
    }
    
    @Override
    public Order getOrderById(Long id) {
        Order order = orderMapper.selectById(id);
        
        if (order != null) {
            // 查询订单项
            LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OrderItem::getOrderId, id);
            List<OrderItem> items = orderItemMapper.selectList(wrapper);
            order.setItems(items);
        }
        
        return order;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(CreateOrderRequest request) {
        // 创建订单
        Order order = new Order();
        order.setOrderNumber(OrderNumberGenerator.generate());
        order.setUserId(request.getUserId());
        order.setStatus("待支付");
        order.setPaymentMethod(request.getPaymentMethod());
        order.setShippingAddress(request.getShippingAddress());
        
        // 计算总金额并创建订单项
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        
        for (CreateOrderRequest.OrderItemDTO itemDTO : request.getItems()) {
            Product product = productService.getProductById(itemDTO.getProductId());
            
            if (product == null) {
                throw new RuntimeException("商品不存在: " + itemDTO.getProductId());
            }
            
            // 先尝试扣减库存（使用数据库级别的原子操作，防止超卖）
            boolean stockDeducted = productService.decreaseStockSafe(product.getId(), itemDTO.getQuantity());
            if (!stockDeducted) {
                throw new RuntimeException("商品库存不足: " + product.getName());
            }
            
            // 创建订单项
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductIcon(product.getIcon());
            orderItem.setProductColor(product.getColor());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(itemDTO.getQuantity());
            
            BigDecimal subtotal = product.getPrice().multiply(new BigDecimal(itemDTO.getQuantity()));
            orderItem.setSubtotal(subtotal);
            
            orderItems.add(orderItem);
            totalAmount = totalAmount.add(subtotal);
        }
        
        order.setTotalAmount(totalAmount);
        orderMapper.insert(order);
        
        // 保存订单项
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }
        
        log.info("创建订单成功: {}", order.getOrderNumber());
        
        order.setItems(orderItems);
        return order;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order updateOrderStatus(Long id, String status) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        order.setStatus(status);
        orderMapper.updateById(order);
        
        log.info("更新订单状态: {} -> {}", id, status);
        return order;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (!"待支付".equals(order.getStatus())) {
            throw new RuntimeException("只能取消待支付订单");
        }
        
        // 恢复库存
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, id);
        List<OrderItem> items = orderItemMapper.selectList(wrapper);
        
        for (OrderItem item : items) {
            productService.increaseStock(item.getProductId(), item.getQuantity());
        }
        
        order.setStatus("已取消");
        orderMapper.updateById(order);
        
        log.info("取消订单: {}", id);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order payOrder(Long id, String paymentMethod) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (!"待支付".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确");
        }
        
        order.setStatus("待发货");
        order.setPaymentMethod(paymentMethod);
        orderMapper.updateById(order);
        
        // 增加销量
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, id);
        List<OrderItem> items = orderItemMapper.selectList(wrapper);
        
        for (OrderItem item : items) {
            productService.increaseSales(item.getProductId(), item.getQuantity());
        }
        
        log.info("支付订单: {}", id);
        return order;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order confirmOrder(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (!"已发货".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确");
        }
        
        order.setStatus("已完成");
        orderMapper.updateById(order);
        
        log.info("确认收货: {}", id);
        return order;
    }
}
