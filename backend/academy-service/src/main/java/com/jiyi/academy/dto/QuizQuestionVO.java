package com.jiyi.academy.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuizQuestionVO {
    private Long id;
    private String question;
    private String type;
    private List<String> options;
    private Integer score;
    private Integer orderNum;
}
