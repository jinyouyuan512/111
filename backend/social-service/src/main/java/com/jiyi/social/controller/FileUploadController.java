package com.jiyi.social.controller;

import com.jiyi.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Tag(name = "文件上传")
@RestController
@RequestMapping("/api/upload")
public class FileUploadController {
    
    @Value("${file.upload.path:uploads}")
    private String uploadPath;
    
    @Value("${server.port:8083}")
    private String serverPort;
    
    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final long MAX_VIDEO_SIZE = 100 * 1024 * 1024; // 100MB
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of("image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp");
    private static final Set<String> ALLOWED_VIDEO_TYPES = Set.of("video/mp4", "video/avi", "video/mov", "video/wmv");
    
    @Operation(summary = "上传图片")
    @PostMapping("/image")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 验证文件
            if (file.isEmpty()) {
                return Result.error(400, "文件不能为空");
            }
            
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
                return Result.error(400, "不支持的图片格式，仅支持 JPG、PNG、GIF、WEBP");
            }
            
            // 验证文件大小
            if (file.getSize() > MAX_IMAGE_SIZE) {
                return Result.error(400, "图片大小不能超过10MB");
            }
            
            // 保存文件
            String url = saveFile(file, "images");
            
            Map<String, String> result = new HashMap<>();
            result.put("url", url);
            result.put("filename", file.getOriginalFilename());
            result.put("size", String.valueOf(file.getSize()));
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("图片上传失败", e);
            return Result.error(500, "图片上传失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "批量上传图片")
    @PostMapping("/images")
    public Result<List<Map<String, String>>> uploadImages(@RequestParam("files") MultipartFile[] files) {
        try {
            if (files == null || files.length == 0) {
                return Result.error(400, "请选择要上传的文件");
            }
            
            if (files.length > 9) {
                return Result.error(400, "最多只能上传9张图片");
            }
            
            List<Map<String, String>> results = new ArrayList<>();
            
            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    continue;
                }
                
                // 验证文件类型
                String contentType = file.getContentType();
                if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
                    continue;
                }
                
                // 验证文件大小
                if (file.getSize() > MAX_IMAGE_SIZE) {
                    continue;
                }
                
                // 保存文件
                String url = saveFile(file, "images");
                
                Map<String, String> result = new HashMap<>();
                result.put("url", url);
                result.put("filename", file.getOriginalFilename());
                result.put("size", String.valueOf(file.getSize()));
                
                results.add(result);
            }
            
            return Result.success(results);
            
        } catch (Exception e) {
            log.error("批量上传图片失败", e);
            return Result.error(500, "批量上传图片失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "上传视频")
    @PostMapping("/video")
    public Result<Map<String, Object>> uploadVideo(@RequestParam("file") MultipartFile file) {
        try {
            // 验证文件
            if (file.isEmpty()) {
                return Result.error(400, "文件不能为空");
            }
            
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !ALLOWED_VIDEO_TYPES.contains(contentType.toLowerCase())) {
                return Result.error(400, "不支持的视频格式，仅支持 MP4、AVI、MOV、WMV");
            }
            
            // 验证文件大小
            if (file.getSize() > MAX_VIDEO_SIZE) {
                return Result.error(400, "视频大小不能超过100MB");
            }
            
            // 保存文件
            String url = saveFile(file, "videos");
            
            // 使用视频URL作为缩略图（浏览器会自动显示第一帧）
            String thumbnail = url;
            
            Map<String, Object> result = new HashMap<>();
            result.put("url", url);
            result.put("thumbnail", thumbnail);
            result.put("filename", file.getOriginalFilename());
            result.put("size", file.getSize());
            result.put("duration", 0); // 实际项目中应该解析视频获取时长
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("视频上传失败", e);
            return Result.error(500, "视频上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 保存文件到本地
     */
    private String saveFile(MultipartFile file, String subDir) throws IOException {
        // 创建日期目录 (例如: uploads/images/2026/01/03/)
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String fullDir = uploadPath + File.separator + subDir + File.separator + dateDir;
        
        Path dirPath = Paths.get(fullDir);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }
        
        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + extension;
        
        // 保存文件
        Path filePath = dirPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath);
        
        // 返回访问URL
        String url = String.format("http://localhost:%s/uploads/%s/%s/%s", 
            serverPort, subDir, dateDir, filename);
        
        log.info("文件上传成功: {}", url);
        return url;
    }
}
