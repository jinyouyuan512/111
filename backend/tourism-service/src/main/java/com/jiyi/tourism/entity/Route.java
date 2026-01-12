package com.jiyi.tourism.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jiyi.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("route")
public class Route extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    private String description;
    private Integer days;
    private BigDecimal estimatedCost;
    private String difficulty;
    private String season;
    private String tags;
    private String coverImage;
    private String status;
    private Integer viewCount;
    private Integer bookingCount;
    
    @TableField(exist = false)
    private String[] tagList;
    
    @TableField(exist = false)
    private List<Attraction> attractions;
    
    @TableField(exist = false)
    private List<Transportation> transportations;
    
    @TableField(exist = false)
    private List<Accommodation> accommodations;
}
