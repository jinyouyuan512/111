package com.jiyi.tourism.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jiyi.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ticket_booking")
public class TicketBooking extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private Long itineraryId;
    private Long attractionId;
    private LocalDate visitDate;
    private Integer quantity;
    private BigDecimal totalPrice;
    private String status;
    private String bookingNo;
    
    @TableField(exist = false)
    private Attraction attraction;
}
