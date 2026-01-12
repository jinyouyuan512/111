package com.jiyi.social.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiyi.social.dto.ReportRequest;
import com.jiyi.social.entity.Report;
import com.jiyi.social.mapper.ReportMapper;
import com.jiyi.social.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 举报服务实现
 */
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    
    private final ReportMapper reportMapper;
    
    @Override
    public void submitReport(Long reporterId, ReportRequest request) {
        Report report = new Report();
        report.setReporterId(reporterId);
        report.setTargetType(request.getTargetType());
        report.setTargetId(request.getTargetId());
        report.setReason(request.getReason());
        report.setDescription(request.getDescription());
        report.setStatus("pending");
        
        reportMapper.insert(report);
    }
    
    @Override
    public Page<Report> getReportList(int page, int size, String status, String targetType) {
        Page<Report> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Report> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(status)) {
            wrapper.eq(Report::getStatus, status);
        }
        if (StringUtils.hasText(targetType)) {
            wrapper.eq(Report::getTargetType, targetType);
        }
        
        wrapper.orderByDesc(Report::getCreatedAt);
        
        return reportMapper.selectPage(pageParam, wrapper);
    }
    
    @Override
    public void handleReport(Long reportId, Long handlerId, String result, String description) {
        Report report = reportMapper.selectById(reportId);
        if (report == null) {
            throw new RuntimeException("举报不存在");
        }
        
        report.setStatus(result);
        report.setHandlerId(handlerId);
        report.setHandleResult(description);
        report.setHandledAt(LocalDateTime.now());
        
        reportMapper.updateById(report);
    }
    
    @Override
    public Report getReportById(Long id) {
        return reportMapper.selectById(id);
    }
}
