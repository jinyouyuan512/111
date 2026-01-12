package com.jiyi.guide.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("poi")
public class Poi {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long scenicAreaId;
    private String name;
    private String description;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String type;
    private Integer triggerRadius;
    private String qrCode;
    private LocalDateTime createdAt;
    @TableLogic
    private Integer deleted;
}
