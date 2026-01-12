package com.jiyi.tourism.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jiyi.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 语音讲解实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("audio_guide")
public class AudioGuide extends BaseEntity {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long spotId;
    
    private String title;
    
    private Integer duration;  // 时长（秒）
    
    private String transcript; // 文字稿
    
    private String audioUrl;   // 音频URL
    
    private Integer orderNum;  // 排序
}
