package com.jiyi.social.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiyi.social.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface PostMapper extends BaseMapper<Post> {
    
    /**
     * 查询活跃用户统计
     */
    @Select("SELECT user_id, COUNT(*) as post_count " +
            "FROM post " +
            "WHERE deleted = 0 " +
            "GROUP BY user_id " +
            "ORDER BY post_count DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> selectActiveUsers(Integer limit);
}
