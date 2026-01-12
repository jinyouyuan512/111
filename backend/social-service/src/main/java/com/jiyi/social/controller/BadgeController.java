package com.jiyi.social.controller;

import com.jiyi.common.result.Result;
import com.jiyi.common.util.JwtUtil;
import com.jiyi.social.dto.BadgeVO;
import com.jiyi.social.service.BadgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 徽章控制器
 */
@Tag(name = "徽章管理")
@RestController
@RequestMapping("/api/badges")
@RequiredArgsConstructor
public class BadgeController {
    
    private final BadgeService badgeService;
    
    @Operation(summary = "获取所有徽章")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public Result<List<BadgeVO>> getAllBadges(@RequestHeader("Authorization") String token) {
        String userId = JwtUtil.getUserIdFromToken(token.replace("Bearer ", ""));
        List<BadgeVO> badges = badgeService.getAllBadges(Long.parseLong(userId));
        return Result.success(badges);
    }
    
    @Operation(summary = "获取用户徽章")
    @GetMapping("/user/{userId}")
    public Result<List<BadgeVO>> getUserBadges(@PathVariable Long userId) {
        List<BadgeVO> badges = badgeService.getUserBadges(userId);
        return Result.success(badges);
    }
}
