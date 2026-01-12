package com.jiyi.social.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 私信视图对象
 */
@Data
@Schema(description = "私信视图对象")
public class MessageVO {
    
    @Schema(description = "消息ID")
    private Long id;
    
    @Schema(description = "发送者ID")
    private Long fromUserId;
    
    @Schema(description = "发送者昵称")
    private String fromUserNickname;
    
    @Schema(description = "发送者头像")
    private String fromUserAvatar;
    
    @Schema(description = "接收者ID")
    private Long toUserId;
    
    @Schema(description = "接收者昵称")
    private String toUserNickname;
    
    @Schema(description = "接收者头像")
    private String toUserAvatar;
    
    @Schema(description = "消息内容")
    private String content;
    
    @Schema(description = "消息类型")
    private String type;
    
    @Schema(description = "媒体URL")
    private String mediaUrl;
    
    @Schema(description = "已读状态")
    private Integer readStatus;
    
    @Schema(description = "阅读时间")
    private LocalDateTime readAt;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
