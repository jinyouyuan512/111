package com.jiyi.academy.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuizVO {
    private Long id;
    private String title;
    private Integer passScore;
    private Integer timeLimit;
    private List<QuizQuestionVO> questions;
    private Boolean passed;
    private Integer bestScore;
}
