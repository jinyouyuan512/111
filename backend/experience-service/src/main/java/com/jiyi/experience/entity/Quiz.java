package com.jiyi.experience.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 题目实体
 */
@Data
@TableName("quiz")
public class Quiz {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 所属场景ID */
    private Long sceneId;
    
    /** 关联交互点ID */
    private Long interactionPointId;
    
    /** 题目内容 */
    private String question;
    
    /** 题目类型: single/multiple/truefalse */
    private String type;
    
    /** 选项JSON数组 */
    private String options;
    
    /** 正确答案 */
    private String answer;
    
    /** 答案解析 */
    private String explanation;
    
    /** 答对获得积分 */
    private Integer points;
    
    /** 难度 1-3 */
    private Integer difficulty;
    
    /** 排序 */
    private Integer sortOrder;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableLogic
    private Integer deleted;
}
