package com.jiyi.social.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Value("${file.upload.path:uploads}")
    private String uploadPath;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置上传文件的访问路径
        String absolutePath = new File(uploadPath).getAbsolutePath();
        // 确保路径以分隔符结尾
        if (!absolutePath.endsWith(File.separator)) {
            absolutePath += File.separator;
        }
        
        System.out.println("=== 静态资源配置 ===");
        System.out.println("上传路径配置: " + uploadPath);
        System.out.println("绝对路径: " + absolutePath);
        System.out.println("映射URL: /uploads/**");
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + absolutePath)
                .setCachePeriod(0); // 禁用缓存，方便调试
    }
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 配置CORS，允许前端访问静态资源和API
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
