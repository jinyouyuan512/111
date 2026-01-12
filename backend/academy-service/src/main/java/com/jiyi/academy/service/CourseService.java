package com.jiyi.academy.service;

import com.jiyi.academy.dto.CertificateVO;
import com.jiyi.academy.dto.CourseVO;
import com.jiyi.academy.dto.QuizResultVO;
import com.jiyi.academy.dto.QuizSubmitRequest;

import java.util.List;

public interface CourseService {
    /**
     * 获取课程列表
     */
    List<CourseVO> getCourseList(String category, Long userId);
    
    /**
     * 获取课程详情
     */
    CourseVO getCourseDetail(Long courseId, Long userId);
    
    /**
     * 报名课程
     */
    void enrollCourse(Long userId, Long courseId);
    
    /**
     * 更新学习进度
     */
    void updateProgress(Long userId, Long courseId, Long chapterId);
    
    /**
     * 提交测验
     */
    QuizResultVO submitQuiz(Long userId, QuizSubmitRequest request);
    
    /**
     * 颁发证书
     */
    CertificateVO issueCertificate(Long userId, Long courseId);
    
    /**
     * 获取用户证书列表
     */
    List<CertificateVO> getUserCertificates(Long userId);
    
    /**
     * 获取课程进度百分比
     */
    Integer getCourseProgress(Long userId, Long courseId);
}
