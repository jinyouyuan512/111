package com.jiyi.social.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 点赞记录实体
 */
@Data
@TableName("like_record")
public class PostLike {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 目标类型: post, comment
     */
    private String targetType;
    
    /**
     * 目标ID (动态ID或评论ID)
     */
    private Long targetId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    // 便捷方法：获取动态ID
    public Long getPostId() {
        return "post".equals(targetType) ? targetId : null;
    }
    
    // 便捷方法：设置动态ID
    public void setPostId(Long postId) {
        this.targetType = "post";
        this.targetId = postId;
    }
}
