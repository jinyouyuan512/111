package com.jiyi.tourism.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("transportation")
public class Transportation {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long routeId;
    private Long fromAttractionId;
    private Long toAttractionId;
    private String type;
    private Integer duration;
    private BigDecimal cost;
    private String description;
}
