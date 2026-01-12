package com.jiyi.tourism.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jiyi.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("attraction")
public class Attraction extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    private String category;
    private String description;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer visitDuration;
    private BigDecimal ticketPrice;
    private String openingHours;
    private String images;
    private BigDecimal rating;
    private String status;
    
    @TableField(exist = false)
    private String[] imageList;
}
