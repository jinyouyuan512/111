package com.jiyi.academy.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CourseVO {
    private Long id;
    private String title;
    private String category;
    private String description;
    private String instructor;
    private String coverImage;
    private BigDecimal totalHours;
    private String level;
    private Integer enrollmentCount;
    private LocalDateTime createdAt;
    private List<ChapterVO> chapters;
    private Boolean enrolled;
    private Integer progressPercentage;
}
