package com.jiyi.social.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiyi.social.entity.PostTopic;
import org.apache.ibatis.annotations.Mapper;

/**
 * 动态话题关联Mapper
 */
@Mapper
public interface PostTopicMapper extends BaseMapper<PostTopic> {
}
