package com.jiyi.social.service;

import com.jiyi.social.dto.BadgeVO;
import java.util.List;

/**
 * 徽章服务接口
 */
public interface BadgeService {
    
    /**
     * 获取所有徽章
     */
    List<BadgeVO> getAllBadges(Long userId);
    
    /**
     * 获取用户徽章
     */
    List<BadgeVO> getUserBadges(Long userId);
    
    /**
     * 检查并授予徽章
     */
    void checkAndAwardBadges(Long userId);
    
    /**
     * 授予徽章
     */
    void awardBadge(Long userId, Long badgeId);
}
