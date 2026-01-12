package com.jiyi.social.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 打卡请求
 */
@Data
@Schema(description = "打卡请求")
public class CheckinRequest {
    
    @NotNull(message = "位置ID不能为空")
    @Schema(description = "位置ID")
    private Long locationId;
    
    @NotBlank(message = "位置名称不能为空")
    @Schema(description = "位置名称")
    private String locationName;
    
    @NotNull(message = "纬度不能为空")
    @Schema(description = "纬度")
    private BigDecimal latitude;
    
    @NotNull(message = "经度不能为空")
    @Schema(description = "经度")
    private BigDecimal longitude;
    
    @Schema(description = "打卡内容")
    private String content;
    
    @Schema(description = "关联动态ID")
    private Long postId;
}
