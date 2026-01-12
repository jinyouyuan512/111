package com.jiyi.social.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "评论视图对象")
public class CommentVO {
    @Schema(description = "评论ID")
    private Long id;
    @Schema(description = "动态ID")
    private Long postId;
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "用户昵称")
    private String userNickname;
    @Schema(description = "用户头像")
    private String userAvatar;
    @Schema(description = "评论内容")
    private String content;
    @Schema(description = "父评论ID")
    private Long parentId;
    @Schema(description = "点赞数")
    private Integer likesCount;
    @Schema(description = "当前用户是否已点赞")
    private Boolean liked;
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
