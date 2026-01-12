package com.jiyi.creative.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("vote_record")
public class VoteRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private Long designId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt = LocalDateTime.now();  // 添加默认值
}
