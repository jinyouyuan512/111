package com.jiyi.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收藏的路线
 */
@Data
@Schema(description = "收藏的路线")
public class SavedRoute {
    
    @Schema(description = "路线ID")
    private Long routeId;
    
    @Schema(description = "路线标题")
    private String routeTitle;
    
    @Schema(description = "路线封面")
    private String routeCover;
    
    @Schema(description = "路线描述")
    private String routeDescription;
    
    @Schema(description = "景点数量")
    private Integer spotsCount;
    
    @Schema(description = "路线距离(公里)")
    private Double distance;
    
    @Schema(description = "难度等级")
    private String difficulty;
    
    @Schema(description = "收藏时间")
    private LocalDateTime savedAt;
}
