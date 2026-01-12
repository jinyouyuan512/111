package com.jiyi.creative.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreativeCallVO {
    private Long id;
    private String title;
    private String description;
    private String requirements;
    private BigDecimal budget;
    private LocalDateTime deadline;
    private String status;
    private Long publisherId;
    private String publisherType;
    private Integer submissionCount;
    private LocalDateTime createdAt;
}
