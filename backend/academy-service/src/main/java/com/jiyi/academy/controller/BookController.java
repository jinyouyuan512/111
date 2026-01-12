package com.jiyi.academy.controller;

import com.jiyi.academy.entity.Book;
import com.jiyi.academy.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 红色读物控制器
 */
@RestController
@RequestMapping("/api/academy/books")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BookController {
    
    private final BookService bookService;
    
    /**
     * 获取书籍列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getBookList(
            @RequestParam(required = false) String category) {
        List<Book> books = bookService.getBookList(category);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("books", books);
        result.put("total", books.size());
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取书籍详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getBookDetail(@PathVariable Long id) {
        Book book = bookService.getBookDetail(id);
        bookService.incrementReadCount(id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("book", book);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取推荐书籍
     */
    @GetMapping("/recommended")
    public ResponseEntity<Map<String, Object>> getRecommendedBooks() {
        List<Book> books = bookService.getRecommendedBooks();
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("books", books);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 搜索书籍
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchBooks(@RequestParam String keyword) {
        List<Book> books = bookService.searchBooks(keyword);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("books", books);
        result.put("total", books.size());
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 收藏书籍
     */
    @PostMapping("/{id}/favorite")
    public ResponseEntity<Map<String, Object>> favoriteBook(@PathVariable Long id) {
        bookService.incrementFavoriteCount(id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "收藏成功");
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 添加书籍（供工作流回调使用）
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addBook(@RequestBody Book book) {
        Map<String, Object> result = new HashMap<>();
        try {
            bookService.addBook(book);
            result.put("success", true);
            result.put("message", "书籍添加成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "添加失败: " + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }
    
    /**
     * 触发n8n工作流同步书籍
     */
    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> triggerBookSync() {
        Map<String, Object> result = new HashMap<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String n8nUrl = "http://localhost:5678/webhook/academy/sync-books";
            restTemplate.postForObject(n8nUrl, null, String.class);
            result.put("success", true);
            result.put("message", "书籍同步任务已触发");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "触发失败: " + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }
}
