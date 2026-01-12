package com.jiyi.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.jiyi.mall.entity.Cart;
import com.jiyi.mall.entity.Product;
import com.jiyi.mall.mapper.CartMapper;
import com.jiyi.mall.mapper.ProductMapper;
import com.jiyi.mall.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 购物车服务实现类
 */
@Slf4j
@Service
public class CartServiceImpl implements CartService {
    
    @Autowired
    private CartMapper cartMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Cart addToCart(Long userId, Long productId, Integer quantity) {
        // 检查商品是否存在
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        
        // 检查库存
        if (product.getStock() < quantity) {
            throw new RuntimeException("库存不足");
        }
        
        // 查询购物车中是否已存在该商品
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId)
                .eq(Cart::getProductId, productId);
        Cart existingCart = cartMapper.selectOne(wrapper);
        
        if (existingCart != null) {
            // 已存在，更新数量
            existingCart.setQuantity(existingCart.getQuantity() + quantity);
            cartMapper.updateById(existingCart);
            existingCart.setProduct(product);
            return existingCart;
        } else {
            // 不存在，新增
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(quantity);
            cart.setSelected(true);
            cartMapper.insert(cart);
            cart.setProduct(product);
            return cart;
        }
    }
    
    @Override
    public List<Cart> getCartList(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId)
                .orderByDesc(Cart::getCreatedAt);
        List<Cart> cartList = cartMapper.selectList(wrapper);
        
        // 填充商品信息
        for (Cart cart : cartList) {
            Product product = productMapper.selectById(cart.getProductId());
            cart.setProduct(product);
        }
        
        return cartList;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Cart updateCartItem(Long id, Long userId, Integer quantity, Boolean selected) {
        Cart cart = cartMapper.selectById(id);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new RuntimeException("购物车项不存在");
        }
        
        if (quantity != null) {
            // 检查库存
            Product product = productMapper.selectById(cart.getProductId());
            if (product.getStock() < quantity) {
                throw new RuntimeException("库存不足");
            }
            cart.setQuantity(quantity);
        }
        
        if (selected != null) {
            cart.setSelected(selected);
        }
        
        cartMapper.updateById(cart);
        
        // 填充商品信息
        Product product = productMapper.selectById(cart.getProductId());
        cart.setProduct(product);
        
        return cart;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCartItem(Long id, Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getId, id)
                .eq(Cart::getUserId, userId);
        cartMapper.delete(wrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteCartItems(List<Long> ids, Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Cart::getId, ids)
                .eq(Cart::getUserId, userId);
        cartMapper.delete(wrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void selectAll(Long userId, Boolean selected) {
        LambdaUpdateWrapper<Cart> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Cart::getUserId, userId)
                .set(Cart::getSelected, selected);
        cartMapper.update(null, wrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearCart(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        cartMapper.delete(wrapper);
    }
}
