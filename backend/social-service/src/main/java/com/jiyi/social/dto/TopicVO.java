package com.jiyi.social.dto;

import lombok.Data;

@Data
public class TopicVO {
    private Long id;
    private String name;
    private String hot;  // 热度显示，如 "12.5w"
    private Long postCount;  // 帖子数量
    private Long viewCount;  // 浏览量
}
