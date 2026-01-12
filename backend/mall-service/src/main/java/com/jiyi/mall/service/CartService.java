package com.jiyi.mall.service;

import com.jiyi.mall.entity.Cart;
import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService {
    
    /**
     * 添加商品到购物车
     */
    Cart addToCart(Long userId, Long productId, Integer quantity);
    
    /**
     * 获取用户购物车列表
     */
    List<Cart> getCartList(Long userId);
    
    /**
     * 更新购物车项
     */
    Cart updateCartItem(Long id, Long userId, Integer quantity, Boolean selected);
    
    /**
     * 删除购物车项
     */
    void deleteCartItem(Long id, Long userId);
    
    /**
     * 批量删除购物车项
     */
    void batchDeleteCartItems(List<Long> ids, Long userId);
    
    /**
     * 全选/取消全选
     */
    void selectAll(Long userId, Boolean selected);
    
    /**
     * 清空购物车
     */
    void clearCart(Long userId);
}
