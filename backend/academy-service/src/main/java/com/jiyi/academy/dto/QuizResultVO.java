package com.jiyi.academy.dto;

import lombok.Data;

@Data
public class QuizResultVO {
    private Long quizId;
    private Integer score;
    private Integer totalScore;
    private Boolean passed;
    private Integer passScore;
}
