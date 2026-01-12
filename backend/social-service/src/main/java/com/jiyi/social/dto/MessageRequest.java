package com.jiyi.social.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 私信请求
 */
@Data
@Schema(description = "私信请求")
public class MessageRequest {
    
    @NotNull(message = "接收者ID不能为空")
    @Schema(description = "接收者ID")
    private Long toUserId;
    
    @NotBlank(message = "消息内容不能为空")
    @Schema(description = "消息内容")
    private String content;
    
    @Schema(description = "消息类型: text, image, video")
    private String type;
    
    @Schema(description = "媒体URL")
    private String mediaUrl;
}
