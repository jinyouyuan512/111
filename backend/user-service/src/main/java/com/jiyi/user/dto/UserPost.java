package com.jiyi.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户动态
 */
@Data
@Schema(description = "用户动态")
public class UserPost {
    
    @Schema(description = "动态ID")
    private Long postId;
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "用户昵称")
    private String userNickname;
    
    @Schema(description = "用户头像")
    private String userAvatar;
    
    @Schema(description = "动态内容")
    private String content;
    
    @Schema(description = "图片列表")
    private List<String> images;
    
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
    
    @Schema(description = "发布时间")
    private LocalDateTime createdAt;
}
