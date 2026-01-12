package com.jiyi.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiyi.mall.entity.Cart;
import org.apache.ibatis.annotations.Mapper;

/**
 * 购物车Mapper
 */
@Mapper
public interface CartMapper extends BaseMapper<Cart> {
}
