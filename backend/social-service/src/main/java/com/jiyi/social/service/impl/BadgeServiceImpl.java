package com.jiyi.social.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiyi.social.dto.BadgeVO;
import com.jiyi.social.entity.Badge;
import com.jiyi.social.entity.UserBadge;
import com.jiyi.social.mapper.BadgeMapper;
import com.jiyi.social.mapper.CheckinMapper;
import com.jiyi.social.mapper.PostMapper;
import com.jiyi.social.mapper.UserBadgeMapper;
import com.jiyi.social.service.BadgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 徽章服务实现
 */
@Service
@RequiredArgsConstructor
public class BadgeServiceImpl implements BadgeService {
    
    private final BadgeMapper badgeMapper;
    private final UserBadgeMapper userBadgeMapper;
    private final PostMapper postMapper;
    private final CheckinMapper checkinMapper;
    
    @Override
    public List<BadgeVO> getAllBadges(Long userId) {
        List<Badge> badges = badgeMapper.selectList(null);
        
        // 获取用户已获得的徽章
        LambdaQueryWrapper<UserBadge> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserBadge::getUserId, userId);
        List<UserBadge> userBadges = userBadgeMapper.selectList(wrapper);
        Map<Long, UserBadge> userBadgeMap = userBadges.stream()
                .collect(Collectors.toMap(UserBadge::getBadgeId, ub -> ub));
        
        return badges.stream()
                .map(badge -> {
                    BadgeVO vo = convertToVO(badge);
                    UserBadge userBadge = userBadgeMap.get(badge.getId());
                    if (userBadge != null) {
                        vo.setObtained(true);
                        vo.setObtainedAt(userBadge.getObtainedAt());
                    } else {
                        vo.setObtained(false);
                    }
                    return vo;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<BadgeVO> getUserBadges(Long userId) {
        LambdaQueryWrapper<UserBadge> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserBadge::getUserId, userId)
               .orderByDesc(UserBadge::getObtainedAt);
        List<UserBadge> userBadges = userBadgeMapper.selectList(wrapper);
        
        return userBadges.stream()
                .map(userBadge -> {
                    Badge badge = badgeMapper.selectById(userBadge.getBadgeId());
                    BadgeVO vo = convertToVO(badge);
                    vo.setObtained(true);
                    vo.setObtainedAt(userBadge.getObtainedAt());
                    return vo;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public void checkAndAwardBadges(Long userId) {
        List<Badge> badges = badgeMapper.selectList(null);
        
        for (Badge badge : badges) {
            // 检查用户是否已获得该徽章
            LambdaQueryWrapper<UserBadge> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserBadge::getUserId, userId)
                   .eq(UserBadge::getBadgeId, badge.getId());
            if (userBadgeMapper.selectCount(wrapper) > 0) {
                continue;
            }
            
            // 检查是否满足条件
            boolean shouldAward = false;
            switch (badge.getConditionType()) {
                case "post_count":
                    int postCount = postMapper.selectCount(
                        new LambdaQueryWrapper<com.jiyi.social.entity.Post>()
                            .eq(com.jiyi.social.entity.Post::getUserId, userId)
                    ).intValue();
                    shouldAward = postCount >= badge.getConditionValue();
                    break;
                case "checkin_count":
                    int checkinCount = checkinMapper.selectCount(
                        new LambdaQueryWrapper<com.jiyi.social.entity.Checkin>()
                            .eq(com.jiyi.social.entity.Checkin::getUserId, userId)
                    ).intValue();
                    shouldAward = checkinCount >= badge.getConditionValue();
                    break;
                // 可以添加更多条件类型
            }
            
            if (shouldAward) {
                awardBadge(userId, badge.getId());
            }
        }
    }
    
    @Override
    public void awardBadge(Long userId, Long badgeId) {
        // 检查是否已获得
        LambdaQueryWrapper<UserBadge> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserBadge::getUserId, userId)
               .eq(UserBadge::getBadgeId, badgeId);
        if (userBadgeMapper.selectCount(wrapper) > 0) {
            return;
        }
        
        UserBadge userBadge = new UserBadge();
        userBadge.setUserId(userId);
        userBadge.setBadgeId(badgeId);
        userBadgeMapper.insert(userBadge);
    }
    
    private BadgeVO convertToVO(Badge badge) {
        BadgeVO vo = new BadgeVO();
        vo.setId(badge.getId());
        vo.setName(badge.getName());
        vo.setDescription(badge.getDescription());
        vo.setIcon(badge.getIcon());
        vo.setType(badge.getType());
        vo.setPoints(badge.getPoints());
        vo.setRarity(badge.getRarity());
        return vo;
    }
}
