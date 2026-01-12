package com.jiyi.academy.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CertificateVO {
    private Long id;
    private Long courseId;
    private String courseTitle;
    private String certificateNo;
    private String certificateUrl;
    private Integer pointsAwarded;
    private LocalDateTime issuedAt;
}
