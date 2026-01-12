package com.jiyi.tourism.controller;

import com.jiyi.common.result.Result;
import com.jiyi.tourism.dto.AIQueryRequest;
import com.jiyi.tourism.dto.AIQueryResponse;
import com.jiyi.tourism.service.AIAssistantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI智能助手控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/tourism/ai")
@RequiredArgsConstructor
public class AIAssistantController {
    
    private final AIAssistantService aiAssistantService;
    
    /**
     * AI问答
     */
    @PostMapping("/chat")
    public Result<AIQueryResponse> chat(
            @RequestBody AIQueryRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        log.info("AI chat request from user: {}, question: {}", userId, request.getQuestion());
        AIQueryResponse response = aiAssistantService.chat(request, userId);
        return Result.success(response);
    }
    
    /**
     * 获取推荐问题
     */
    @GetMapping("/suggestions")
    public Result<List<String>> getSuggestions(
            @RequestParam(required = false) String context) {
        log.info("Getting AI suggestions for context: {}", context);
        List<String> suggestions = aiAssistantService.getSuggestions(context);
        return Result.success(suggestions);
    }
    
    /**
     * 智能路线推荐
     */
    @PostMapping("/recommend-route")
    public Result<AIQueryResponse> recommendRoute(
            @RequestBody AIQueryRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        log.info("AI route recommendation for user: {}", userId);
        AIQueryResponse response = aiAssistantService.recommendRoute(request, userId);
        return Result.success(response);
    }
    
    /**
     * 景点智能问答
     */
    @GetMapping("/spots/{spotId}/qa")
    public Result<AIQueryResponse> spotQA(
            @PathVariable Long spotId,
            @RequestParam String question) {
        log.info("Spot QA for spot: {}, question: {}", spotId, question);
        AIQueryResponse response = aiAssistantService.spotQA(spotId, question);
        return Result.success(response);
    }
}
