package com.jiyi.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiyi.common.exception.BusinessException;
import com.jiyi.common.util.JwtUtil;
import com.jiyi.user.dto.*;
import com.jiyi.user.entity.User;
import com.jiyi.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate redisTemplate;
    
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    
    /**
     * 用户注册
     */
    public User register(RegisterRequest request) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> usernameQuery = new LambdaQueryWrapper<>();
        usernameQuery.eq(User::getUsername, request.getUsername());
        if (userMapper.selectCount(usernameQuery) > 0) {
            throw new BusinessException(409, "用户名已存在");
        }
        
        // 检查邮箱是否已存在
        LambdaQueryWrapper<User> emailQuery = new LambdaQueryWrapper<>();
        emailQuery.eq(User::getEmail, request.getEmail());
        if (userMapper.selectCount(emailQuery) > 0) {
            throw new BusinessException(409, "邮箱已被注册");
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());
        user.setRole("user");
        user.setLevel(1);
        user.setPoints(0);
        user.setAvatar("https://via.placeholder.com/150");
        
        userMapper.insert(user);
        return user;
    }
    
    /**
     * 用户登录
     */
    public LoginResponse login(LoginRequest request) {
        // 查询用户
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        query.eq(User::getUsername, request.getUsername());
        User user = userMapper.selectOne(query);
        
        if (user == null) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        
        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        
        // 更新最后登录时间
        user.setLastLoginAt(LocalDateTime.now());
        userMapper.updateById(user);
        
        // 生成令牌
        String accessToken = JwtUtil.generateAccessToken(
            user.getId().toString(), 
            user.getUsername(), 
            user.getRole()
        );
        String refreshToken = JwtUtil.generateRefreshToken(user.getId().toString());
        
        // 存储刷新令牌到Redis
        redisTemplate.opsForValue().set(
            Objects.requireNonNull(REFRESH_TOKEN_PREFIX + Objects.requireNonNull(user.getId()).toString()),
            Objects.requireNonNull(refreshToken),
            7,
            TimeUnit.DAYS
        );
        
        // 构建响应
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(user, userInfo);
        
        return new LoginResponse(accessToken, refreshToken, userInfo);
    }
    
    /**
     * 刷新令牌
     */
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        try {
            // 验证刷新令牌
            if (!JwtUtil.validateToken(request.getRefreshToken())) {
                throw new BusinessException(401, "刷新令牌无效或已过期");
            }
            
            String userId = JwtUtil.getUserIdFromToken(request.getRefreshToken());
            
            // 验证Redis中的刷新令牌
            String storedToken = redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + userId);
            if (storedToken == null || !storedToken.equals(request.getRefreshToken())) {
                throw new BusinessException(401, "刷新令牌无效");
            }
            
            // 查询用户
            User user = userMapper.selectById(Long.parseLong(userId));
            if (user == null) {
                throw new BusinessException(404, "用户不存在");
            }
            
            // 生成新的访问令牌
            String accessToken = JwtUtil.generateAccessToken(
                user.getId().toString(),
                user.getUsername(),
                user.getRole()
            );
            
            // 构建响应
            UserInfo userInfo = new UserInfo();
            BeanUtils.copyProperties(user, userInfo);
            
            return new LoginResponse(accessToken, request.getRefreshToken(), userInfo);
        } catch (Exception e) {
            throw new BusinessException(401, "刷新令牌失败: " + e.getMessage());
        }
    }
    
    /**
     * 用户登出
     */
    public void logout(Long userId) {
        // 删除Redis中的刷新令牌
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + userId);
    }
}
