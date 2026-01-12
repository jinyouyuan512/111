package com.jiyi.tourism.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TicketBookingRequest {
    private Long itineraryId;
    private List<TicketItem> tickets;
    
    @Data
    public static class TicketItem {
        private Long attractionId;
        private LocalDate visitDate;
        private Integer quantity;
    }
}
