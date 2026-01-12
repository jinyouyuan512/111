package com.jiyi.social.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 打卡视图对象
 */
@Data
@Schema(description = "打卡视图对象")
public class CheckinVO {
    
    @Schema(description = "打卡ID")
    private Long id;
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "用户昵称")
    private String userNickname;
    
    @Schema(description = "用户头像")
    private String userAvatar;
    
    @Schema(description = "位置ID")
    private Long locationId;
    
    @Schema(description = "位置名称")
    private String locationName;
    
    @Schema(description = "纬度")
    private BigDecimal latitude;
    
    @Schema(description = "经度")
    private BigDecimal longitude;
    
    @Schema(description = "关联动态ID")
    private Long postId;
    
    @Schema(description = "打卡时间")
    private LocalDateTime createdAt;
}
