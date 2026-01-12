package com.jiyi.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiyi.common.result.Result;
import com.jiyi.user.entity.User;
import com.jiyi.user.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员 - 用户管理控制器
 */
@Tag(name = "管理员-用户管理")
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserMapper userMapper;

    @Operation(summary = "获取用户列表")
    @GetMapping
    public Result<Map<String, Object>> listUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role) {
        
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or().like(User::getEmail, keyword)
                    .or().like(User::getNickname, keyword));
        }
        if (StringUtils.hasText(role)) {
            wrapper.eq(User::getRole, role);
        }
        wrapper.orderByDesc(User::getCreatedAt);
        
        Page<User> result = userMapper.selectPage(pageParam, wrapper);
        
        // 隐藏密码字段
        result.getRecords().forEach(user -> user.setPasswordHash(null));
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("pages", result.getPages());
        data.put("current", result.getCurrent());
        
        return Result.success(data);
    }

    @Operation(summary = "获取用户详情")
    @GetMapping("/{userId}")
    public Result<User> getUserDetail(@PathVariable Long userId) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setPasswordHash(null); // 不返回密码
        }
        return Result.success(user);
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/{userId}")
    public Result<Void> updateUser(@PathVariable Long userId, @RequestBody User user) {
        user.setId(userId);
        user.setPasswordHash(null); // 不允许通过此接口修改密码
        userMapper.updateById(user);
        return Result.success();
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{userId}")
    public Result<Void> deleteUser(@PathVariable Long userId) {
        userMapper.deleteById(userId);
        return Result.success();
    }

    @Operation(summary = "禁用/启用用户")
    @PostMapping("/{userId}/toggle-status")
    public Result<Void> toggleUserStatus(@PathVariable Long userId) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            // 使用 deleted 字段作为禁用标志
            user.setDeleted(user.getDeleted() == 1 ? 0 : 1);
            userMapper.updateById(user);
        }
        return Result.success();
    }

    @Operation(summary = "修改用户角色")
    @PostMapping("/{userId}/role")
    public Result<Void> changeUserRole(@PathVariable Long userId, @RequestBody Map<String, String> body) {
        String role = body.get("role");
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setRole(role);
            userMapper.updateById(user);
        }
        return Result.success();
    }

    @Operation(summary = "获取用户统计")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getUserStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 总用户数
        Long totalUsers = userMapper.selectCount(null);
        stats.put("totalUsers", totalUsers);
        
        // 今日新增
        LambdaQueryWrapper<User> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.apply("DATE(created_at) = CURDATE()");
        Long newUsersToday = userMapper.selectCount(todayWrapper);
        stats.put("newUsersToday", newUsersToday);
        
        // 各角色数量
        LambdaQueryWrapper<User> adminWrapper = new LambdaQueryWrapper<>();
        adminWrapper.eq(User::getRole, "admin");
        stats.put("adminCount", userMapper.selectCount(adminWrapper));
        
        LambdaQueryWrapper<User> designerWrapper = new LambdaQueryWrapper<>();
        designerWrapper.eq(User::getRole, "designer");
        stats.put("designerCount", userMapper.selectCount(designerWrapper));
        
        return Result.success(stats);
    }
}
