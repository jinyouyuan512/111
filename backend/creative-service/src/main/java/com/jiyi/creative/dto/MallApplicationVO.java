package com.jiyi.creative.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商城上架申请 VO（包含作品媒体信息）
 */
@Data
public class MallApplicationVO {
    private Long id;
    private Long designId;
    private Long userId;
    private String productName;
    private String category;
    private String description;
    private BigDecimal suggestedPrice;
    private Integer initialStock;
    private String icon;
    private String status;
    private Long reviewerId;
    private String reviewComment;
    private LocalDateTime reviewedAt;
    private Long productId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 作品相关信息
    private String designTitle;
    private String designDescription;
    private String coverImage;
    private List<String> files;  // 作品文件（图片/视频）
    private String designerName;
}
