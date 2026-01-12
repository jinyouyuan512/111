package com.jiyi.social.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiyi.social.entity.Topic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * 话题Mapper
 */
@Mapper
public interface TopicMapper extends BaseMapper<Topic> {
    
    /**
     * 更新所有话题的post_count统计
     * 从post_topic表统计每个话题被使用的次数
     */
    @Update("UPDATE topic t " +
            "SET t.post_count = (" +
            "    SELECT COUNT(*) " +
            "    FROM post_topic pt " +
            "    WHERE pt.topic_id = t.id" +
            ")")
    void updatePostCounts();
}

