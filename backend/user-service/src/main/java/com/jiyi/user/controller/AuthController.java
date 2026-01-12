package com.jiyi.user.controller;

import com.jiyi.common.result.Result;
import com.jiyi.user.dto.*;
import com.jiyi.user.entity.User;
import com.jiyi.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Tag(name = "认证管理")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<User> register(@RequestBody RegisterRequest request) {
        User user = authService.register(request);
        return Result.success(user);
    }
    
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return Result.success(response);
    }
    
    @Operation(summary = "刷新令牌")
    @PostMapping("/refresh")
    public Result<LoginResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        LoginResponse response = authService.refreshToken(request);
        return Result.success(response);
    }
    
    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String token) {
        // 从token中提取用户ID
        String accessToken = token.replace("Bearer ", "");
        String userId = com.jiyi.common.util.JwtUtil.getUserIdFromToken(accessToken);
        authService.logout(Long.parseLong(userId));
        return Result.success();
    }
}
