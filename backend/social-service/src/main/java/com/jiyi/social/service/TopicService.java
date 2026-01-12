package com.jiyi.social.service;

import com.jiyi.social.dto.TopicVO;
import com.jiyi.social.dto.UserStatsVO;

import java.util.List;

public interface TopicService {
    /**
     * 获取热门话题
     */
    List<TopicVO> getHotTopics(Integer limit);
    
    /**
     * 获取活跃用户
     */
    List<UserStatsVO> getActiveUsers(Integer limit);
    
    /**
     * 搜索话题
     */
    List<TopicVO> searchTopics(String keyword);
}
