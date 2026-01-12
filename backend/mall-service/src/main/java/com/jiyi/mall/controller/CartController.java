package com.jiyi.mall.controller;

import com.jiyi.common.result.Result;
import com.jiyi.mall.dto.AddToCartRequest;
import com.jiyi.mall.dto.UpdateCartRequest;
import com.jiyi.mall.entity.Cart;
import com.jiyi.mall.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/mall/cart")
@Tag(name = "购物车管理", description = "购物车相关接口")
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    @PostMapping
    @Operation(summary = "添加到购物车", description = "添加商品到购物车")
    public Result<Cart> addToCart(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody AddToCartRequest request
    ) {
        log.info("添加到购物车: userId={}, productId={}, quantity={}", 
                userId, request.getProductId(), request.getQuantity());
        return Result.success(cartService.addToCart(userId, request.getProductId(), request.getQuantity()));
    }
    
    @GetMapping
    @Operation(summary = "获取购物车列表", description = "获取用户的购物车列表")
    public Result<List<Cart>> getCartList(@RequestHeader("X-User-Id") Long userId) {
        log.info("获取购物车列表: userId={}", userId);
        return Result.success(cartService.getCartList(userId));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新购物车项", description = "更新购物车中的商品数量或选中状态")
    public Result<Cart> updateCartItem(
            @Parameter(description = "购物车项ID") @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody UpdateCartRequest request
    ) {
        log.info("更新购物车项: id={}, userId={}, quantity={}, selected={}", 
                id, userId, request.getQuantity(), request.getSelected());
        return Result.success(cartService.updateCartItem(id, userId, request.getQuantity(), request.getSelected()));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除购物车项", description = "删除购物车中的商品")
    public Result<Void> deleteCartItem(
            @Parameter(description = "购物车项ID") @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId
    ) {
        log.info("删除购物车项: id={}, userId={}", id, userId);
        cartService.deleteCartItem(id, userId);
        return Result.success();
    }
    
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除购物车项", description = "批量删除购物车中的商品")
    public Result<Void> batchDeleteCartItems(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody List<Long> ids
    ) {
        log.info("批量删除购物车项: userId={}, ids={}", userId, ids);
        cartService.batchDeleteCartItems(ids, userId);
        return Result.success();
    }
    
    @PutMapping("/select-all")
    @Operation(summary = "全选/取消全选", description = "全选或取消全选购物车中的商品")
    public Result<Void> selectAll(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam Boolean selected
    ) {
        log.info("全选/取消全选: userId={}, selected={}", userId, selected);
        cartService.selectAll(userId, selected);
        return Result.success();
    }
    
    @DeleteMapping("/clear")
    @Operation(summary = "清空购物车", description = "清空用户的购物车")
    public Result<Void> clearCart(@RequestHeader("X-User-Id") Long userId) {
        log.info("清空购物车: userId={}", userId);
        cartService.clearCart(userId);
        return Result.success();
    }
}
