package com.jiyi.social.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiyi.social.dto.MessageRequest;
import com.jiyi.social.dto.MessageVO;
import com.jiyi.social.entity.PrivateMessage;
import com.jiyi.social.mapper.PrivateMessageMapper;
import com.jiyi.social.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 私信服务实现
 */
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    
    private final PrivateMessageMapper messageMapper;
    
    @Override
    public MessageVO sendMessage(Long fromUserId, MessageRequest request) {
        PrivateMessage message = new PrivateMessage();
        message.setFromUserId(fromUserId);
        message.setToUserId(request.getToUserId());
        message.setContent(request.getContent());
        message.setType(request.getType() != null ? request.getType() : "text");
        message.setMediaUrl(request.getMediaUrl());
        message.setReadStatus(0);
        
        messageMapper.insert(message);
        
        return convertToVO(message);
    }
    
    @Override
    public List<MessageVO> getConversation(Long userId, Long otherUserId, Integer page, Integer size) {
        Page<PrivateMessage> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<PrivateMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w
                .and(w1 -> w1.eq(PrivateMessage::getFromUserId, userId)
                             .eq(PrivateMessage::getToUserId, otherUserId))
                .or(w2 -> w2.eq(PrivateMessage::getFromUserId, otherUserId)
                            .eq(PrivateMessage::getToUserId, userId))
        ).orderByDesc(PrivateMessage::getCreatedAt);
        
        Page<PrivateMessage> result = messageMapper.selectPage(pageObj, wrapper);
        
        return result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    @Override
    public void markAsRead(Long messageId, Long userId) {
        PrivateMessage message = messageMapper.selectById(messageId);
        if (message != null && message.getToUserId().equals(userId) && message.getReadStatus() == 0) {
            message.setReadStatus(1);
            message.setReadAt(LocalDateTime.now());
            messageMapper.updateById(message);
        }
    }
    
    @Override
    public Integer getUnreadCount(Long userId) {
        LambdaQueryWrapper<PrivateMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PrivateMessage::getToUserId, userId)
               .eq(PrivateMessage::getReadStatus, 0);
        return messageMapper.selectCount(wrapper).intValue();
    }
    
    private MessageVO convertToVO(PrivateMessage message) {
        MessageVO vo = new MessageVO();
        vo.setId(message.getId());
        vo.setFromUserId(message.getFromUserId());
        vo.setToUserId(message.getToUserId());
        vo.setContent(message.getContent());
        vo.setType(message.getType());
        vo.setMediaUrl(message.getMediaUrl());
        vo.setReadStatus(message.getReadStatus());
        vo.setReadAt(message.getReadAt());
        vo.setCreatedAt(message.getCreatedAt());
        // TODO: 从用户服务获取用户信息
        vo.setFromUserNickname("用户" + message.getFromUserId());
        vo.setFromUserAvatar("");
        vo.setToUserNickname("用户" + message.getToUserId());
        vo.setToUserAvatar("");
        return vo;
    }
}
