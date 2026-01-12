package com.jiyi.social.controller;

import com.jiyi.common.result.Result;
import com.jiyi.common.util.JwtUtil;
import com.jiyi.social.dto.MessageRequest;
import com.jiyi.social.dto.MessageVO;
import com.jiyi.social.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

/**
 * 私信控制器
 */
@Tag(name = "私信管理")
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    
    private final MessageService messageService;
    
    @Operation(summary = "发送私信")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public Result<MessageVO> sendMessage(@Valid @RequestBody MessageRequest request,
                                        @RequestHeader("Authorization") String token) {
        String userId = JwtUtil.getUserIdFromToken(token.replace("Bearer ", ""));
        MessageVO message = messageService.sendMessage(Long.parseLong(userId), request);
        return Result.success(message);
    }
    
    @Operation(summary = "获取会话消息")
    @GetMapping("/conversation/{otherUserId}")
    @PreAuthorize("isAuthenticated()")
    public Result<List<MessageVO>> getConversation(@PathVariable Long otherUserId,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "20") Integer size,
                                                   @RequestHeader("Authorization") String token) {
        String userId = JwtUtil.getUserIdFromToken(token.replace("Bearer ", ""));
        List<MessageVO> messages = messageService.getConversation(Long.parseLong(userId), otherUserId, page, size);
        return Result.success(messages);
    }
    
    @Operation(summary = "标记消息为已读")
    @PutMapping("/{messageId}/read")
    @PreAuthorize("isAuthenticated()")
    public Result<Void> markAsRead(@PathVariable Long messageId,
                                   @RequestHeader("Authorization") String token) {
        String userId = JwtUtil.getUserIdFromToken(token.replace("Bearer ", ""));
        messageService.markAsRead(messageId, Long.parseLong(userId));
        return Result.success();
    }
    
    @Operation(summary = "获取未读消息数")
    @GetMapping("/unread-count")
    @PreAuthorize("isAuthenticated()")
    public Result<Integer> getUnreadCount(@RequestHeader("Authorization") String token) {
        String userId = JwtUtil.getUserIdFromToken(token.replace("Bearer ", ""));
        Integer count = messageService.getUnreadCount(Long.parseLong(userId));
        return Result.success(count);
    }
}
