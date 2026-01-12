package com.jiyi.guide.dto;

import lombok.Data;

@Data
public class AudioGuideVO {
    private Long id;
    private Long poiId;
    private String title;
    private String content;
    private String audioUrl;
    private Integer duration;
    private String language;
}
