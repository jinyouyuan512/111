package com.jiyi.guide.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("audio_guide")
public class AudioGuide {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long poiId;
    private String title;
    private String content;
    private String audioUrl;
    private Integer duration;
    private String language;
    private LocalDateTime createdAt;
}
