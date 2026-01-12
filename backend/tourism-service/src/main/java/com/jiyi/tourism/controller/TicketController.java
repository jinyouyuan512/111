package com.jiyi.tourism.controller;

import com.jiyi.common.result.Result;
import com.jiyi.tourism.dto.*;
import com.jiyi.tourism.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 门票预订控制器
 * 提供门票查询、在线预订等功能
 */
@Slf4j
@RestController
@RequestMapping("/api/tourism/tickets")
@RequiredArgsConstructor
public class TicketController {
    
    private final TicketService ticketService;
    
    /**
     * 获取门票列表
     */
    @GetMapping
    public Result<List<TicketVO>> getTickets() {
        log.info("Getting all tickets");
        return Result.success(ticketService.getTickets());
    }
    
    /**
     * 获取门票详情
     */
    @GetMapping("/{ticketId}")
    public Result<TicketVO> getTicketDetail(@PathVariable Long ticketId) {
        log.info("Getting ticket detail: {}", ticketId);
        return Result.success(ticketService.getTicketDetail(ticketId));
    }
    
    /**
     * 预订门票
     */
    @PostMapping("/book")
    public Result<BookingResultVO> bookTicket(
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId,
            @RequestBody TicketBookRequest request) {
        log.info("Booking ticket for user: {}, request: {}", userId, request);
        return Result.success(ticketService.bookTicket(userId, request));
    }
    
    /**
     * 获取用户订单
     */
    @GetMapping("/orders")
    public Result<List<TicketOrderVO>> getMyOrders(
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId) {
        log.info("Getting orders for user: {}", userId);
        return Result.success(ticketService.getUserOrders(userId));
    }
    
    /**
     * 取消订单
     */
    @PostMapping("/orders/{orderId}/cancel")
    public Result<Void> cancelOrder(
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId,
            @PathVariable Long orderId) {
        log.info("Canceling order: {} for user: {}", orderId, userId);
        ticketService.cancelOrder(userId, orderId);
        return Result.success(null);
    }
    
    /**
     * 检查门票可用性
     */
    @GetMapping("/{ticketId}/availability")
    public Result<TicketAvailabilityVO> checkAvailability(
            @PathVariable Long ticketId,
            @RequestParam String date) {
        log.info("Checking availability for ticket: {}, date: {}", ticketId, date);
        return Result.success(ticketService.checkAvailability(ticketId, date));
    }
}
