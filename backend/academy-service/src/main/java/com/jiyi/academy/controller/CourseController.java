package com.jiyi.academy.controller;

import com.jiyi.academy.dto.CertificateVO;
import com.jiyi.academy.dto.CourseVO;
import com.jiyi.academy.dto.QuizResultVO;
import com.jiyi.academy.dto.QuizSubmitRequest;
import com.jiyi.academy.service.CourseService;
import com.jiyi.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/academy/courses")
@CrossOrigin(origins = "*")
public class CourseController {
    
    @Autowired
    private CourseService courseService;
    
    /**
     * 获取课程列表
     */
    @GetMapping
    public Result<List<CourseVO>> getCourseList(
            @RequestParam(required = false) String category,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            List<CourseVO> courses = courseService.getCourseList(category, userId);
            return Result.success(courses);
        } catch (Exception e) {
            log.error("获取课程列表失败", e);
            return Result.error("获取课程列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取课程详情
     */
    @GetMapping("/{courseId}")
    public Result<CourseVO> getCourseDetail(
            @PathVariable Long courseId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            CourseVO course = courseService.getCourseDetail(courseId, userId);
            return Result.success(course);
        } catch (Exception e) {
            log.error("获取课程详情失败", e);
            return Result.error("获取课程详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 报名课程
     */
    @PostMapping("/{courseId}/enroll")
    public Result<Void> enrollCourse(
            @PathVariable Long courseId,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            courseService.enrollCourse(userId, courseId);
            Result<Void> result = Result.success();
            result.setMessage("报名成功");
            return result;
        } catch (Exception e) {
            log.error("报名课程失败", e);
            return Result.error("报名失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新学习进度
     */
    @PostMapping("/{courseId}/chapters/{chapterId}/complete")
    public Result<Void> updateProgress(
            @PathVariable Long courseId,
            @PathVariable Long chapterId,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            courseService.updateProgress(userId, courseId, chapterId);
            Result<Void> result = Result.success();
            result.setMessage("进度更新成功");
            return result;
        } catch (Exception e) {
            log.error("更新学习进度失败", e);
            return Result.error("更新进度失败: " + e.getMessage());
        }
    }
    
    /**
     * 提交测验
     */
    @PostMapping("/quizzes/submit")
    public Result<QuizResultVO> submitQuiz(
            @RequestBody QuizSubmitRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            request.setUserId(userId);
            QuizResultVO result = courseService.submitQuiz(userId, request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("提交测验失败", e);
            return Result.error("提交测验失败: " + e.getMessage());
        }
    }
    
    /**
     * 颁发证书
     */
    @PostMapping("/{courseId}/certificate")
    public Result<CertificateVO> issueCertificate(
            @PathVariable Long courseId,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            CertificateVO certificate = courseService.issueCertificate(userId, courseId);
            Result<CertificateVO> result = Result.success(certificate);
            result.setMessage("证书颁发成功");
            return result;
        } catch (Exception e) {
            log.error("颁发证书失败", e);
            return Result.error("颁发证书失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户证书列表
     */
    @GetMapping("/certificates")
    public Result<List<CertificateVO>> getUserCertificates(
            @RequestHeader("X-User-Id") Long userId) {
        try {
            List<CertificateVO> certificates = courseService.getUserCertificates(userId);
            return Result.success(certificates);
        } catch (Exception e) {
            log.error("获取证书列表失败", e);
            return Result.error("获取证书列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取课程进度
     */
    @GetMapping("/{courseId}/progress")
    public Result<Integer> getCourseProgress(
            @PathVariable Long courseId,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            Integer progress = courseService.getCourseProgress(userId, courseId);
            return Result.success(progress);
        } catch (Exception e) {
            log.error("获取课程进度失败", e);
            return Result.error("获取课程进度失败: " + e.getMessage());
        }
    }
}
