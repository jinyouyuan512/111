package com.jiyi.social.controller;

import com.jiyi.common.result.Result;
import com.jiyi.common.util.JwtUtil;
import com.jiyi.social.dto.CommentCreateRequest;
import com.jiyi.social.dto.CommentVO;
import com.jiyi.social.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "评论管理")
@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {
    
    private final CommentService commentService;
    
    @Operation(summary = "创建评论")
    @PostMapping
    public Result<CommentVO> createComment(@PathVariable Long postId,
                                          @RequestBody CommentCreateRequest request,
                                          @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return Result.error(401, "请先登录");
        }
        try {
            String accessToken = token.replace("Bearer ", "");
            String userId = JwtUtil.getUserIdFromToken(accessToken);
            CommentVO comment = commentService.createComment(postId, Long.parseLong(userId), request);
            return Result.success(comment);
        } catch (Exception e) {
            return Result.error(401, "Token无效，请重新登录");
        }
    }
    
    @Operation(summary = "获取评论列表")
    @GetMapping
    public Result<List<CommentVO>> getComments(@PathVariable Long postId,
                                               @RequestParam(defaultValue = "1") Integer page,
                                               @RequestParam(defaultValue = "20") Integer size,
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
        List<CommentVO> comments = commentService.getComments(postId, page, size, currentUserId);
        return Result.success(comments);
    }
    
    @Operation(summary = "删除评论")
    @DeleteMapping("/{commentId}")
    public Result<Void> deleteComment(@PathVariable Long postId,
                                      @PathVariable Long commentId,
                                      @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return Result.error(401, "请先登录");
        }
        try {
            String accessToken = token.replace("Bearer ", "");
            String userId = JwtUtil.getUserIdFromToken(accessToken);
            commentService.deleteComment(commentId, Long.parseLong(userId));
            return Result.success();
        } catch (Exception e) {
            return Result.error(401, "Token无效，请重新登录");
        }
    }
    
    @Operation(summary = "点赞评论")
    @PostMapping("/{commentId}/like")
    public Result<Void> likeComment(@PathVariable Long postId,
                                    @PathVariable Long commentId,
                                    @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return Result.error(401, "请先登录");
        }
        try {
            String accessToken = token.replace("Bearer ", "");
            String userId = JwtUtil.getUserIdFromToken(accessToken);
            commentService.likeComment(commentId, Long.parseLong(userId));
            return Result.success();
        } catch (Exception e) {
            return Result.error(401, "Token无效，请重新登录");
        }
    }
    
    @Operation(summary = "取消评论点赞")
    @DeleteMapping("/{commentId}/like")
    public Result<Void> unlikeComment(@PathVariable Long postId,
                                      @PathVariable Long commentId,
                                      @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return Result.error(401, "请先登录");
        }
        try {
            String accessToken = token.replace("Bearer ", "");
            String userId = JwtUtil.getUserIdFromToken(accessToken);
            commentService.unlikeComment(commentId, Long.parseLong(userId));
            return Result.success();
        } catch (Exception e) {
            return Result.error(401, "Token无效，请重新登录");
        }
    }
}
