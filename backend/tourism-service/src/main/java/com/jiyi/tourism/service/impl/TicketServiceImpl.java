package com.jiyi.tourism.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiyi.tourism.dto.*;
import com.jiyi.tourism.entity.Attraction;
import com.jiyi.tourism.entity.TicketBooking;
import com.jiyi.tourism.mapper.AttractionMapper;
import com.jiyi.tourism.mapper.TicketBookingMapper;
import com.jiyi.tourism.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {
    
    private final AttractionMapper attractionMapper;
    private final TicketBookingMapper ticketBookingMapper;
    
    private static final Map<String, String> SPOT_ICONS = new HashMap<>();
    private static final Map<String, String> SPOT_GRADIENTS = new HashMap<>();
    
    static {
        SPOT_ICONS.put("西柏坡纪念馆", "🏛️");
        SPOT_ICONS.put("狼牙山", "⛰️");
        SPOT_ICONS.put("冉庄地道战遗址", "🚇");
        SPOT_ICONS.put("李大钊纪念馆", "📚");
        SPOT_ICONS.put("白求恩柯棣华纪念馆", "🏥");
        SPOT_ICONS.put("华北军区烈士陵园", "🎖️");
        SPOT_ICONS.put("白洋淀雁翎队纪念馆", "🚤");
        SPOT_ICONS.put("塞罕坝展览馆", "🌲");
        
        SPOT_GRADIENTS.put("西柏坡纪念馆", "linear-gradient(135deg, #c41e3a, #8b0000)");
        SPOT_GRADIENTS.put("狼牙山", "linear-gradient(135deg, #2c5530, #1a3a1c)");
        SPOT_GRADIENTS.put("冉庄地道战遗址", "linear-gradient(135deg, #5d4e37, #3d3225)");
        SPOT_GRADIENTS.put("李大钊纪念馆", "linear-gradient(135deg, #1e3a5f, #0d1f33)");
        SPOT_GRADIENTS.put("白求恩柯棣华纪念馆", "linear-gradient(135deg, #2e7d32, #1b5e20)");
        SPOT_GRADIENTS.put("华北军区烈士陵园", "linear-gradient(135deg, #37474f, #263238)");
        SPOT_GRADIENTS.put("白洋淀雁翎队纪念馆", "linear-gradient(135deg, #1890ff, #096dd9)");
        SPOT_GRADIENTS.put("塞罕坝展览馆", "linear-gradient(135deg, #228b22, #006400)");
    }
    
    @Override
    public List<TicketVO> getTickets() {
        log.info("Getting all tickets");
        
        LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attraction::getStatus, "open");
        wrapper.eq(Attraction::getCategory, "革命遗址");
        wrapper.orderByDesc(Attraction::getRating);
        
        List<Attraction> attractions = attractionMapper.selectList(wrapper);
        
        return attractions.stream()
                .map(this::convertToTicketVO)
                .collect(Collectors.toList());
    }
    
    @Override
    public TicketVO getTicketDetail(Long ticketId) {
        log.info("Getting ticket detail: {}", ticketId);
        
        Attraction attraction = attractionMapper.selectById(ticketId);
        if (attraction == null) {
            throw new RuntimeException("门票不存在");
        }
        
        return convertToTicketVO(attraction);
    }
    
    @Override
    @Transactional
    public BookingResultVO bookTicket(Long userId, TicketBookRequest request) {
        log.info("Booking ticket for user: {}, request: {}", userId, request);
        
        Attraction attraction = attractionMapper.selectById(request.getTicketId());
        if (attraction == null) {
            throw new RuntimeException("门票不存在");
        }
        
        // 创建订单
        TicketBooking booking = new TicketBooking();
        booking.setUserId(userId);
        booking.setAttractionId(request.getTicketId());
        booking.setVisitDate(LocalDate.parse(request.getVisitDate()));
        booking.setQuantity(request.getQuantity());
        booking.setTotalPrice(attraction.getTicketPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
        booking.setStatus("confirmed");
        booking.setBookingNo(generateBookingNo());
        
        ticketBookingMapper.insert(booking);
        
        // 返回结果
        BookingResultVO result = new BookingResultVO();
        result.setOrderId(booking.getId());
        result.setOrderNo(booking.getBookingNo());
        result.setTicketName(attraction.getName());
        result.setVisitDate(request.getVisitDate());
        result.setQuantity(request.getQuantity());
        result.setTotalPrice(booking.getTotalPrice());
        result.setStatus("confirmed");
        result.setQrCode("QR_" + booking.getBookingNo());
        result.setMessage("预订成功！请凭二维码入园");
        
        return result;
    }
    
    @Override
    public List<TicketOrderVO> getUserOrders(Long userId) {
        log.info("Getting orders for user: {}", userId);
        
        LambdaQueryWrapper<TicketBooking> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TicketBooking::getUserId, userId);
        wrapper.orderByDesc(TicketBooking::getCreatedAt);
        
        List<TicketBooking> bookings = ticketBookingMapper.selectList(wrapper);
        
        return bookings.stream()
                .map(this::convertToTicketOrderVO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void cancelOrder(Long userId, Long orderId) {
        log.info("Canceling order: {} for user: {}", orderId, userId);
        
        TicketBooking booking = ticketBookingMapper.selectById(orderId);
        if (booking == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (!booking.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此订单");
        }
        
        if (!"confirmed".equals(booking.getStatus())) {
            throw new RuntimeException("订单状态不允许取消");
        }
        
        booking.setStatus("cancelled");
        ticketBookingMapper.updateById(booking);
    }
    
    @Override
    public TicketAvailabilityVO checkAvailability(Long ticketId, String date) {
        log.info("Checking availability for ticket: {}, date: {}", ticketId, date);
        
        Attraction attraction = attractionMapper.selectById(ticketId);
        if (attraction == null) {
            throw new RuntimeException("门票不存在");
        }
        
        // 查询当天已售数量
        LambdaQueryWrapper<TicketBooking> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TicketBooking::getAttractionId, ticketId);
        wrapper.eq(TicketBooking::getVisitDate, LocalDate.parse(date));
        wrapper.eq(TicketBooking::getStatus, "confirmed");
        
        Long soldCount = ticketBookingMapper.selectCount(wrapper);
        
        int totalStock = 1000; // 每日限量
        int available = totalStock - soldCount.intValue();
        
        TicketAvailabilityVO vo = new TicketAvailabilityVO();
        vo.setTicketId(ticketId);
        vo.setDate(date);
        vo.setTotalStock(totalStock);
        vo.setSoldCount(soldCount.intValue());
        vo.setAvailableCount(Math.max(0, available));
        vo.setIsAvailable(available > 0);
        vo.setMessage(available > 0 ? "可预订" : "已售罄");
        
        return vo;
    }
    
    private TicketVO convertToTicketVO(Attraction attraction) {
        TicketVO vo = new TicketVO();
        vo.setId(attraction.getId());
        vo.setName(attraction.getName());
        vo.setIcon(SPOT_ICONS.getOrDefault(attraction.getName(), "🏛️"));
        vo.setGradient(SPOT_GRADIENTS.getOrDefault(attraction.getName(), "linear-gradient(135deg, #c41e3a, #8b0000)"));
        vo.setAddress(attraction.getAddress());
        vo.setOpenTime(attraction.getOpeningHours());
        vo.setRating(attraction.getRating().doubleValue());
        vo.setPrice(attraction.getTicketPrice());
        
        // 如果有折扣
        if (attraction.getTicketPrice().compareTo(BigDecimal.ZERO) > 0) {
            vo.setOriginalPrice(attraction.getTicketPrice().multiply(BigDecimal.valueOf(1.2)));
            vo.setDiscount("8折");
        }
        
        vo.setNeedReserve(attraction.getTicketPrice().compareTo(BigDecimal.ZERO) == 0);
        vo.setSold(new Random().nextInt(10000) + 5000);
        vo.setStock(1000);
        vo.setDescription(attraction.getDescription());
        
        return vo;
    }
    
    private TicketOrderVO convertToTicketOrderVO(TicketBooking booking) {
        TicketOrderVO vo = new TicketOrderVO();
        vo.setId(booking.getId());
        vo.setOrderNo(booking.getBookingNo());
        vo.setVisitDate(booking.getVisitDate().toString());
        vo.setQuantity(booking.getQuantity());
        vo.setTotalPrice(booking.getTotalPrice());
        vo.setStatus(booking.getStatus());
        vo.setQrCode("QR_" + booking.getBookingNo());
        vo.setCreatedAt(booking.getCreatedAt());
        
        // 获取景点名称
        Attraction attraction = attractionMapper.selectById(booking.getAttractionId());
        if (attraction != null) {
            vo.setTicketName(attraction.getName() + "门票");
            vo.setSpotName(attraction.getName());
        }
        
        return vo;
    }
    
    private String generateBookingNo() {
        return "TB" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }
}
