package com.jiyi.social.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 徽章视图对象
 */
@Data
@Schema(description = "徽章视图对象")
public class BadgeVO {
    
    @Schema(description = "徽章ID")
    private Long id;
    
    @Schema(description = "徽章名称")
    private String name;
    
    @Schema(description = "徽章描述")
    private String description;
    
    @Schema(description = "图标URL")
    private String icon;
    
    @Schema(description = "类型")
    private String type;
    
    @Schema(description = "获得积分")
    private Integer points;
    
    @Schema(description = "稀有度")
    private String rarity;
    
    @Schema(description = "是否已获得")
    private Boolean obtained;
    
    @Schema(description = "获得时间")
    private LocalDateTime obtainedAt;
}
