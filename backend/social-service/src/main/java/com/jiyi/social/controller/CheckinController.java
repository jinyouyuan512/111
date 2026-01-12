package com.jiyi.social.controller;

import com.jiyi.common.result.Result;
import com.jiyi.common.util.JwtUtil;
import com.jiyi.social.dto.CheckinRequest;
import com.jiyi.social.dto.CheckinVO;
import com.jiyi.social.service.CheckinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

/**
 * 打卡控制器
 */
@Tag(name = "打卡管理")
@RestController
@RequestMapping("/api/checkins")
@RequiredArgsConstructor
public class CheckinController {
    
    private final CheckinService checkinService;
    
    @Operation(summary = "创建打卡记录")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public Result<CheckinVO> createCheckin(@Valid @RequestBody CheckinRequest request,
                                          @RequestHeader("Authorization") String token) {
        String userId = JwtUtil.getUserIdFromToken(token.replace("Bearer ", ""));
        CheckinVO checkin = checkinService.createCheckin(Long.parseLong(userId), request);
        return Result.success(checkin);
    }
    
    @Operation(summary = "获取用户打卡记录")
    @GetMapping("/user/{userId}")
    public Result<List<CheckinVO>> getUserCheckins(@PathVariable Long userId,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        List<CheckinVO> checkins = checkinService.getUserCheckins(userId, page, size);
        return Result.success(checkins);
    }
    
    @Operation(summary = "获取景点打卡记录")
    @GetMapping("/location/{locationId}")
    public Result<List<CheckinVO>> getLocationCheckins(@PathVariable Long locationId,
                                                       @RequestParam(defaultValue = "1") Integer page,
                                                       @RequestParam(defaultValue = "10") Integer size) {
        List<CheckinVO> checkins = checkinService.getLocationCheckins(locationId, page, size);
        return Result.success(checkins);
    }
}
