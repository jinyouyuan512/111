package com.jiyi.tourism.dto;

import lombok.Data;

/**
 * 出行建议VO
 */
@Data
public class TravelTipVO {
    private String icon;
    private String title;
    private String content;
    private String type;  // info, success, warning
}
