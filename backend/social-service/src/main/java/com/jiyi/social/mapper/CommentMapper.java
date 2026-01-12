package com.jiyi.social.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiyi.social.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
