package com.jiyi.user.controller;

import com.jiyi.common.result.Result;
import com.jiyi.common.util.JwtUtil;
import com.jiyi.user.dto.UserInfo;
import com.jiyi.user.service.UserFollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户关注控制器
 */
@Tag(name = "用户关注")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserFollowController {
    
    private final UserFollowService userFollowService;
    
    @Operation(summary = "关注用户")
    @PostMapping("/{userId}/follow")
    @PreAuthorize("isAuthenticated()")
    public Result<Void> followUser(@PathVariable Long userId,
                                   @RequestHeader("Authorization") String token) {
        String accessToken = token.replace("Bearer ", "");
        String currentUserId = JwtUtil.getUserIdFromToken(accessToken);
        userFollowService.followUser(Long.parseLong(currentUserId), userId);
        return Result.success();
    }
    
    @Operation(summary = "取消关注")
    @DeleteMapping("/{userId}/follow")
    @PreAuthorize("isAuthenticated()")
    public Result<Void> unfollowUser(@PathVariable Long userId,
                                     @RequestHeader("Authorization") String token) {
        String accessToken = token.replace("Bearer ", "");
        String currentUserId = JwtUtil.getUserIdFromToken(accessToken);
        userFollowService.unfollowUser(Long.parseLong(currentUserId), userId);
        return Result.success();
    }
    
    @Operation(summary = "检查是否关注")
    @GetMapping("/{userId}/is-following")
    @PreAuthorize("isAuthenticated()")
    public Result<Boolean> isFollowing(@PathVariable Long userId,
                                       @RequestHeader("Authorization") String token) {
        String accessToken = token.replace("Bearer ", "");
        String currentUserId = JwtUtil.getUserIdFromToken(accessToken);
        boolean following = userFollowService.isFollowing(Long.parseLong(currentUserId), userId);
        return Result.success(following);
    }
    
    @Operation(summary = "获取关注列表")
    @GetMapping("/{userId}/following")
    public Result<List<UserInfo>> getFollowingList(@PathVariable Long userId) {
        List<UserInfo> followingList = userFollowService.getFollowingList(userId);
        return Result.success(followingList);
    }
    
    @Operation(summary = "获取粉丝列表")
    @GetMapping("/{userId}/followers")
    public Result<List<UserInfo>> getFollowersList(@PathVariable Long userId) {
        List<UserInfo> followersList = userFollowService.getFollowersList(userId);
        return Result.success(followersList);
    }
    
    @Operation(summary = "获取关注数")
    @GetMapping("/{userId}/following-count")
    public Result<Long> getFollowingCount(@PathVariable Long userId) {
        Long count = userFollowService.getFollowingCount(userId);
        return Result.success(count);
    }
    
    @Operation(summary = "获取粉丝数")
    @GetMapping("/{userId}/followers-count")
    public Result<Long> getFollowersCount(@PathVariable Long userId) {
        Long count = userFollowService.getFollowersCount(userId);
        return Result.success(count);
    }
}
