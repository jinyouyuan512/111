package com.jiyi.social.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiyi.common.result.Result;
import com.jiyi.social.entity.Comment;
import com.jiyi.social.entity.Post;
import com.jiyi.social.entity.Report;
import com.jiyi.social.mapper.CommentMapper;
import com.jiyi.social.mapper.PostMapper;
import com.jiyi.social.mapper.ReportMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员 - 内容管理控制器
 */
@Tag(name = "管理员-内容管理")
@RestController
@RequestMapping("/api/admin/content")
@RequiredArgsConstructor
public class AdminContentController {

    private final PostMapper postMapper;
    private final CommentMapper commentMapper;
    private final ReportMapper reportMapper;

    // ==================== 动态管理 ====================

    @Operation(summary = "获取动态列表")
    @GetMapping("/posts")
    public Result<Map<String, Object>> listPosts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        
        Page<Post> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Post::getContent, keyword);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Post::getStatus, status);
        }
        wrapper.orderByDesc(Post::getCreatedAt);
        
        Page<Post> result = postMapper.selectPage(pageParam, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        
        return Result.success(data);
    }

    @Operation(summary = "获取动态详情")
    @GetMapping("/posts/{postId}")
    public Result<Post> getPostDetail(@PathVariable Long postId) {
        Post post = postMapper.selectById(postId);
        return Result.success(post);
    }

    @Operation(summary = "隐藏动态")
    @PostMapping("/posts/{postId}/hide")
    public Result<Void> hidePost(@PathVariable Long postId) {
        Post post = postMapper.selectById(postId);
        if (post != null) {
            post.setStatus("hidden");
            postMapper.updateById(post);
        }
        return Result.success();
    }

    @Operation(summary = "显示动态")
    @PostMapping("/posts/{postId}/show")
    public Result<Void> showPost(@PathVariable Long postId) {
        Post post = postMapper.selectById(postId);
        if (post != null) {
            post.setStatus("normal");
            postMapper.updateById(post);
        }
        return Result.success();
    }

    @Operation(summary = "删除动态")
    @DeleteMapping("/posts/{postId}")
    public Result<Void> deletePost(@PathVariable Long postId) {
        postMapper.deleteById(postId);
        return Result.success();
    }

    // ==================== 评论管理 ====================

    @Operation(summary = "获取评论列表")
    @GetMapping("/comments")
    public Result<Map<String, Object>> listComments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "50") Integer size,
            @RequestParam(required = false) String keyword) {
        
        Page<Comment> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Comment::getContent, keyword);
        }
        wrapper.orderByDesc(Comment::getCreatedAt);
        
        Page<Comment> result = commentMapper.selectPage(pageParam, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        
        return Result.success(data);
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/comments/{commentId}")
    public Result<Void> deleteComment(@PathVariable Long commentId) {
        commentMapper.deleteById(commentId);
        return Result.success();
    }

    // ==================== 举报管理 ====================

    @Operation(summary = "获取举报列表")
    @GetMapping("/reports")
    public Result<Map<String, Object>> listReports(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String status) {
        
        Page<Report> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Report> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(status)) {
            wrapper.eq(Report::getStatus, status);
        }
        wrapper.orderByDesc(Report::getCreatedAt);
        
        Page<Report> result = reportMapper.selectPage(pageParam, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        
        return Result.success(data);
    }

    @Operation(summary = "处理举报-通过")
    @PostMapping("/reports/{reportId}/resolve")
    public Result<Void> resolveReport(@PathVariable Long reportId) {
        Report report = reportMapper.selectById(reportId);
        if (report != null) {
            report.setStatus("resolved");
            reportMapper.updateById(report);
            
            // 如果是动态举报，隐藏该动态
            if ("post".equals(report.getTargetType())) {
                Post post = postMapper.selectById(report.getTargetId());
                if (post != null) {
                    post.setStatus("hidden");
                    postMapper.updateById(post);
                }
            }
        }
        return Result.success();
    }

    @Operation(summary = "处理举报-驳回")
    @PostMapping("/reports/{reportId}/reject")
    public Result<Void> rejectReport(@PathVariable Long reportId) {
        Report report = reportMapper.selectById(reportId);
        if (report != null) {
            report.setStatus("rejected");
            reportMapper.updateById(report);
        }
        return Result.success();
    }

    @Operation(summary = "获取待处理举报数量")
    @GetMapping("/reports/pending-count")
    public Result<Long> getPendingReportCount() {
        LambdaQueryWrapper<Report> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Report::getStatus, "pending");
        Long count = reportMapper.selectCount(wrapper);
        return Result.success(count);
    }

    // ==================== 统计 ====================

    @Operation(summary = "获取内容统计")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getContentStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 总动态数
        stats.put("totalPosts", postMapper.selectCount(null));
        
        // 今日新增动态
        LambdaQueryWrapper<Post> todayPostWrapper = new LambdaQueryWrapper<>();
        todayPostWrapper.apply("DATE(created_at) = CURDATE()");
        stats.put("newPostsToday", postMapper.selectCount(todayPostWrapper));
        
        // 总评论数
        stats.put("totalComments", commentMapper.selectCount(null));
        
        // 待处理举报
        LambdaQueryWrapper<Report> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(Report::getStatus, "pending");
        stats.put("pendingReports", reportMapper.selectCount(pendingWrapper));
        
        return Result.success(stats);
    }
}
