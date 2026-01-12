package com.jiyi.academy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("learning_material")
public class LearningMaterial {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long chapterId;
    
    private String title;
    
    private String type;
    
    private String fileUrl;
    
    private Long fileSize;
    
    private LocalDateTime createdAt;
}
