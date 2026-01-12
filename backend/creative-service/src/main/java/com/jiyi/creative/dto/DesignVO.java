package com.jiyi.creative.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DesignVO {
    private Long id;
    private Long designerId;
    private String designerName;
    private Long contestId;
    private String contestTitle;
    private Long callId;
    private String callTitle;
    private String title;
    private Integer categoryType;  // 分类类型
    private String description;
    private String designConcept;
    private List<String> files;
    private String coverImage;
    private String copyrightStatement;
    private String status;
    private String rejectReason;
    private Integer votes;
    private Integer views;
    private Boolean hasVoted;
    private LocalDateTime createdAt;
}
