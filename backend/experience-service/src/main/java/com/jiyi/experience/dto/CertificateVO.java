package com.jiyi.experience.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * 证书视图对象
 */
@Data
public class CertificateVO {
    
    private Long id;
    
    /**
     * 证书编号
     */
    private String certificateNo;
    
    /**
     * 场景名称
     */
    private String sceneName;
    
    /**
     * 颁发日期
     */
    private LocalDate issueDate;
    
    /**
     * 证书图片URL
     */
    private String certificateUrl;
}
