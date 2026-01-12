package com.jiyi.experience.dto;

import lombok.Data;

/**
 * 答题请求
 */
@Data
public class AnswerQuizRequest {
    
    /** 用户ID */
    private Long userId;
    
    /** 题目ID */
    private Long quizId;
    
    /** 用户答案 */
    private String answer;
    
    /** 用时（秒） */
    private Integer timeSpent;
}
