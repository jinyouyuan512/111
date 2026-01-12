package com.jiyi.social.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiyi.common.result.Result;
import com.jiyi.common.util.JwtUtil;
import com.jiyi.social.dto.ReportRequest;
import com.jiyi.social.entity.Report;
import com.jiyi.social.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

/**
 * 举报控制器
 */
@Tag(name = "举报管理")
@RestController
@RequestMapping("/api/social/reports")
@RequiredArgsConstructor
public class ReportController {
    
    private final ReportService reportService;
    
    @Operation(summary = "提交举报")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public Result<Void> submitReport(@Valid @RequestBody ReportRequest request,
                                     @RequestHeader("Authorization") String token) {
        String userId = JwtUtil.getUserIdFromToken(token.replace("Bearer ", ""));
        reportService.submitReport(Long.parseLong(userId), request);
        return Result.success();
    }
    
    @Operation(summary = "获取举报列表（管理员）")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<Report>> getReportList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "目标类型") @RequestParam(required = false) String targetType
    ) {
        return Result.success(reportService.getReportList(page, size, status, targetType));
    }
    
    @Operation(summary = "处理举报（管理员）")
    @PutMapping("/{id}/handle")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> handleReport(
            @Parameter(description = "举报ID") @PathVariable Long id,
            @Parameter(description = "处理结果") @RequestParam String result,
            @Parameter(description = "处理说明") @RequestParam(required = false) String description,
            @RequestHeader("Authorization") String token
    ) {
        String userId = JwtUtil.getUserIdFromToken(token.replace("Bearer ", ""));
        reportService.handleReport(id, Long.parseLong(userId), result, description);
        return Result.success();
    }
    
    @Operation(summary = "获取举报详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Report> getReportById(@Parameter(description = "举报ID") @PathVariable Long id) {
        return Result.success(reportService.getReportById(id));
    }
}
