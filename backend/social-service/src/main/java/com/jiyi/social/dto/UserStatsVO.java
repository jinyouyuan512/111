package com.jiyi.social.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户统计视图对象
 */
@Data
@Schema(description = "用户统计视图对象")
public class UserStatsVO {
    
    @Schema(description = "用户ID")
    private Long id;
    
    @Schema(description = "用户ID（兼容字段）")
    private Long userId;
    
    @Schema(description = "用户名")
    private String name;
    
    @Schema(description = "用户描述")
    private String desc;
    
    @Schema(description = "头像")
    private String avatar;
    
    @Schema(description = "用户等级")
    private Integer level;
    
    @Schema(description = "总积分")
    private Integer totalPoints;
    
    @Schema(description = "动态数")
    private Long postCount;
    
    @Schema(description = "打卡数")
    private Integer checkinCount;
    
    @Schema(description = "徽章数")
    private Integer badgeCount;
    
    @Schema(description = "关注数")
    private Long followingCount;
    
    @Schema(description = "粉丝数")
    private Long followerCount;
}

