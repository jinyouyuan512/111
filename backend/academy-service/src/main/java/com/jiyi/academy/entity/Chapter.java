package com.jiyi.academy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("chapter")
public class Chapter {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long courseId;
    
    private String title;
    
    private String videoUrl;
    
    private Integer duration;
    
    private Integer orderNum;
    
    private LocalDateTime createdAt;
    
    @TableLogic
    private Integer deleted;
}
