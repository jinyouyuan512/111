package com.jiyi.user.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置头像上传目录的静态资源映射
        String uploadPath = System.getProperty("user.dir") + "/uploads/";
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath);
        
        System.out.println("=== 静态资源配置 ===");
        System.out.println("上传路径: " + uploadPath);
        System.out.println("映射URL: /uploads/**");
    }
}
