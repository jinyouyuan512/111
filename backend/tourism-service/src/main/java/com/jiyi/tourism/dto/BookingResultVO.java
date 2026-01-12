package com.jiyi.tourism.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 预订结果VO
 */
@Data
public class BookingResultVO {
    private Long orderId;
    private String orderNo;
    private String ticketName;
    private String visitDate;
    private Integer quantity;
    private BigDecimal totalPrice;
    private String status;
    private String qrCode;
    private String message;
}
