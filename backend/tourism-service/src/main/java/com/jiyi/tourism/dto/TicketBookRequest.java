package com.jiyi.tourism.dto;

import lombok.Data;

/**
 * 门票预订请求
 */
@Data
public class TicketBookRequest {
    private Long ticketId;
    private String visitDate;
    private Integer quantity;
    private String visitorName;
    private String visitorPhone;
    private String visitorIdCard;
}
