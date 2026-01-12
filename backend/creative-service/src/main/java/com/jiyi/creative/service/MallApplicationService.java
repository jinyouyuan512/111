package com.jiyi.creative.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiyi.creative.dto.MallApplicationRequest;
import com.jiyi.creative.dto.MallApplicationVO;
import com.jiyi.creative.entity.MallApplication;

public interface MallApplicationService {
    
    /**
     * 提交上架申请
     */
    MallApplication submitApplication(MallApplicationRequest request, Long userId);
    
    /**
     * 获取申请列表（管理员）- 包含作品媒体信息
     */
    Page<MallApplicationVO> getApplicationListWithDesign(int page, int size, String status);
    
    /**
     * 获取申请列表（管理员）
     */
    Page<MallApplication> getApplicationList(int page, int size, String status);
    
    /**
     * 获取用户的申请列表
     */
    Page<MallApplication> getUserApplications(Long userId, int page, int size);
    
    /**
     * 获取申请详情
     */
    MallApplication getApplicationById(Long id);
    
    /**
     * 获取申请详情（包含作品媒体信息）
     */
    MallApplicationVO getApplicationWithDesign(Long id);
    
    /**
     * 审核申请
     */
    MallApplication reviewApplication(Long id, boolean approved, String comment, Long reviewerId);
    
    /**
     * 检查作品是否已申请上架
     */
    boolean hasApplied(Long designId);
}
