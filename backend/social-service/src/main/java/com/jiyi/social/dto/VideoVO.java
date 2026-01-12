package com.jiyi.social.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 视频信息VO
 */
@Data
@Schema(description = "视频信息")
public class VideoVO {
    @Schema(description = "视频URL")
    private String url;
    
    @Schema(description = "缩略图URL")
    private String thumbnail;
    
    @Schema(description = "时长（秒）")
    private Integer duration;
}
