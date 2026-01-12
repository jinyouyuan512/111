package com.jiyi.tourism.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 门票订单VO
 */
@Data
public class TicketOrderVO {
    private Long id;
    private String orderNo;
    private String ticketName;
    private String spotName;
    private String visitDate;
    private Integer quantity;
    private BigDecimal totalPrice;
    private String status;
    private String qrCode;
    private LocalDateTime createdAt;
}
