package com.jiyi.tourism.dto;

import lombok.Data;

/**
 * 语音讲解VO
 */
@Data
public class AudioGuideVO {
    private Long id;
    private Long spotId;
    private String title;
    private Integer duration;  // 秒
    private String transcript; // 文字稿
    private String audioUrl;
    private Integer orderNum;
}
