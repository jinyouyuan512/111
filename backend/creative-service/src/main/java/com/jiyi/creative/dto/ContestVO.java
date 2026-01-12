package com.jiyi.creative.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ContestVO {
    private Long id;
    private String title;
    private String description;
    private String theme;
    private String coverImage;
    private BigDecimal prizePool;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime votingEndTime;
    private String status;
    private Integer participantCount;
    private LocalDateTime createdAt;
}
