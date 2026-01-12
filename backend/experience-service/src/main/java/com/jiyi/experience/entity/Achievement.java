package com.jiyi.experience.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 成就定义实体
 */
@Data
@TableName("achievement")
public class Achievement {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 成就代码 */
    private String code;
    
    /** 成就名称 */
    private String name;
    
    /** 成就描述 */
    private String description;
    
    /** 成就图标 */
    private String icon;
    
    /** 成就分类 */
    private String category;
    
    /** 条件类型 */
    private String conditionType;
    
    /** 条件值 */
    private Integer conditionValue;
    
    /** 奖励积分 */
    private Integer rewardPoints;
    
    /** 稀有度 1普通 2稀有 3史诗 4传说 */
    private Integer rarity;
    
    /** 排序 */
    private Integer sortOrder;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableLogic
    private Integer deleted;
}
