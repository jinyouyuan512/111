package com.jiyi.academy.controller;

import com.jiyi.academy.entity.News;
import com.jiyi.academy.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 河北红色新闻控制器
 */
@RestController
@RequestMapping("/api/academy/news")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NewsController {
    
    private final NewsService newsService;
    
    /**
     * 获取新闻列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getNewsList(
            @RequestParam(required = false) String category) {
        List<News> news = newsService.getNewsList(category);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("news", news);
        result.put("total", news.size());
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取新闻详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getNewsDetail(@PathVariable Long id) {
        News news = newsService.getNewsDetail(id);
        newsService.incrementViewCount(id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("news", news);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取置顶新闻
     */
    @GetMapping("/top")
    public ResponseEntity<Map<String, Object>> getTopNews() {
        List<News> news = newsService.getTopNews();
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("news", news);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取热门新闻
     */
    @GetMapping("/hot")
    public ResponseEntity<Map<String, Object>> getHotNews() {
        List<News> news = newsService.getHotNews();
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("news", news);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 搜索新闻
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchNews(@RequestParam String keyword) {
        List<News> news = newsService.searchNews(keyword);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("news", news);
        result.put("total", news.size());
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 点赞新闻
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> likeNews(@PathVariable Long id) {
        newsService.incrementLikeCount(id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "点赞成功");
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 触发n8n工作流采集新闻
     */
    @PostMapping("/crawl")
    public ResponseEntity<Map<String, Object>> triggerNewsCrawl() {
        Map<String, Object> result = new HashMap<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String n8nUrl = "http://localhost:5678/webhook/academy/crawl-news";
            restTemplate.postForObject(n8nUrl, null, String.class);
            result.put("success", true);
            result.put("message", "新闻采集任务已触发");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "触发失败: " + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }
    
    /**
     * 添加新闻（供工作流回调使用）
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addNews(@RequestBody News news) {
        Map<String, Object> result = new HashMap<>();
        try {
            newsService.addNews(news);
            result.put("success", true);
            result.put("message", "新闻添加成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "添加失败: " + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }
}
