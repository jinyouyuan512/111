package com.jiyi.experience.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jiyi.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 体验证书实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("certificate")
public class Certificate extends BaseEntity {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 场景ID
     */
    private Long sceneId;
    
    /**
     * 证书编号
     */
    private String certificateNo;
    
    /**
     * 颁发日期
     */
    private java.time.LocalDate issueDate;
    
    /**
     * 证书图片URL
     */
    private String certificateUrl;
}
