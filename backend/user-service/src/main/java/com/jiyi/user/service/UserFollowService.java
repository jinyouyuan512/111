package com.jiyi.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiyi.common.exception.BusinessException;
import com.jiyi.user.dto.UserInfo;
import com.jiyi.user.entity.User;
import com.jiyi.user.entity.UserFollow;
import com.jiyi.user.mapper.UserFollowMapper;
import com.jiyi.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户关注服务
 */
@Service
@RequiredArgsConstructor
public class UserFollowService {
    
    private final UserFollowMapper userFollowMapper;
    private final UserMapper userMapper;
    
    /**
     * 关注用户
     */
    @Transactional
    public void followUser(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new BusinessException(400, "不能关注自己");
        }
        
        // 检查是否已关注
        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollow::getFollowerId, followerId)
               .eq(UserFollow::getFollowingId, followingId);
        
        if (userFollowMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(400, "已经关注过该用户");
        }
        
        UserFollow userFollow = new UserFollow();
        userFollow.setFollowerId(followerId);
        userFollow.setFollowingId(followingId);
        userFollowMapper.insert(userFollow);
    }
    
    /**
     * 取消关注
     */
    @Transactional
    public void unfollowUser(Long followerId, Long followingId) {
        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollow::getFollowerId, followerId)
               .eq(UserFollow::getFollowingId, followingId);
        
        userFollowMapper.delete(wrapper);
    }
    
    /**
     * 检查是否关注
     */
    public boolean isFollowing(Long followerId, Long followingId) {
        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollow::getFollowerId, followerId)
               .eq(UserFollow::getFollowingId, followingId);
        
        return userFollowMapper.selectCount(wrapper) > 0;
    }
    
    /**
     * 获取关注列表
     */
    public List<UserInfo> getFollowingList(Long userId) {
        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollow::getFollowerId, userId);
        
        List<UserFollow> follows = userFollowMapper.selectList(wrapper);
        List<Long> followingIds = follows.stream()
                .map(UserFollow::getFollowingId)
                .collect(Collectors.toList());
        
        if (followingIds.isEmpty()) {
            return List.of();
        }
        
        List<User> users = userMapper.selectBatchIds(followingIds);
        return users.stream()
                .map(user -> {
                    UserInfo userInfo = new UserInfo();
                    BeanUtils.copyProperties(user, userInfo);
                    return userInfo;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 获取粉丝列表
     */
    public List<UserInfo> getFollowersList(Long userId) {
        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollow::getFollowingId, userId);
        
        List<UserFollow> follows = userFollowMapper.selectList(wrapper);
        List<Long> followerIds = follows.stream()
                .map(UserFollow::getFollowerId)
                .collect(Collectors.toList());
        
        if (followerIds.isEmpty()) {
            return List.of();
        }
        
        List<User> users = userMapper.selectBatchIds(followerIds);
        return users.stream()
                .map(user -> {
                    UserInfo userInfo = new UserInfo();
                    BeanUtils.copyProperties(user, userInfo);
                    return userInfo;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 获取关注数
     */
    public Long getFollowingCount(Long userId) {
        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollow::getFollowerId, userId);
        return userFollowMapper.selectCount(wrapper);
    }
    
    /**
     * 获取粉丝数
     */
    public Long getFollowersCount(Long userId) {
        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollow::getFollowingId, userId);
        return userFollowMapper.selectCount(wrapper);
    }
}
