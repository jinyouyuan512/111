package com.jiyi.tourism.dto;

import lombok.Data;
import java.util.List;

/**
 * AI查询请求
 */
@Data
public class AIQueryRequest {
    private String question;
    private String context; // 上下文，如当前景点、路线等
    private List<String> interests; // 用户兴趣标签
    private String travelStyle; // 出行风格
    private Integer budget; // 预算
    private Integer days; // 天数
    private List<String> selectedSpots; // 已选景点
    private String sessionId; // 会话ID，用于多轮对话
}
