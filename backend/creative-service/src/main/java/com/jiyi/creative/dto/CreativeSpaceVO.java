package com.jiyi.creative.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreativeSpaceVO {
    private List<ContestVO> contests;
    private List<CreativeCallVO> creativeCalls;
    private List<DesignVO> featuredDesigns;
}
