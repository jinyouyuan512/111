package com.jiyi.social.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiyi.social.dto.ReportRequest;
import com.jiyi.social.entity.Report;

/**
 * 举报服务接口
 */
public interface ReportService {
    
    /**
     * 提交举报
     */
    void submitReport(Long reporterId, ReportRequest request);
    
    /**
     * 获取举报列表
     */
    Page<Report> getReportList(int page, int size, String status, String targetType);
    
    /**
     * 处理举报
     */
    void handleReport(Long reportId, Long handlerId, String result, String description);
    
    /**
     * 获取举报详情
     */
    Report getReportById(Long id);
}
