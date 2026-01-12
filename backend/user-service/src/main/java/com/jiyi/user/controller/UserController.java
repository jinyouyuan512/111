package com.jiyi.user.controller;

import com.jiyi.common.result.Result;
import com.jiyi.common.util.JwtUtil;
import com.jiyi.user.dto.UserInfo;
import com.jiyi.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @Operation(summary = "获取当前用户信息")
    @GetMapping("/me")
    public Result<UserInfo> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return Result.error(401, "未登录");
        }
        try {
            String accessToken = token.replace("Bearer ", "");
            String userId = JwtUtil.getUserIdFromToken(accessToken);
            UserInfo userInfo = userService.getUserById(Long.parseLong(userId));
            return Result.success(userInfo);
        } catch (Exception e) {
            return Result.error(401, "Token无效或已过期");
        }
    }
    
    @Operation(summary = "根据ID获取用户信息")
    @GetMapping("/{userId}")
    public Result<UserInfo> getUserById(@PathVariable Long userId) {
        UserInfo userInfo = userService.getUserById(userId);
        return Result.success(userInfo);
    }
    
    @Operation(summary = "更新用户信息")
    @PutMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public Result<UserInfo> updateProfile(@PathVariable Long userId, 
                                          @RequestBody UserInfo profile,
                                          @RequestHeader("Authorization") String token) {
        String accessToken = token.replace("Bearer ", "");
        String currentUserId = JwtUtil.getUserIdFromToken(accessToken);
        
        // 只能更新自己的信息
        if (!currentUserId.equals(userId.toString())) {
            return Result.error(403, "无权限修改其他用户信息");
        }
        
        UserInfo updatedInfo = userService.updateProfile(userId, profile);
        return Result.success(updatedInfo);
    }
    
    @Operation(summary = "获取用户统计信息")
    @GetMapping("/{userId}/statistics")
    @PreAuthorize("isAuthenticated()")
    public Result<com.jiyi.user.dto.UserStatistics> getUserStatistics(@PathVariable Long userId) {
        com.jiyi.user.dto.UserStatistics statistics = userService.getUserStatistics(userId);
        return Result.success(statistics);
    }
    
    @Operation(summary = "修改密码")
    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public Result<Void> changePassword(@RequestBody com.jiyi.user.dto.PasswordChangeRequest request,
                                       @RequestHeader("Authorization") String token) {
        String accessToken = token.replace("Bearer ", "");
        String userId = JwtUtil.getUserIdFromToken(accessToken);
        userService.changePassword(Long.parseLong(userId), request);
        return Result.success();
    }
    
    @Operation(summary = "上传头像")
    @PostMapping("/avatar")
    @PreAuthorize("isAuthenticated()")
    public Result<String> uploadAvatar(@RequestParam("file") org.springframework.web.multipart.MultipartFile file,
                                       @RequestHeader("Authorization") String token) {
        String accessToken = token.replace("Bearer ", "");
        String userId = JwtUtil.getUserIdFromToken(accessToken);
        String avatarUrl = userService.uploadAvatar(Long.parseLong(userId), file);
        return Result.success(avatarUrl);
    }
    
    @Operation(summary = "获取用户学习进度")
    @GetMapping("/{userId}/learning-progress")
    @PreAuthorize("isAuthenticated()")
    public Result<java.util.List<com.jiyi.user.dto.LearningProgress>> getLearningProgress(@PathVariable Long userId) {
        java.util.List<com.jiyi.user.dto.LearningProgress> progress = userService.getLearningProgress(userId);
        return Result.success(progress);
    }
    
    @Operation(summary = "获取用户收藏的路线")
    @GetMapping("/{userId}/saved-routes")
    @PreAuthorize("isAuthenticated()")
    public Result<java.util.List<com.jiyi.user.dto.SavedRoute>> getSavedRoutes(@PathVariable Long userId) {
        java.util.List<com.jiyi.user.dto.SavedRoute> routes = userService.getSavedRoutes(userId);
        return Result.success(routes);
    }
    
    @Operation(summary = "获取用户动态")
    @GetMapping("/{userId}/posts")
    public Result<java.util.List<com.jiyi.user.dto.UserPost>> getUserPosts(@PathVariable Long userId,
                                                                            @RequestParam(defaultValue = "1") Integer page,
                                                                            @RequestParam(defaultValue = "10") Integer size) {
        java.util.List<com.jiyi.user.dto.UserPost> posts = userService.getUserPosts(userId, page, size);
        return Result.success(posts);
    }
}
