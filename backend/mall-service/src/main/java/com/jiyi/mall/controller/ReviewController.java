package com.jiyi.mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiyi.common.result.Result;
import com.jiyi.mall.entity.Review;
import com.jiyi.mall.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "商品评论")
@RestController
@RequestMapping("/api/mall/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "获取商品评论列表(分页)")
    @GetMapping("/product/{productId}")
    public Result<Map<String, Object>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "5") Integer size) {
        Page<Review> reviewPage = reviewService.getProductReviews(productId, page, size);
        Double avgRating = reviewService.getAverageRating(productId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("records", reviewPage.getRecords());
        result.put("total", reviewPage.getTotal());
        result.put("pages", reviewPage.getPages());
        result.put("current", reviewPage.getCurrent());
        result.put("avgRating", Math.round(avgRating * 10) / 10.0);
        return Result.success(result);
    }

    @Operation(summary = "检查用户是否可以评论")
    @GetMapping("/can-review")
    public Result<Boolean> canReview(
            @RequestParam Long userId,
            @RequestParam Long productId) {
        return Result.success(reviewService.canReview(userId, productId));
    }

    @Operation(summary = "添加评论")
    @PostMapping
    public Result<Review> addReview(@RequestBody Review review) {
        // 检查是否可以评论
        if (!reviewService.canReview(review.getUserId(), review.getProductId())) {
            return Result.error(400, "您未购买该商品或已评论过");
        }
        return Result.success(reviewService.addReview(review));
    }
}
