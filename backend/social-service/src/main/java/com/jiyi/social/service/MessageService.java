package com.jiyi.social.service;

import com.jiyi.social.dto.MessageRequest;
import com.jiyi.social.dto.MessageVO;
import java.util.List;

/**
 * 私信服务接口
 */
public interface MessageService {
    
    /**
     * 发送私信
     */
    MessageVO sendMessage(Long fromUserId, MessageRequest request);
    
    /**
     * 获取会话消息列表
     */
    List<MessageVO> getConversation(Long userId, Long otherUserId, Integer page, Integer size);
    
    /**
     * 标记消息为已读
     */
    void markAsRead(Long messageId, Long userId);
    
    /**
     * 获取未读消息数
     */
    Integer getUnreadCount(Long userId);
}
