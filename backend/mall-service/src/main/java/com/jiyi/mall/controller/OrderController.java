package com.jiyi.mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiyi.mall.dto.CreateOrderRequest;
import com.jiyi.mall.entity.Order;
import com.jiyi.mall.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/mall/orders")
@Tag(name = "订单管理", description = "订单相关接口")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @GetMapping
    @Operation(summary = "获取订单列表", description = "分页查询订单列表")
    public Page<Order> getOrderList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "订单状态") @RequestParam(required = false) String status
    ) {
        log.info("查询订单列表: page={}, size={}, userId={}, status={}", page, size, userId, status);
        return orderService.getOrderList(page, size, userId, status);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取订单详情", description = "根据ID获取订单详细信息")
    public Order getOrderById(@Parameter(description = "订单ID") @PathVariable Long id) {
        log.info("查询订单详情: id={}", id);
        return orderService.getOrderById(id);
    }
    
    @PostMapping
    @Operation(summary = "创建订单", description = "创建新订单")
    public Order createOrder(@RequestBody CreateOrderRequest request) {
        log.info("创建订单: userId={}, items={}", request.getUserId(), request.getItems().size());
        return orderService.createOrder(request);
    }
    
    @PutMapping("/{id}/status")
    @Operation(summary = "更新订单状态", description = "更新订单状态")
    public Order updateOrderStatus(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @Parameter(description = "订单状态") @RequestParam String status
    ) {
        log.info("更新订单状态: id={}, status={}", id, status);
        return orderService.updateOrderStatus(id, status);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "取消订单", description = "取消订单并恢复库存")
    public void cancelOrder(@Parameter(description = "订单ID") @PathVariable Long id) {
        log.info("取消订单: id={}", id);
        orderService.cancelOrder(id);
    }
    
    @PostMapping("/{id}/pay")
    @Operation(summary = "支付订单", description = "支付订单")
    public Order payOrder(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @Parameter(description = "支付方式") @RequestParam String paymentMethod
    ) {
        log.info("支付订单: id={}, paymentMethod={}", id, paymentMethod);
        return orderService.payOrder(id, paymentMethod);
    }
    
    @PostMapping("/{id}/confirm")
    @Operation(summary = "确认收货", description = "确认收货")
    public Order confirmOrder(@Parameter(description = "订单ID") @PathVariable Long id) {
        log.info("确认收货: id={}", id);
        return orderService.confirmOrder(id);
    }
}
