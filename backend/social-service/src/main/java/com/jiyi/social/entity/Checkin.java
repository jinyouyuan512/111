package com.jiyi.social.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 打卡记录实体
 */
@Data
@TableName("checkin")
public class Checkin {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 位置ID
     */
    private Long locationId;
    
    /**
     * 位置名称
     */
    private String locationName;
    
    /**
     * 纬度
     */
    private BigDecimal latitude;
    
    /**
     * 经度
     */
    private BigDecimal longitude;
    
    /**
     * 关联动态ID
     */
    private Long postId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
