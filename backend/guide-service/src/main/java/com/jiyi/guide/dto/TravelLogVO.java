package com.jiyi.guide.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TravelLogVO {
    private Long id;
    private Long userId;
    private Long visitId;
    private String title;
    private String content;
    private List<String> images;
    private String trackMapUrl;
    private LocalDateTime createdAt;
}
