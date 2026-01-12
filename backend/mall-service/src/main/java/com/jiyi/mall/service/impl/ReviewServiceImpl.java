package com.jiyi.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiyi.mall.entity.Order;
import com.jiyi.mall.entity.OrderItem;
import com.jiyi.mall.entity.Review;
import com.jiyi.mall.mapper.OrderItemMapper;
import com.jiyi.mall.mapper.OrderMapper;
import com.jiyi.mall.mapper.ReviewMapper;
import com.jiyi.mall.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Override
    public Page<Review> getProductReviews(Long productId, Integer page, Integer size) {
        Page<Review> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getProductId, productId)
               .orderByDesc(Review::getCreateTime);
        return this.page(pageParam, wrapper);
    }

    @Override
    public Review addReview(Review review) {
        this.save(review);
        return review;
    }

    @Override
    public Double getAverageRating(Long productId) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getProductId, productId);
        var reviews = this.list(wrapper);
        if (reviews.isEmpty()) return 5.0;
        return reviews.stream().mapToInt(Review::getRating).average().orElse(5.0);
    }

    @Override
    public Long getReviewCount(Long productId) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getProductId, productId);
        return this.count(wrapper);
    }

    @Override
    public boolean canReview(Long userId, Long productId) {
        // 查询用户是否购买过该商品（已完成的订单）
        LambdaQueryWrapper<Order> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(Order::getUserId, userId)
                   .eq(Order::getStatus, "已完成");
        List<Order> orders = orderMapper.selectList(orderWrapper);
        
        for (Order order : orders) {
            LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
            itemWrapper.eq(OrderItem::getOrderId, order.getId())
                      .eq(OrderItem::getProductId, productId);
            if (orderItemMapper.selectCount(itemWrapper) > 0) {
                // 检查是否已经评论过
                LambdaQueryWrapper<Review> reviewWrapper = new LambdaQueryWrapper<>();
                reviewWrapper.eq(Review::getUserId, userId)
                            .eq(Review::getProductId, productId);
                return this.count(reviewWrapper) == 0;
            }
        }
        return false;
    }
}
