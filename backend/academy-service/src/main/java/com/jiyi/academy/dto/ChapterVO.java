package com.jiyi.academy.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChapterVO {
    private Long id;
    private Long courseId;
    private String title;
    private String videoUrl;
    private Integer duration;
    private Integer orderNum;
    private Boolean completed;
    private List<LearningMaterialVO> materials;
    private QuizVO quiz;
}
