package com.jiyi.creative.dto;

import lombok.Data;
import java.util.List;

@Data
public class DesignSubmitRequest {
    private Long contestId;
    private Long callId;
    private String title;
    private Integer categoryType;
    private String description;
    private String designConcept;
    private List<String> files;
    private String coverImage;
    private String copyrightStatement;
    private String tags;
}
