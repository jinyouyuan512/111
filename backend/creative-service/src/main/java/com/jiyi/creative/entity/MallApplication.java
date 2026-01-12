package com.jiyi.creative.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商城上架申请实体
 */
@Data
@TableName("mall_application")
public class MallApplication {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 关联的设计作品ID
     */
    private Long designId;
    
    /**
     * 申请人ID
     */
    private Long userId;
    
    /**
     * 商品名称
     */
    private String productName;
    
    /**
     * 商品分类
     */
    private String category;
    
    /**
     * 商品描述
     */
    private String description;
    
    /**
     * 建议价格
     */
    private BigDecimal suggestedPrice;
    
    /**
     * 初始库存
     */
    private Integer initialStock;
    
    /**
     * 商品图标
     */
    private String icon;
    
    /**
     * 申请状态: pending-待审核, approved-已通过, rejected-已拒绝
     */
    private String status;
    
    /**
     * 审核人ID
     */
    private Long reviewerId;
    
    /**
     * 审核意见
     */
    private String reviewComment;
    
    /**
     * 审核时间
     */
    private LocalDateTime reviewedAt;
    
    /**
     * 生成的商品ID（审核通过后）
     */
    private Long productId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    private Integer deleted;
}
