package com.jiyi.creative.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DesignerVO {
    private Long id;
    private Long userId;
    private String realName;
    private String bio;
    private List<String> skills;
    private String portfolioUrl;
    private Integer experienceYears;
    private BigDecimal rating;
    private Integer completedProjects;
    private Boolean verified;
    private BigDecimal matchScore;
}
