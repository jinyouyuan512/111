package com.jiyi.creative.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("contest")
public class Contest {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title;
    private String description;
    private String theme;
    private String coverImage;
    private BigDecimal prizePool;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime votingEndTime;
    private String status; // upcoming, ongoing, voting, completed
    private Integer participantCount;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    private Integer deleted;
}
