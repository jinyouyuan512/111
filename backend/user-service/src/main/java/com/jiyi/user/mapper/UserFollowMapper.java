package com.jiyi.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiyi.user.entity.UserFollow;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户关注Mapper
 */
@Mapper
public interface UserFollowMapper extends BaseMapper<UserFollow> {
}
