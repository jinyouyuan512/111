package com.jiyi.creative.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiyi.creative.dto.MallApplicationRequest;
import com.jiyi.creative.dto.MallApplicationVO;
import com.jiyi.creative.entity.MallApplication;
import com.jiyi.creative.service.MallApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 商城上架申请控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/creative/mall-applications")
@CrossOrigin(origins = "*")
public class MallApplicationController {
    
    @Autowired
    private MallApplicationService applicationService;
    
    /**
     * 提交上架申请
     */
    @PostMapping
    public ResponseEntity<?> submitApplication(
            @RequestBody MallApplicationRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("code", 401, "message", "请先登录"));
        }
        
        try {
            MallApplication application = applicationService.submitApplication(request, userId);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "申请提交成功，请等待管理员审核",
                "data", application
            ));
        } catch (Exception e) {
            log.error("提交申请失败", e);
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * 获取申请列表（管理员）- 包含作品媒体信息
     */
    @GetMapping
    public ResponseEntity<?> getApplicationList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        
        Page<MallApplicationVO> result = applicationService.getApplicationListWithDesign(page, size, status);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", Map.of(
            "records", result.getRecords(),
            "total", result.getTotal(),
            "page", result.getCurrent(),
            "size", result.getSize()
        ));
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取我的申请列表
     */
    @GetMapping("/my")
    public ResponseEntity<?> getMyApplications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("code", 401, "message", "请先登录"));
        }
        
        Page<MallApplication> result = applicationService.getUserApplications(userId, page, size);
        
        return ResponseEntity.ok(Map.of(
            "code", 200,
            "data", Map.of(
                "records", result.getRecords(),
                "total", result.getTotal(),
                "page", result.getCurrent(),
                "size", result.getSize()
            )
        ));
    }
    
    /**
     * 获取申请详情（包含作品媒体信息）
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getApplicationById(@PathVariable Long id) {
        MallApplicationVO application = applicationService.getApplicationWithDesign(id);
        
        if (application == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(Map.of("code", 200, "data", application));
    }
    
    /**
     * 审核申请（管理员）
     */
    @PostMapping("/{id}/review")
    public ResponseEntity<?> reviewApplication(
            @PathVariable Long id,
            @RequestBody Map<String, Object> reviewData,
            @RequestHeader(value = "X-User-Id", required = false) Long reviewerId) {
        
        if (reviewerId == null) {
            return ResponseEntity.status(401).body(Map.of("code", 401, "message", "请先登录"));
        }
        
        boolean approved = Boolean.TRUE.equals(reviewData.get("approved"));
        String comment = (String) reviewData.getOrDefault("comment", "");
        
        try {
            MallApplication application = applicationService.reviewApplication(id, approved, comment, reviewerId);
            
            String message = approved ? "审核通过，商品已上架" : "审核已拒绝";
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", message,
                "data", application
            ));
        } catch (Exception e) {
            log.error("审核失败", e);
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * 检查作品是否已申请
     */
    @GetMapping("/check/{designId}")
    public ResponseEntity<?> checkApplication(@PathVariable Long designId) {
        boolean hasApplied = applicationService.hasApplied(designId);
        return ResponseEntity.ok(Map.of(
            "code", 200,
            "data", Map.of("hasApplied", hasApplied)
        ));
    }
}
