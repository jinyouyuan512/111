package com.jiyi.academy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("course")
public class Course {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title;
    
    private String category;
    
    private String description;
    
    private String instructor;
    
    private String coverImage;
    
    private BigDecimal totalHours;
    
    private String level;
    
    private String status;
    
    private Integer enrollmentCount;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @TableLogic
    private Integer deleted;
}
