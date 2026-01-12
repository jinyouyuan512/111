package com.jiyi.guide.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("guide_route")
public class GuideRoute {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long scenicAreaId;
    private String name;
    private String description;
    private Integer duration;
    private BigDecimal distance;
    private String difficulty;
    private String pathData;
    private LocalDateTime createdAt;
    @TableLogic
    private Integer deleted;
}
