package com.jiyi.tourism.service;

import com.jiyi.tourism.dto.AIQueryRequest;
import com.jiyi.tourism.dto.AIQueryResponse;

import java.util.List;

/**
 * AI智能助手服务接口
 */
public interface AIAssistantService {
    
    /**
     * AI问答
     */
    AIQueryResponse chat(AIQueryRequest request, Long userId);
    
    /**
     * 获取推荐问题
     */
    List<String> getSuggestions(String context);
    
    /**
     * 智能路线推荐
     */
    AIQueryResponse recommendRoute(AIQueryRequest request, Long userId);
    
    /**
     * 景点智能问答
     */
    AIQueryResponse spotQA(Long spotId, String question);
}
