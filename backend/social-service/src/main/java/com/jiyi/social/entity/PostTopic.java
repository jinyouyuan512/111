package com.jiyi.social.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 动态话题关联实体
 */
@Data
@TableName("post_topic")
public class PostTopic {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long postId;
    
    private Long topicId;
}
