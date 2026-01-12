package com.jiyi.creative.dto;

import lombok.Data;

@Data
public class DesignReviewRequest {
    private String status; // approved, rejected
    private String rejectReason;
}
