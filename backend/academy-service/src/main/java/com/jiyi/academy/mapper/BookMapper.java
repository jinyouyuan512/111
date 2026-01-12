package com.jiyi.academy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiyi.academy.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface BookMapper extends BaseMapper<Book> {
    
    @Update("UPDATE red_book SET read_count = read_count + 1 WHERE id = #{id}")
    void incrementReadCount(@Param("id") Long id);
    
    @Update("UPDATE red_book SET favorite_count = favorite_count + 1 WHERE id = #{id}")
    void incrementFavoriteCount(@Param("id") Long id);
}
