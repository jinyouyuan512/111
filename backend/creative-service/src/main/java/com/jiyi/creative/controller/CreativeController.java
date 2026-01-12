package com.jiyi.creative.controller;

import com.jiyi.common.result.Result;
import com.jiyi.creative.dto.*;
import com.jiyi.creative.service.CreativeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/creative")
@RequiredArgsConstructor
public class CreativeController {
    
    private final CreativeService creativeService;
    
    /**
     * 获取众创空间首页数据
     */
    @GetMapping("/space")
    public Result<CreativeSpaceVO> getCreativeSpace() {
        return Result.success(creativeService.getCreativeSpace());
    }
    
    /**
     * 获取所有设计大赛
     */
    @GetMapping("/contests")
    public Result<List<ContestVO>> getContests() {
        return Result.success(creativeService.getContests());
    }
    
    /**
     * 获取大赛详情
     */
    @GetMapping("/contests/{contestId}")
    public Result<ContestVO> getContestById(@PathVariable Long contestId) {
        return Result.success(creativeService.getContestById(contestId));
    }
    
    /**
     * 获取所有创意征集
     */
    @GetMapping("/calls")
    public Result<List<CreativeCallVO>> getCreativeCalls() {
        return Result.success(creativeService.getCreativeCalls());
    }
    
    /**
     * 获取创意征集详情
     */
    @GetMapping("/calls/{callId}")
    public Result<CreativeCallVO> getCreativeCallById(@PathVariable Long callId) {
        return Result.success(creativeService.getCreativeCallById(callId));
    }
    
    /**
     * 提交设计作品
     */
    @PostMapping("/designs")
    public Result<DesignVO> submitDesign(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody DesignSubmitRequest request) {
        return Result.success(creativeService.submitDesign(userId, request));
    }
    
    /**
     * 获取作品详情
     */
    @GetMapping("/designs/{designId}")
    public Result<DesignVO> getDesignById(
            @PathVariable Long designId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return Result.success(creativeService.getDesignById(designId, userId));
    }
    
    /**
     * 获取大赛作品列表
     */
    @GetMapping("/contests/{contestId}/designs")
    public Result<List<DesignVO>> getDesignsByContest(
            @PathVariable Long contestId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return Result.success(creativeService.getDesignsByContest(contestId, userId));
    }
    
    /**
     * 获取征集作品列表
     */
    @GetMapping("/calls/{callId}/designs")
    public Result<List<DesignVO>> getDesignsByCall(
            @PathVariable Long callId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return Result.success(creativeService.getDesignsByCall(callId, userId));
    }
    
    /**
     * 获取我的作品
     */
    @GetMapping("/designs/my")
    public Result<List<DesignVO>> getMyDesigns(@RequestHeader("X-User-Id") Long userId) {
        return Result.success(creativeService.getMyDesigns(userId));
    }
    
    /**
     * 审核作品
     */
    @PostMapping("/designs/{designId}/review")
    public Result<Void> reviewDesign(
            @PathVariable Long designId,
            @RequestBody DesignReviewRequest request) {
        creativeService.reviewDesign(designId, request);
        return Result.success();
    }
    
    /**
     * 投票
     */
    @PostMapping("/designs/{designId}/vote")
    public Result<Void> voteDesign(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long designId) {
        creativeService.voteDesign(userId, designId);
        return Result.success();
    }
    
    /**
     * 取消投票
     */
    @DeleteMapping("/designs/{designId}/vote")
    public Result<Void> unvoteDesign(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long designId) {
        creativeService.unvoteDesign(userId, designId);
        return Result.success();
    }
    
    /**
     * 获取排行榜
     */
    @GetMapping("/designs/top")
    public Result<List<DesignVO>> getTopDesigns(@RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(creativeService.getTopDesigns(limit));
    }
    
    /**
     * 发放奖励
     */
    @PostMapping("/designs/{designId}/reward")
    public Result<Void> distributeReward(
            @PathVariable Long designId,
            @RequestParam String type,
            @RequestParam BigDecimal amount) {
        creativeService.distributeReward(designId, type, amount);
        return Result.success();
    }
    
    /**
     * 匹配设计师
     */
    @GetMapping("/requirements/{requirementId}/match")
    public Result<List<DesignerVO>> matchDesigners(@PathVariable Long requirementId) {
        return Result.success(creativeService.matchDesigners(requirementId));
    }
}
