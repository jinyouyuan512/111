package com.jiyi.social.service;

import com.jiyi.social.dto.CheckinRequest;
import com.jiyi.social.dto.CheckinVO;
import java.util.List;

/**
 * 打卡服务接口
 */
public interface CheckinService {
    
    /**
     * 创建打卡记录
     */
    CheckinVO createCheckin(Long userId, CheckinRequest request);
    
    /**
     * 获取用户打卡记录
     */
    List<CheckinVO> getUserCheckins(Long userId, Integer page, Integer size);
    
    /**
     * 获取景点打卡记录
     */
    List<CheckinVO> getLocationCheckins(Long locationId, Integer page, Integer size);
    
    /**
     * 获取用户打卡统计
     */
    Integer getUserCheckinCount(Long userId);
}
