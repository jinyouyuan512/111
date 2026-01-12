package com.jiyi.experience.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiyi.experience.entity.Task;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskMapper extends BaseMapper<Task> {
}
