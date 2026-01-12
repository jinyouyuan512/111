package com.jiyi.social.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 举报请求
 */
@Data
@Schema(description = "举报请求")
public class ReportRequest {
    
    @NotBlank(message = "目标类型不能为空")
    @Schema(description = "目标类型: post, comment, user")
    private String targetType;
    
    @NotNull(message = "目标ID不能为空")
    @Schema(description = "目标ID")
    private Long targetId;
    
    @NotBlank(message = "举报原因不能为空")
    @Schema(description = "举报原因")
    private String reason;
    
    @Schema(description = "详细说明")
    private String description;
}
