package com.jiyi.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiyi.mall.entity.Review;

public interface ReviewService {
    Page<Review> getProductReviews(Long productId, Integer page, Integer size);
    Review addReview(Review review);
    Double getAverageRating(Long productId);
    Long getReviewCount(Long productId);
    boolean canReview(Long userId, Long productId);
}
