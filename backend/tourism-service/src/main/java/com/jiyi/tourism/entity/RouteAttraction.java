package com.jiyi.tourism.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("route_attraction")
public class RouteAttraction {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long routeId;
    private Long attractionId;
    private Integer dayNum;
    private Integer orderNum;
    private String visitTime;
    private String notes;
}
