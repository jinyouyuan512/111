package com.jiyi.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户统计信息
 */
@Data
@Schema(description = "用户统计信息")
public class UserStatistics {
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "学习课程数")
    private Integer coursesCount;
    
    @Schema(description = "完成课程数")
    private Integer completedCoursesCount;
    
    @Schema(description = "收藏路线数")
    private Integer savedRoutesCount;
    
    @Schema(description = "发布动态数")
    private Integer postsCount;
    
    @Schema(description = "获得点赞数")
    private Integer likesCount;
    
    @Schema(description = "关注数")
    private Integer followingCount;
    
    @Schema(description = "粉丝数")
    private Integer followersCount;
    
    @Schema(description = "积分")
    private Integer points;
    
    @Schema(description = "等级")
    private Integer level;
}
