package com.jiyi.tourism.dto;

import lombok.Data;

/**
 * 门票可用性VO
 */
@Data
public class TicketAvailabilityVO {
    private Long ticketId;
    private String date;
    private Integer totalStock;
    private Integer soldCount;
    private Integer availableCount;
    private Boolean isAvailable;
    private String message;
}
