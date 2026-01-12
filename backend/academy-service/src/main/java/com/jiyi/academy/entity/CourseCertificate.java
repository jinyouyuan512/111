package com.jiyi.academy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("course_certificate")
public class CourseCertificate {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long courseId;
    
    private String certificateNo;
    
    private String certificateUrl;
    
    @TableField("points_awarded")
    private Integer points;
    
    @TableField("issued_at")
    private LocalDateTime issueTime;
}
