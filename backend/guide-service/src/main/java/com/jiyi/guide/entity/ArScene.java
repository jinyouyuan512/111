package com.jiyi.guide.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ar_scene")
public class ArScene {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long poiId;
    private String name;
    private String description;
    private String sceneDataUrl;
    private String previewImage;
    private LocalDateTime createdAt;
}
