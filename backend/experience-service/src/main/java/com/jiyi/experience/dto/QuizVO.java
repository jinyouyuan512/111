package com.jiyi.experience.dto;

import lombok.Data;
import java.util.List;

/**
 * 题目视图对象
 */
@Data
public class QuizVO {
    
    private Long id;
    private Long sceneId;
    private String question;
    private String type;
    private List<String> options;
    private Integer points;
    private Integer difficulty;
    
    /** 用户答案（答题后返回） */
    private String userAnswer;
    
    /** 正确答案（答题后返回） */
    private String correctAnswer;
    
    /** 是否正确（答题后返回） */
    private Boolean isCorrect;
    
    /** 答案解析（答题后返回） */
    private String explanation;
}
