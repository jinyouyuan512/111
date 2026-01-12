package com.jiyi.tourism.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("accommodation")
public class Accommodation {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long routeId;
    private Integer dayNum;
    private String name;
    private String type;
    private String address;
    private String priceRange;
    private BigDecimal rating;
    private String contact;
}
