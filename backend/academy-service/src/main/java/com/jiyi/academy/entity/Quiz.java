package com.jiyi.academy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("quiz")
public class Quiz {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long chapterId;
    
    private String title;
    
    private Integer passScore;
    
    private Integer timeLimit;
    
    private LocalDateTime createdAt;
}
