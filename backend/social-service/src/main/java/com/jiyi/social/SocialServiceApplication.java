package com.jiyi.social;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 社交服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
public class SocialServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SocialServiceApplication.class, args);
    }
}
