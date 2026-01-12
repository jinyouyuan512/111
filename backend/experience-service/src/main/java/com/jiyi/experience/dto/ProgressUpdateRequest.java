package com.jiyi.experience.dto;

import lombok.Data;

/**
 * 进度更新请求
 */
@Data
public class ProgressUpdateRequest {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 场景ID
     */
    private Long sceneId;
    
    /**
     * 完成的交互点ID
     */
    private Long interactionPointId;
}
