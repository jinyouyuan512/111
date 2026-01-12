package com.jiyi.creative.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("reward_record")
public class RewardRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long designerId;
    private Long designId;
    private Long contestId;
    private Long callId;
    private String type; // prize, royalty, bonus
    private BigDecimal amount;
    private String status; // pending, paid, failed
    private LocalDateTime paidAt;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
