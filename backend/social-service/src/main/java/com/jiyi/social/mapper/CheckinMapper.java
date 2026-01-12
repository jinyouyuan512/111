package com.jiyi.social.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiyi.social.entity.Checkin;
import org.apache.ibatis.annotations.Mapper;

/**
 * 打卡记录Mapper
 */
@Mapper
public interface CheckinMapper extends BaseMapper<Checkin> {
}
