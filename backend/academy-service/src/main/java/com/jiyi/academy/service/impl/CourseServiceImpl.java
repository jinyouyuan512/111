package com.jiyi.academy.service.impl;

import com.jiyi.academy.dto.CertificateVO;
import com.jiyi.academy.dto.CourseVO;
import com.jiyi.academy.dto.QuizResultVO;
import com.jiyi.academy.dto.QuizSubmitRequest;
import com.jiyi.academy.service.CourseService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {
    
    @Override
    public List<CourseVO> getCourseList(String category, Long userId) {
        return Collections.emptyList();
    }
    
    @Override
    public CourseVO getCourseDetail(Long courseId, Long userId) {
        return null;
    }
    
    @Override
    public void enrollCourse(Long userId, Long courseId) {
    }
    
    @Override
    public void updateProgress(Long userId, Long courseId, Long chapterId) {
    }
    
    @Override
    public QuizResultVO submitQuiz(Long userId, QuizSubmitRequest request) {
        return null;
    }

    @Override
    public CertificateVO issueCertificate(Long userId, Long courseId) {
        return null;
    }
    
    @Override
    public List<CertificateVO> getUserCertificates(Long userId) {
        return Collections.emptyList();
    }
    
    @Override
    public Integer getCourseProgress(Long userId, Long courseId) {
        return 0;
    }
}