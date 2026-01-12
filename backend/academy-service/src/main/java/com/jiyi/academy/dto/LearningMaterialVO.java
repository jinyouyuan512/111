package com.jiyi.academy.dto;

import lombok.Data;

@Data
public class LearningMaterialVO {
    private Long id;
    private String title;
    private String type;
    private String fileUrl;
    private Long fileSize;
}
