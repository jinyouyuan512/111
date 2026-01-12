package com.jiyi.social.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * 创建动态请求
 */
@Data
@Schema(description = "创建动态请求")
public class PostCreateRequest {
    
    @Schema(description = "动态标题")
    private String title;
    
    @NotBlank(message = "内容不能为空")
    @Schema(description = "动态内容")
    private String content;
    
    @Schema(description = "图片列表")
    private List<String> images;
    
    @Schema(description = "视频信息")
    private VideoDTO video;
    
    @Schema(description = "话题标签列表")
    private List<String> tags;
    
    @Schema(description = "位置")
    private String location;
    
    @Schema(description = "分类")
    private String category;
}

