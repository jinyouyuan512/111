package com.jiyi.user.service;

import com.jiyi.common.exception.BusinessException;
import com.jiyi.user.dto.*;
import com.jiyi.user.entity.User;
import com.jiyi.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户服务
 */
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * 根据ID获取用户信息
     */
    public UserInfo getUserById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(user, userInfo);
        return userInfo;
    }
    
    /**
     * 更新用户信息
     */
    public UserInfo updateProfile(Long userId, UserInfo profile) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        
        // 更新允许修改的字段
        if (profile.getNickname() != null) {
            user.setNickname(profile.getNickname());
        }
        if (profile.getPhone() != null) {
            user.setPhone(profile.getPhone());
        }
        if (profile.getAvatar() != null) {
            user.setAvatar(profile.getAvatar());
        }
        
        userMapper.updateById(user);
        
        UserInfo updatedInfo = new UserInfo();
        BeanUtils.copyProperties(user, updatedInfo);
        return updatedInfo;
    }
    
    /**
     * 获取用户统计信息
     */
    public UserStatistics getUserStatistics(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        
        UserStatistics statistics = new UserStatistics();
        statistics.setUserId(userId);
        statistics.setPoints(user.getPoints() != null ? user.getPoints() : 0);
        statistics.setLevel(user.getLevel() != null ? user.getLevel() : 1);
        
        // TODO: 从其他服务获取统计数据
        statistics.setCoursesCount(0);
        statistics.setCompletedCoursesCount(0);
        statistics.setSavedRoutesCount(0);
        statistics.setPostsCount(0);
        statistics.setLikesCount(0);
        statistics.setFollowingCount(0);
        statistics.setFollowersCount(0);
        
        return statistics;
    }
    
    /**
     * 修改密码
     */
    public void changePassword(Long userId, PasswordChangeRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        
        // 验证旧密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new BusinessException(400, "旧密码错误");
        }
        
        // 更新密码
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userMapper.updateById(user);
    }
    
    /**
     * 上传头像
     */
    public String uploadAvatar(Long userId, MultipartFile file) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "请选择要上传的文件");
        }
        
        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException(400, "只能上传图片文件");
        }
        
        // 验证文件大小（最大 5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BusinessException(400, "图片大小不能超过 5MB");
        }
        
        try {
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = "avatar_" + userId + "_" + System.currentTimeMillis() + extension;
            
            // 保存文件到 uploads 目录
            String uploadDir = System.getProperty("user.dir") + "/uploads/avatars/";
            java.io.File dir = new java.io.File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            java.io.File destFile = new java.io.File(uploadDir + filename);
            file.transferTo(destFile);
            
            // 返回可访问的 URL
            String avatarUrl = "http://localhost:8081/uploads/avatars/" + filename;
            
            user.setAvatar(avatarUrl);
            userMapper.updateById(user);
            
            return avatarUrl;
        } catch (java.io.IOException e) {
            throw new BusinessException(500, "文件上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取学习进度
     */
    public List<LearningProgress> getLearningProgress(Long userId) {
        // TODO: 从academy-service获取学习进度
        return new ArrayList<>();
    }
    
    /**
     * 获取收藏的路线
     */
    public List<SavedRoute> getSavedRoutes(Long userId) {
        // TODO: 从tourism-service获取收藏路线
        return new ArrayList<>();
    }
    
    /**
     * 获取用户动态
     */
    public List<UserPost> getUserPosts(Long userId, Integer page, Integer size) {
        // TODO: 从social-service获取用户动态
        return new ArrayList<>();
    }
}
