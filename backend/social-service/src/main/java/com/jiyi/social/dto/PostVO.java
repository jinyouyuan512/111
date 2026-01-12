package com.jiyi.social.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "动态视图对象")
public class PostVO {
    @Schema(description = "动态ID")
    private Long id;
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "用户昵称")
    private String userNickname;
    @Schema(description = "用户头像")
    private String userAvatar;
    @Schema(description = "标题")
    private String title;
    @Schema(description = "内容")
    private String content;
    @Schema(description = "图片列表")
    private List<String> images;
    @Schema(description = "视频信息")
    private VideoVO video;
    @Schema(description = "位置")
    private String location;
    @Schema(description = "分类")
    private String category;
    @Schema(description = "点赞数")
    private Integer likesCount;
    @Schema(description = "评论数")
    private Integer commentsCount;
    @Schema(description = "分享数")
    private Integer sharesCount;
    @Schema(description = "是否已点赞")
    private Boolean liked;
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
