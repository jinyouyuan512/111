package com.jiyi.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单实体类
 */
@Data
@TableName("orders")
public class Order {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 订单号
     */
    private String orderNumber;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 订单状态: pending-待支付, paid-待发货, shipped-待收货, completed-已完成, cancelled-已取消
     */
    private String status;
    
    /**
     * 支付方式
     */
    private String paymentMethod;
    
    /**
     * 收货地址
     */
    private String shippingAddress;
    
    /**
     * 物流单号
     */
    private String trackingNumber;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer deleted;
    
    /**
     * 订单项列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<OrderItem> items;
}
