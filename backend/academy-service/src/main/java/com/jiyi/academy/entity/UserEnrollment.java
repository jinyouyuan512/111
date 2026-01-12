package com.jiyi.academy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_enrollment")
public class UserEnrollment {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long courseId;
    
    @TableField("enrolled_at")
    private LocalDateTime enrollTime;
    
    private String status;
    
    private Integer progress;
    
    private LocalDateTime completedTime;
}
