package com.jiyi.guide.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("travel_log")
public class TravelLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long visitId;
    private String title;
    private String content;
    private String images;
    private String trackMapUrl;
    private LocalDateTime createdAt;
}
