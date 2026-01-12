package com.jiyi.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学习进度
 */
@Data
@Schema(description = "学习进度")
public class LearningProgress {
    
    @Schema(description = "课程ID")
    private Long courseId;
    
    @Schema(description = "课程标题")
    private String courseTitle;
    
    @Schema(description = "课程封面")
    private String courseCover;
    
    @Schema(description = "总章节数")
    private Integer totalChapters;
    
    @Schema(description = "已完成章节数")
    private Integer completedChapters;
    
    @Schema(description = "进度百分比")
    private Integer progressPercentage;
    
    @Schema(description = "最后学习时间")
    private LocalDateTime lastStudyTime;
    
    @Schema(description = "是否完成")
    private Boolean completed;
}
