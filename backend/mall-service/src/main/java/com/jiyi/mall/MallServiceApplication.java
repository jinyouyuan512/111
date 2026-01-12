package com.jiyi.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 商城服务启动类
 * 
 * @author Jiyi Red Route Team
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.jiyi.mall.mapper")
public class MallServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(MallServiceApplication.class, args);
        System.out.println("========================================");
        System.out.println("商城服务启动成功！");
        System.out.println("API文档地址: http://localhost:8085/doc.html");
        System.out.println("========================================");
    }
}
