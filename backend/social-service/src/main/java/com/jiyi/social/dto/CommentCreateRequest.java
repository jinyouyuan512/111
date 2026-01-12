package com.jiyi.social.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
@Schema(description = "创建评论请求")
public class CommentCreateRequest {
    @NotBlank(message = "评论内容不能为空")
    @Schema(description = "评论内容")
    private String content;
    @Schema(description = "父评论ID")
    private Long parentId;
}
