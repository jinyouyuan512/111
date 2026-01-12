package com.jiyi.creative.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiyi.creative.entity.Design;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DesignMapper extends BaseMapper<Design> {
    /**
     * 选择性插入，只插入非 null 字段
     */
    int insertSelective(Design design);
}
