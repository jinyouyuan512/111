package com.jiyi.academy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiyi.academy.entity.News;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface NewsMapper extends BaseMapper<News> {
    
    @Update("UPDATE red_news SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(@Param("id") Long id);
    
    @Update("UPDATE red_news SET like_count = like_count + 1 WHERE id = #{id}")
    void incrementLikeCount(@Param("id") Long id);
}
