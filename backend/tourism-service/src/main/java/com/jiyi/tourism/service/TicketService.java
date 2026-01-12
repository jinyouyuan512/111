package com.jiyi.tourism.service;

import com.jiyi.tourism.dto.*;
import java.util.List;

/**
 * 门票服务接口
 */
public interface TicketService {
    
    /**
     * 获取门票列表
     */
    List<TicketVO> getTickets();
    
    /**
     * 获取门票详情
     */
    TicketVO getTicketDetail(Long ticketId);
    
    /**
     * 预订门票
     */
    BookingResultVO bookTicket(Long userId, TicketBookRequest request);
    
    /**
     * 获取用户订单
     */
    List<TicketOrderVO> getUserOrders(Long userId);
    
    /**
     * 取消订单
     */
    void cancelOrder(Long userId, Long orderId);
    
    /**
     * 检查门票可用性
     */
    TicketAvailabilityVO checkAvailability(Long ticketId, String date);
}
