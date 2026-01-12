package com.jiyi.social.controller;

import com.jiyi.common.result.Result;
import com.jiyi.common.util.JwtUtil;
import com.jiyi.social.dto.*;
import com.jiyi.social.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "动态管理")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    
    @Operation(summary = "创建动态")
    @PostMapping
    public Result<PostVO> createPost(@RequestBody PostCreateRequest request,
                                     @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return Result.error(401, "请先登录");
        }
        try {
            String accessToken = token.replace("Bearer ", "");
            if (!JwtUtil.validateToken(accessToken)) {
                return Result.error(401, "Token已过期，请重新登录");
            }
            String userId = JwtUtil.getUserIdFromToken(accessToken);
            PostVO post = postService.createPost(Long.parseLong(userId), request);
            return Result.success(post);
        } catch (Exception e) {
            return Result.error(401, "Token无效，请重新登录");
        }
    }
    
    @Operation(summary = "获取动态列表")
    @GetMapping
    public Result<List<PostVO>> getPosts(@RequestParam(required = false) String category,
                                         @RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) String sortBy,
                                         @RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer size,
                                         @RequestHeader(value = "Authorization", required = false) String token) {
        Long currentUserId = null;
        if (token != null && !token.isEmpty()) {
            try {
                String accessToken = token.replace("Bearer ", "");
                if (JwtUtil.validateToken(accessToken)) {
                    currentUserId = Long.parseLong(JwtUtil.getUserIdFromToken(accessToken));
                }
            } catch (Exception e) {
                // Token无效，忽略
            }
        }
        List<PostVO> posts = postService.searchPosts(keyword, category, sortBy, page, size, currentUserId);
        return Result.success(posts);
    }
    
    @Operation(summary = "获取动态详情")
    @GetMapping("/{postId}")
    public Result<PostVO> getPostById(@PathVariable Long postId) {
        PostVO post = postService.getPostById(postId);
        return Result.success(post);
    }
    
    @Operation(summary = "删除动态")
    @DeleteMapping("/{postId}")
    public Result<Void> deletePost(@PathVariable Long postId,
                                   @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return Result.error(401, "请先登录");
        }
        try {
            String accessToken = token.replace("Bearer ", "");
            String userId = JwtUtil.getUserIdFromToken(accessToken);
            postService.deletePost(postId, Long.parseLong(userId));
            return Result.success();
        } catch (Exception e) {
            return Result.error(401, "Token无效，请重新登录");
        }
    }
    
    @Operation(summary = "点赞动态")
    @PostMapping("/{postId}/like")
    public Result<Void> likePost(@PathVariable Long postId,
                                 @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return Result.error(401, "请先登录");
        }
        String accessToken = token.replace("Bearer ", "");
        // 先验证token是否有效
        if (!JwtUtil.validateToken(accessToken)) {
            return Result.error(401, "Token已过期，请重新登录");
        }
        try {
            String userId = JwtUtil.getUserIdFromToken(accessToken);
            postService.likePost(postId, Long.parseLong(userId));
            return Result.success();
        } catch (com.jiyi.common.exception.BusinessException e) {
            // 业务异常，返回具体错误信息
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "服务器错误: " + e.getMessage());
        }
    }
    
    @Operation(summary = "取消点赞")
    @DeleteMapping("/{postId}/like")
    public Result<Void> unlikePost(@PathVariable Long postId,
                                   @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return Result.error(401, "请先登录");
        }
        String accessToken = token.replace("Bearer ", "");
        // 先验证token是否有效
        if (!JwtUtil.validateToken(accessToken)) {
            return Result.error(401, "Token已过期，请重新登录");
        }
        try {
            String userId = JwtUtil.getUserIdFromToken(accessToken);
            postService.unlikePost(postId, Long.parseLong(userId));
            return Result.success();
        } catch (com.jiyi.common.exception.BusinessException e) {
            // 业务异常，返回具体错误信息
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "服务器错误: " + e.getMessage());
        }
    }
    
    @Operation(summary = "分享动态")
    @PostMapping("/{postId}/share")
    public Result<Void> sharePost(@PathVariable Long postId) {
        postService.sharePost(postId);
        return Result.success();
    }
    
    @Operation(summary = "获取用户动态")
    @GetMapping("/user/{userId}")
    public Result<List<PostVO>> getUserPosts(@PathVariable Long userId,
                                             @RequestParam(defaultValue = "1") Integer page,
                                             @RequestParam(defaultValue = "10") Integer size) {
        List<PostVO> posts = postService.getUserPosts(userId, page, size);
        return Result.success(posts);
    }
}
