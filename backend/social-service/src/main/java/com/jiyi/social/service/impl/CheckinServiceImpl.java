package com.jiyi.social.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiyi.social.dto.CheckinRequest;
import com.jiyi.social.dto.CheckinVO;
import com.jiyi.social.entity.Checkin;
import com.jiyi.social.mapper.CheckinMapper;
import com.jiyi.social.service.CheckinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 打卡服务实现
 */
@Service
@RequiredArgsConstructor
public class CheckinServiceImpl implements CheckinService {
    
    private final CheckinMapper checkinMapper;
    
    @Override
    public CheckinVO createCheckin(Long userId, CheckinRequest request) {
        Checkin checkin = new Checkin();
        checkin.setUserId(userId);
        checkin.setLocationId(request.getLocationId());
        checkin.setLocationName(request.getLocationName());
        checkin.setLatitude(request.getLatitude());
        checkin.setLongitude(request.getLongitude());
        checkin.setPostId(request.getPostId());
        
        checkinMapper.insert(checkin);
        
        return convertToVO(checkin);
    }
    
    @Override
    public List<CheckinVO> getUserCheckins(Long userId, Integer page, Integer size) {
        Page<Checkin> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<Checkin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Checkin::getUserId, userId)
               .orderByDesc(Checkin::getCreatedAt);
        
        Page<Checkin> result = checkinMapper.selectPage(pageObj, wrapper);
        
        return result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<CheckinVO> getLocationCheckins(Long locationId, Integer page, Integer size) {
        Page<Checkin> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<Checkin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Checkin::getLocationId, locationId)
               .orderByDesc(Checkin::getCreatedAt);
        
        Page<Checkin> result = checkinMapper.selectPage(pageObj, wrapper);
        
        return result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Integer getUserCheckinCount(Long userId) {
        LambdaQueryWrapper<Checkin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Checkin::getUserId, userId);
        return checkinMapper.selectCount(wrapper).intValue();
    }
    
    private CheckinVO convertToVO(Checkin checkin) {
        CheckinVO vo = new CheckinVO();
        vo.setId(checkin.getId());
        vo.setUserId(checkin.getUserId());
        vo.setLocationId(checkin.getLocationId());
        vo.setLocationName(checkin.getLocationName());
        vo.setLatitude(checkin.getLatitude());
        vo.setLongitude(checkin.getLongitude());
        vo.setPostId(checkin.getPostId());
        vo.setCreatedAt(checkin.getCreatedAt());
        // TODO: 从用户服务获取用户信息
        vo.setUserNickname("用户" + checkin.getUserId());
        vo.setUserAvatar("");
        return vo;
    }
}
