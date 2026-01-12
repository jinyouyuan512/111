package com.jiyi.guide.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("visit_track")
public class VisitTrack {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long visitId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime recordedAt;
}
