package com.jiyi.social.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiyi.social.dto.TopicVO;
import com.jiyi.social.dto.UserStatsVO;
import com.jiyi.social.entity.Post;
import com.jiyi.social.entity.Topic;
import com.jiyi.social.mapper.PostMapper;
import com.jiyi.social.mapper.TopicMapper;
import com.jiyi.social.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {
    private final TopicMapper topicMapper;
    private final PostMapper postMapper;
    
    @Override
    public List<TopicVO> getHotTopics(Integer limit) {
        // 先更新所有话题的post_count统计
        topicMapper.updatePostCounts();
        
        // 查询热门话题，按post_count排序
        LambdaQueryWrapper<Topic> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Topic::getPostCount)
               .orderByDesc(Topic::getViewCount)
               .last("LIMIT " + limit);
        
        List<Topic> topics = topicMapper.selectList(wrapper);
        
        // 如果数据库中没有话题，返回默认话题
        if (topics.isEmpty()) {
            return createDefaultTopics(limit);
        }
        
        return topics.stream().map(this::convertToVO).collect(Collectors.toList());
    }
    
    /**
     * 创建默认话题（用于数据库为空时展示）
     */
    private List<TopicVO> createDefaultTopics(Integer limit) {
        List<TopicVO> topics = new ArrayList<>();
        
        TopicVO topic1 = new TopicVO();
        topic1.setId(1L);
        topic1.setName("建党百年");
        topic1.setPostCount(125L);
        topic1.setViewCount(125000L);
        topic1.setHot("12.5w");
        topics.add(topic1);
        
        TopicVO topic2 = new TopicVO();
        topic2.setId(2L);
        topic2.setName("西柏坡精神");
        topic2.setPostCount(82L);
        topic2.setViewCount(82000L);
        topic2.setHot("8.2w");
        topics.add(topic2);
        
        TopicVO topic3 = new TopicVO();
        topic3.setId(3L);
        topic3.setName("红色旅游打卡");
        topic3.setPostCount(56L);
        topic3.setViewCount(56000L);
        topic3.setHot("5.6w");
        topics.add(topic3);
        
        TopicVO topic4 = new TopicVO();
        topic4.setId(4L);
        topic4.setName("重走长征路");
        topic4.setPostCount(34L);
        topic4.setViewCount(34000L);
        topic4.setHot("3.4w");
        topics.add(topic4);
        
        TopicVO topic5 = new TopicVO();
        topic5.setId(5L);
        topic5.setName("我的入党故事");
        topic5.setPostCount(21L);
        topic5.setViewCount(21000L);
        topic5.setHot("2.1w");
        topics.add(topic5);
        
        return topics.stream().limit(limit).collect(Collectors.toList());
    }
    
    @Override
    public List<UserStatsVO> getActiveUsers(Integer limit) {
        // 从post表统计用户发帖数，获取最活跃的用户
        List<Map<String, Object>> userStats = postMapper.selectActiveUsers(limit);
        
        List<UserStatsVO> users = new ArrayList<>();
        
        // 如果数据库中有真实数据，使用真实数据
        if (!userStats.isEmpty()) {
            for (Map<String, Object> stat : userStats) {
                UserStatsVO user = new UserStatsVO();
                Long userId = ((Number) stat.get("user_id")).longValue();
                Long postCount = ((Number) stat.get("post_count")).longValue();
                
                user.setId(userId);
                user.setUserId(userId);
                user.setPostCount(postCount);
                
                // 这里应该从user-service获取用户详细信息（用户名、头像、昵称等）
                // 暂时使用默认值，实际应该通过Feign客户端调用user-service
                user.setName("用户" + userId);
                user.setDesc("发布了 " + postCount + " 条红色动态");
                user.setAvatar("https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png");
                user.setFollowerCount(0L);
                
                users.add(user);
            }
        } else {
            // 如果数据库为空，返回示例数据供展示
            users = createSampleUsers(limit);
        }
        
        return users;
    }
    
    /**
     * 创建示例用户数据（用于数据库为空时展示）
     */
    private List<UserStatsVO> createSampleUsers(Integer limit) {
        List<UserStatsVO> users = new ArrayList<>();
        String defaultAvatar = "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png";
        
        UserStatsVO user1 = new UserStatsVO();
        user1.setId(1L);
        user1.setUserId(1L);
        user1.setName("红色追梦人");
        user1.setDesc("分享红色摄影作品");
        user1.setAvatar(defaultAvatar);
        user1.setPostCount(156L);
        user1.setFollowerCount(2340L);
        users.add(user1);
        
        UserStatsVO user2 = new UserStatsVO();
        user2.setId(2L);
        user2.setUserId(2L);
        user2.setName("党史研究员");
        user2.setDesc("专注党史科普");
        user2.setAvatar(defaultAvatar);
        user2.setPostCount(289L);
        user2.setFollowerCount(5678L);
        users.add(user2);
        
        UserStatsVO user3 = new UserStatsVO();
        user3.setId(3L);
        user3.setUserId(3L);
        user3.setName("志愿者小李");
        user3.setDesc("记录志愿服务日常");
        user3.setAvatar(defaultAvatar);
        user3.setPostCount(98L);
        user3.setFollowerCount(1234L);
        users.add(user3);
        
        UserStatsVO user4 = new UserStatsVO();
        user4.setId(4L);
        user4.setUserId(4L);
        user4.setName("西柏坡讲解员");
        user4.setDesc("传播西柏坡精神");
        user4.setAvatar(defaultAvatar);
        user4.setPostCount(234L);
        user4.setFollowerCount(3456L);
        users.add(user4);
        
        UserStatsVO user5 = new UserStatsVO();
        user5.setId(5L);
        user5.setUserId(5L);
        user5.setName("红色文化传承人");
        user5.setDesc("弘扬革命传统");
        user5.setAvatar(defaultAvatar);
        user5.setPostCount(178L);
        user5.setFollowerCount(2890L);
        users.add(user5);
        
        return users.stream().limit(limit).collect(Collectors.toList());
    }
    
    @Override
    public List<TopicVO> searchTopics(String keyword) {
        LambdaQueryWrapper<Topic> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Topic::getName, keyword)
               .orderByDesc(Topic::getViewCount)
               .last("LIMIT 20");
        
        List<Topic> topics = topicMapper.selectList(wrapper);
        
        return topics.stream().map(this::convertToVO).collect(Collectors.toList());
    }
    
    private TopicVO convertToVO(Topic topic) {
        TopicVO vo = new TopicVO();
        vo.setId(topic.getId());
        vo.setName(topic.getName());
        vo.setPostCount(topic.getPostCount() != null ? topic.getPostCount().longValue() : 0L);
        vo.setViewCount(topic.getViewCount() != null ? topic.getViewCount().longValue() : 0L);
        
        // 格式化热度显示 - 优先使用post_count，如果为0则使用view_count
        long count = topic.getPostCount() != null && topic.getPostCount() > 0 
                     ? topic.getPostCount() 
                     : (topic.getViewCount() != null ? topic.getViewCount() : 0);
        
        if (count >= 10000) {
            vo.setHot(String.format("%.1fw", count / 10000.0));
        } else if (count >= 1000) {
            vo.setHot(String.format("%.1fk", count / 1000.0));
        } else {
            vo.setHot(String.valueOf(count));
        }
        
        return vo;
    }
}
