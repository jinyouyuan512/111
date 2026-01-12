package com.jiyi.guide.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("user_visit")
public class UserVisit {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long scenicAreaId;
    private LocalDate visitDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private LocalDateTime createdAt;
}
