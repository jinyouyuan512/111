package com.jiyi.mall.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiyi.common.result.Result;
import com.jiyi.mall.entity.Order;
import com.jiyi.mall.entity.OrderItem;
import com.jiyi.mall.mapper.OrderItemMapper;
import com.jiyi.mall.mapper.OrderMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员 - 订单管理控制器
 */
@Tag(name = "管理员-订单管理")
@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Operation(summary = "获取订单列表")
    @GetMapping
    public Result<Map<String, Object>> listOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        Page<Order> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Order::getOrderNumber, keyword);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Order::getStatus, status);
        }
        if (StringUtils.hasText(startDate)) {
            wrapper.ge(Order::getCreateTime, startDate + " 00:00:00");
        }
        if (StringUtils.hasText(endDate)) {
            wrapper.le(Order::getCreateTime, endDate + " 23:59:59");
        }
        wrapper.orderByDesc(Order::getCreateTime);
        
        Page<Order> result = orderMapper.selectPage(pageParam, wrapper);
        
        // 加载订单项
        for (Order order : result.getRecords()) {
            LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
            itemWrapper.eq(OrderItem::getOrderId, order.getId());
            List<OrderItem> items = orderItemMapper.selectList(itemWrapper);
            order.setItems(items);
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        
        return Result.success(data);
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/{orderId}")
    public Result<Order> getOrderDetail(@PathVariable Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order != null) {
            LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
            itemWrapper.eq(OrderItem::getOrderId, orderId);
            List<OrderItem> items = orderItemMapper.selectList(itemWrapper);
            order.setItems(items);
        }
        return Result.success(order);
    }

    @Operation(summary = "订单发货")
    @PostMapping("/{orderId}/ship")
    public Result<Void> shipOrder(@PathVariable Long orderId, @RequestBody ShipRequest request) {
        Order order = orderMapper.selectById(orderId);
        if (order != null && "paid".equals(order.getStatus())) {
            order.setStatus("shipped");
            order.setTrackingNumber(request.getTrackingNo());
            order.setUpdateTime(LocalDateTime.now());
            orderMapper.updateById(order);
        }
        return Result.success();
    }

    @Operation(summary = "取消订单")
    @PostMapping("/{orderId}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order != null && "pending".equals(order.getStatus())) {
            order.setStatus("cancelled");
            order.setUpdateTime(LocalDateTime.now());
            orderMapper.updateById(order);
        }
        return Result.success();
    }

    @Operation(summary = "订单退款")
    @PostMapping("/{orderId}/refund")
    public Result<Void> refundOrder(@PathVariable Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order != null) {
            order.setStatus("refunded");
            order.setUpdateTime(LocalDateTime.now());
            orderMapper.updateById(order);
        }
        return Result.success();
    }

    @Operation(summary = "获取订单统计")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getOrderStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 总订单数
        stats.put("totalOrders", orderMapper.selectCount(null));
        
        // 各状态订单数
        String[] statuses = {"pending", "paid", "shipped", "completed", "cancelled", "refunded"};
        for (String status : statuses) {
            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Order::getStatus, status);
            stats.put(status + "Count", orderMapper.selectCount(wrapper));
        }
        
        // 今日订单数
        LambdaQueryWrapper<Order> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.apply("DATE(create_time) = CURDATE()");
        stats.put("todayOrders", orderMapper.selectCount(todayWrapper));
        
        // 今日收入 (已支付的订单)
        LambdaQueryWrapper<Order> revenueWrapper = new LambdaQueryWrapper<>();
        revenueWrapper.apply("DATE(create_time) = CURDATE()");
        revenueWrapper.in(Order::getStatus, "paid", "shipped", "completed");
        List<Order> todayPaidOrders = orderMapper.selectList(revenueWrapper);
        BigDecimal todayRevenue = todayPaidOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("todayRevenue", todayRevenue);
        
        return Result.success(stats);
    }

    @Data
    public static class ShipRequest {
        private String expressCompany;
        private String trackingNo;
    }
}
